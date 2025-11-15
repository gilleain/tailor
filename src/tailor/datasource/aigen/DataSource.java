package tailor.datasource.aigen;

import java.io.IOException;

import tailor.structure.Structure;

public class DataSource {
    
    /**
     * Creates a structure from a file path
     */
    public static Structure structureFromFile(String filepath) throws IOException {
        PDBParser parser = new PDBParser(new StructureBuilder());
        return parser.getStructure(filepath);
    }
}

