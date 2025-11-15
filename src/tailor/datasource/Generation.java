package tailor.datasource;

import java.util.ArrayList;
import java.util.List;

import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;

/**
 * Generate protein structures from phi/psi angles
 */
public class Generation {
    
    // Bond distances
    public static final double N_CA_DISTANCE = 1.47;
    public static final double CA_C_DISTANCE = 1.53;
    public static final double C_N_DISTANCE = 1.32;
    public static final double C_O_DISTANCE = 1.24;
    
    // Bond angles
    public static final double N_CA_C_ANGLE = 112;
    public static final double CA_C_O_ANGLE = 121;
    public static final double C_N_CA_ANGLE = 123;
    public static final double CA_C_N_ANGLE = 114;
    public static final double O_C_N_ANGLE = 125;
    
    // Common conformations
    public static final PhiPsi HELIX = new PhiPsi(-57, -47);
    public static final PhiPsi PARALLEL = new PhiPsi(-119, 113);
    public static final PhiPsi ANTIPARALLEL = new PhiPsi(-139, 135);
    public static final PhiPsi PP_II = new PhiPsi(-70, 150);
    
    public static final List<PhiPsi> TYPE1_TURN = List.of(
        new PhiPsi(-60, -30), 
        new PhiPsi(-90, 0)
    );
    
    public static final List<PhiPsi> TYPE2_TURN = List.of(
        new PhiPsi(-60, 120), 
        new PhiPsi(80, 0)
    );
    
    public static final List<PhiPsi> TYPE1_PRIME_TURN = List.of(
        new PhiPsi(60, 30), 
        new PhiPsi(90, 0)
    );
    
    public static final List<PhiPsi> TYPE2_PRIME_TURN = List.of(
        new PhiPsi(60, -120), 
        new PhiPsi(-90, 0)
    );
    
    public static final List<PhiPsi> CATBOX = List.of(
        new PhiPsi(-90, 0), 
        new PhiPsi(-90, 180)
    );
    
    /**
     * Extends an existing chain with residues based on phi/psi angles
     */
    public static void extendChain(List<PhiPsi> phiPsiList, Double lastPsi, Chain chain) {
        int numberOfResidues = phiPsiList.size();
        Group lastResidue;
        double currentLastPsi;
        
        if (chain == null) {
            chain = new Chain("A");
            PhiPsi firstAngles = phiPsiList.get(0);
            Group nTerminus = makeNTerminalResidue(firstAngles.psi);
            chain.addGroup(nTerminus);
            lastResidue = nTerminus;
            currentLastPsi = firstAngles.psi;
        } else {
            lastResidue = chain.getLast();
            PhiPsi firstAngles = phiPsiList.get(0);
            Group firstResidue = makeResidue(lastResidue, firstAngles.phi, firstAngles.psi, lastPsi);
            chain.addGroup(firstResidue);
            lastResidue = firstResidue;
            currentLastPsi = firstAngles.psi;
        }
        
        for (int i = 1; i < numberOfResidues; i++) {
            PhiPsi angles = phiPsiList.get(i);
            Group residue = makeResidue(lastResidue, angles.phi, angles.psi, currentLastPsi);
            chain.addGroup(residue);
            lastResidue = residue;
            currentLastPsi = angles.psi;
        }
    }
    
    /**
     * Creates a protein fragment from a list of phi/psi angles
     */
    public static Chain makeFragment(List<PhiPsi> phiPsiList) {
        Vector c_pos = new Vector(0, 0, 0);
        Vector o_pos = new Vector(C_O_DISTANCE, 0, 0);
        Vector ca_pos = Geometry.makeXYZFromAngle(o_pos, c_pos, CA_C_DISTANCE, CA_C_O_ANGLE, Geometry.NEG_Y);
        
        Group nCap = new Group(1, "GLY");
        nCap.addAtom(new Atom("CA", ca_pos));
        nCap.addAtom(new Atom("C", c_pos));
        nCap.addAtom(new Atom("O", o_pos));
        
        PhiPsi firstAngles = phiPsiList.get(0);
        double phi = firstAngles.phi;
        double psi = firstAngles.psi;
        double N_CA_C_O_torsion = Geometry.invertTorsion(psi);
        
        Vector n_pos = Geometry.makeXYZFromAngle(o_pos, c_pos, C_N_DISTANCE, O_C_N_ANGLE, Geometry.Y);
        ca_pos = Geometry.makeXYZ(n_pos, N_CA_DISTANCE, c_pos, C_N_CA_ANGLE, o_pos, 0);
        c_pos = Geometry.makeXYZ(ca_pos, CA_C_DISTANCE, n_pos, N_CA_C_ANGLE, c_pos, phi);
        o_pos = Geometry.makeXYZ(c_pos, C_O_DISTANCE, ca_pos, CA_C_O_ANGLE, n_pos, N_CA_C_O_torsion);
        
        Group firstResidue = new Group(2, "GLY");
        firstResidue.addAtom(new Atom("N", n_pos));
        firstResidue.addAtom(new Atom("CA", ca_pos));
        firstResidue.addAtom(new Atom("C", c_pos));
        firstResidue.addAtom(new Atom("O", o_pos));
        
        Chain chain = new Chain("A");
        chain.addGroup(nCap);
        chain.addGroup(firstResidue);
        
        if (phiPsiList.size() > 1) {
            extendChain(phiPsiList.subList(1, phiPsiList.size()), psi, chain);
        }
        
        // Add C-terminal cap
        Group last = chain.getLast();
        double lastPsi = phiPsiList.get(phiPsiList.size() - 1).psi;
        
        n_pos = last.getAtomPosition("N");
        ca_pos = last.getAtomPosition("CA");
        c_pos = last.getAtomPosition("C");
        o_pos = last.getAtomPosition("O");
        
        n_pos = Geometry.makeXYZ(c_pos, C_N_DISTANCE, ca_pos, CA_C_N_ANGLE, n_pos, lastPsi);
        Group cCap = new Group(last.getNumber() + 1, "GLY");
        cCap.addAtom(new Atom("N", n_pos));
        chain.addGroup(cCap);
        
        return chain;
    }
    
