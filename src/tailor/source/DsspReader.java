package tailor.source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tailor.geometry.Vector;
import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Helix;
import tailor.structure.SSE;
import tailor.structure.Strand;
import tailor.structure.Structure;

public class DsspReader {
    
    public static Structure read(File path) throws IOException {
        String pdbID = path.getName().substring(0, 4);  // this is a hack...

        List<String> records = getRecords(new BufferedReader(new FileReader(path)));
        Chain chain = new Chain();
        String previousStructure = null;
        SSE currentSSE = null;
        for (String record : records) {
            Group group = parseGroup(record);
            chain.addGroup(group);
            String structure = parseStructure(record);
            if (previousStructure == null || !previousStructure.equals(structure)) {
                currentSSE = makeSSE(structure);
                if (currentSSE != null) {
                    chain.addSSE(currentSSE);
                }
            }
            
            if (currentSSE != null) {
                currentSSE.addGroup(group);
            }
            previousStructure = structure;
        }
        
        return chain;
    }
    
    private static SSE makeSSE(String structure) {
        switch (structure) {
            case "E": return new Strand(); // hmmm
            case "H": return new Helix(); // hmmm
            default: return null;
        }
    }

    private static String parseStructure(String record) {
        return record.substring(16, 17);
    }

    private static Group parseGroup(String record) {
        Group group = new Group();
        String pdbNumber = record.substring(5, 10).trim();
        group.setId(pdbNumber);
        group.addAtom(parseAtom(record));
        return group;
    }

    private static Atom parseAtom(String record) {
        double xca = Double.parseDouble(record.substring(115, 122).trim());
        double yca = Double.parseDouble(record.substring(122, 129).trim());
        double zca = Double.parseDouble(record.substring(129).trim());
        Atom atom = new Atom("CA", new Vector(xca, yca, zca));
        return atom;
    }

    private static List<String> getRecords(BufferedReader bufferer) {
        String line;
        List<String> records = new ArrayList<>();
        try {
            int lineCounter = 0;
            while ((line = bufferer.readLine()) != null) {
                lineCounter++;
                if (lineCounter < 28) {
                    continue;
                } else {
                    records.add(line);
                }
            }
        } catch (IOException ioe) {
            
        } finally {
            try {
                bufferer.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        return records;
    }

}
