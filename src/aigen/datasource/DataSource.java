package aigen.datasource;

import java.io.IOException;

public class DataSource {
    
    /**
     * Creates a structure from a file path
     */
    public static Structure structureFromFile(String filepath) throws IOException {
        PDBParser parser = new PDBParser(new StructureBuilder());
        return parser.getStructure(filepath);
    }
}