    /**
     * Creates an N-terminal residue
     */
    public static Group makeNTerminalResidue(double psi) {
        double N_CA_C_O_torsion = Geometry.invertTorsion(psi);
        Vector nitrogen = new Vector(0.0, 0.0, 0.0);
        Vector cAlpha = new Vector(N_CA_DISTANCE, 0.0, 0.0);
        Vector carbonylCarbon = Geometry.makeXYZFromAngle(nitrogen, cAlpha, CA_C_DISTANCE, N_CA_C_ANGLE, Geometry.Y);
        Vector oxygen = Geometry.makeXYZ(carbonylCarbon, C_O_DISTANCE, cAlpha, CA_C_O_ANGLE, nitrogen, N_CA_C_O_torsion);
        
        Group r = new Group(1, "GLY");
        r.addAtom(new Atom("N", nitrogen));
        r.addAtom(new Atom("CA", cAlpha));
        r.addAtom(new Atom("C", carbonylCarbon));
        r.addAtom(new Atom("O", oxygen));
        
        return r;
    }
    
    /**
     * Creates a residue based on the previous residue and phi/psi angles
     */
    public static Group makeResidue(Group previousResidue, double phi, double psi, double lastPsi) {
        Vector previousNitrogen = previousResidue.getAtomPosition("N");
        Vector previousCAlpha = previousResidue.getAtomPosition("CA");
        Vector previousC = previousResidue.getAtomPosition("C");
        double N_CA_C_O_torsion = Geometry.invertTorsion(psi);
        
        Vector nitrogen = Geometry.makeXYZ(previousC, C_N_DISTANCE, previousCAlpha, CA_C_N_ANGLE, previousNitrogen, lastPsi);
        Vector cAlpha = Geometry.makeXYZ(nitrogen, N_CA_DISTANCE, previousC, C_N_CA_ANGLE, previousCAlpha, 180);
        Vector carbonylCarbon = Geometry.makeXYZ(cAlpha, CA_C_DISTANCE, nitrogen, N_CA_C_ANGLE, previousC, phi);
        Vector oxygen = Geometry.makeXYZ(carbonylCarbon, C_O_DISTANCE, cAlpha, CA_C_O_ANGLE, nitrogen, N_CA_C_O_torsion);
        
        int residueNumber = previousResidue.getNumber() + 1;
        Group r = new Group(residueNumber, "GLY");
        r.addAtom(new Atom("N", nitrogen));
        r.addAtom(new Atom("CA", cAlpha));
        r.addAtom(new Atom("C", carbonylCarbon));
        r.addAtom(new Atom("O", oxygen));
        
        return r;
    }
    
    /**
     * Converts a chain to PDB format string
     */
    public static String toPDB(Chain chain) {
        List<String> records = new ArrayList<>();
        int atomnum = 1;
        String recordFormat = "ATOM %6d  %-3s %s %5d     %7.3f %7.3f %7.3f";
        
        for (Group residue : chain.getGroups()) {
            for (Atom atom : residue.getAtoms()) {
                Vector p = atom.getCenter();
                String record = String.format(recordFormat, 
                    atomnum, atom.getName(), residue.getName(), 
                    residue.getNumber(), p.x(), p.y(), p.z());
                records.add(record);
                atomnum++;
            }
        }
        
        return String.join("\n", records);
    }
    
    public static void main(String[] args) {
        Chain ctbx = makeFragment(CATBOX);
        System.out.println(toPDB(ctbx));
    }
}