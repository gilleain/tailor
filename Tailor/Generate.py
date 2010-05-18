from Geometry import Vector, makeXYZ, makeXYZFromAngle, invertTorsion, O, X, Y, Z
from Feature import Structure, Model, Chain, Residue, Atom

N_CA_distance = 1.47
CA_C_distance = 1.53
C_N_distance = 1.32
C_O_distance = 1.24

N_CA_C_angle = 112  
CA_C_O_angle = 121
C_N_CA_angle = 123
CA_C_N_angle = 114
O_C_N_angle = 125

helix = (-57, -47)
parallel = (-119, 113)
antiparallel = (-139, 135)
pp_II = (-70, 150)
type1Turn = [(-60, -30), (-90, 0)]
type2Turn = [(-60, 120), (80, 0)]
type1PrimeTurn = [(60, 30), (90, 0)]
type2PrimeTurn = [(60, -120), (-90, 0)]
catbox = [(-90, 0), (-90, 180)]

def extendChain(phiPsiList, lastPsi=None, chain=None):
    numberOfResidues = len(phiPsiList)
    if chain is None:
        chain = Chain("A") 
        (phi, psi) = phiPsiList[0]
        nTerminus = makeNTerminalResidue(psi)
        chain.add(nTerminus)
        lastResidue = nTerminus
        lastPsi = psi
    else:
        lastResidue = chain.subFeatures[-1]
        (phi, psi) = phiPsiList[0]
        firstResidue = makeResidue(lastResidue, phi, psi, lastPsi)
        chain.add(firstResidue)
        lastResidue = firstResidue
        lastPsi = psi

    for i in range(1, numberOfResidues):
        (phi, psi) = phiPsiList[i]
        residue = makeResidue(lastResidue, phi, psi, lastPsi)
        chain.add(residue)
        lastResidue = residue
        lastPsi = psi

def makeFragment(phiPsiList):
    c_pos = Vector(0, 0, 0)
    o_pos = Vector(C_O_distance, 0, 0)
    ca_pos = makeXYZFromAngle(o_pos, c_pos, CA_C_distance, CA_C_O_angle, -Y)
    nCap = Residue(1, "GLY") 
    nCap.subFeatures = [Atom("CA", ca_pos), Atom("C", c_pos), Atom("O", o_pos)]

    (phi, psi) = phiPsiList[0]
    N_CA_C_O_torsion = invertTorsion(psi)

    n_pos = makeXYZFromAngle(o_pos, c_pos, C_N_distance, O_C_N_angle, Y)
    ca_pos = makeXYZ(n_pos, N_CA_distance, c_pos, C_N_CA_angle, o_pos, 0)
    c_pos = makeXYZ(ca_pos, CA_C_distance, n_pos, N_CA_C_angle, c_pos, phi)
    o_pos = makeXYZ(c_pos, C_O_distance, ca_pos, CA_C_O_angle, n_pos, N_CA_C_O_torsion)
    firstResidue = Residue(2, "GLY")
    firstResidue.subFeatures = [Atom("N", n_pos), Atom("CA", ca_pos), Atom("C", c_pos), Atom("O", o_pos)]

    chain = Chain("A")
    chain.add(nCap)
    chain.add(firstResidue)
    extendChain(phiPsiList[1:], psi, chain)
    
    last = chain.subFeatures[-1]
    lastPsi = phiPsiList[-1][1]
    n_pos = last.getAtomPosition("N")
    ca_pos = last.getAtomPosition("CA")
    c_pos = last.getAtomPosition("C")
    o_pos = last.getAtomPosition("O")
    n_pos = makeXYZ(c_pos, C_N_distance, ca_pos, CA_C_N_angle, n_pos, lastPsi)
    cCap = Residue(last.number + 1, "GLY")
    cCap.subFeatures = [Atom("N", n_pos)]
    chain.add(cCap)

    return chain

def makeNTerminalResidue(psi):
    N_CA_C_O_torsion = invertTorsion(psi)
    nitrogen = Vector(0.0, 0.0, 0.0) 
    cAlpha = Vector(N_CA_distance, 0.0, 0.0)
    carbonylCarbon = makeXYZFromAngle(nitrogen, cAlpha, CA_C_distance, N_CA_C_angle, Y)
    oxygen = makeXYZ(carbonylCarbon, C_O_distance, cAlpha, CA_C_O_angle, nitrogen, N_CA_C_O_torsion)

    r = Residue(1, "GLY")
    r.subFeatures = [Atom("N", 1, nitrogen), Atom("CA", 2, cAlpha), Atom("C", 3, carbonylCarbon), Atom("O", 4, oxygen)]
    return r

def makeResidue(previousResidue, phi, psi, lastPsi):
    previousNitrogen = previousResidue.getAtomPosition("N")
    previousCAlpha = previousResidue.getAtomPosition("CA")
    previousC = previousResidue.getAtomPosition("C")
    N_CA_C_O_torsion = invertTorsion(psi)

    nitrogen = makeXYZ(previousC, C_N_distance, previousCAlpha, CA_C_N_angle, previousNitrogen, lastPsi)
    cAlpha = makeXYZ(nitrogen, N_CA_distance, previousC, C_N_CA_angle, previousCAlpha, 180)
    carbonylCarbon = makeXYZ(cAlpha, CA_C_distance, nitrogen, N_CA_C_angle, previousC, phi)
    oxygen = makeXYZ(carbonylCarbon, C_O_distance, cAlpha, CA_C_O_angle, nitrogen, N_CA_C_O_torsion)

    residueNumber = previousResidue.number + 1
    r = Residue(residueNumber, "GLY")
    r.subFeatures = [Atom("N", nitrogen), Atom("CA", cAlpha), Atom("C", carbonylCarbon), Atom("O", oxygen)]
    return r

def to_pdb(chain):
    records = []
    atomnum = 1
    record_format = "ATOM %6i  %-3s %s %5i     %7.3f %7.3f %7.3f"
    for residue in chain:
        for atom in residue:
            p = atom.getCenter()
            records.append(record_format % (atomnum, atom.name, residue.resname, residue.number, p.x, p.y, p.z))
    return "\n".join(records)

if __name__ == "__main__":
    ctbx = makeFragment(catbox)
    print to_pdb(ctbx)
    #for residue in ctbx:
    #    print residue
    #    for atom in residue:
    #        print atom, atom.coord
