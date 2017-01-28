package tailor.source;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;

import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Protein;

public class TestPDBWriter {
    
    @Test
    public void singleAtomTest() throws IOException {
        String atomName = "2HG1";
        String residueName = "VAL";
        int residueNumber = 146;
        Vector coords = new Vector("-1.167  26.492  10.182");
        Atom atom = new Atom(atomName, coords);
        
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        PDBWriter.write(atom, 2236, residueName, residueNumber, out);
        out.flush();
        
        String result = stringWriter.toString();
        assertEquals("ATOM   2236 2HG1 VAL   146      -1.167  26.492  10.182", result);
    }
    
    @Test
    public void singleChainTest() throws IOException {
        Chain chain = new Chain();
        int numberOfResidues = 10;
        for (int index = 0; index < numberOfResidues; index++) {
            Group group = new Group();
            group.setNumber(index);
            group.setId("GLY");
            group.addAtom(randomAtom("N"));
            group.addAtom(randomAtom("CA"));
            chain.addGroup(group);
        }
        Protein protein = new Protein("1TST");
        protein.addChain(chain);
        String written = writeToString(protein);
        
        BufferedReader reader = new BufferedReader(new StringReader(written));
        int counter = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            counter++;
        }
        assertEquals(numberOfResidues * 2, counter);
    }
    
    private String writeToString(Protein protein) throws IOException {
        StringWriter stringWriter = new StringWriter();
        BufferedWriter out = new BufferedWriter(stringWriter);
        PDBWriter.write(protein, out);
        out.flush();
        return stringWriter.toString();
    }
    
    /**
     * Guaranteed to be random, chosen by die roll.
     */
    private Atom randomAtom(String atomName) {
        Vector coords = new Vector("-1.167  26.492  10.182");
        return new Atom(atomName, coords);
    }

}
