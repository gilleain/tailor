package aigen.run;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import tailor.datasource.aigen.PDBFileList;

/**
 * Main runner class that processes structures through a pipeline
 */
public class Run {
    private List<Pipe> pipes;
    private String structurePath;
    private List<String> structureList;
    
    public Run(List<Pipe> pipes, String structurePath) {
        this(pipes, structurePath, new ArrayList<>());
    }
    
    public Run(List<Pipe> pipes, String structurePath, List<String> structureList) {
        this.pipes = pipes;
        this.structurePath = structurePath;
        this.structureList = structureList;
    }
    
    /**
     * Runs the pipeline on all structures
     */
    public void run() {
        run(System.err);
    }
    
    public void run(PrintStream err) {
        try {
            // Start with this to catch IO errors before writing anything
            PDBFileList structures = new PDBFileList(this.structurePath, this.structureList);
            
            // Write headers for all pipes
            for (Pipe pipe : this.pipes) {
                pipe.writeHeader();
            }
            
            // Process each structure
            for (tailor.structure.Structure structure : structures) {
                err.write(("Processing : " + structure).getBytes());
                
//                try {
                    for (Pipe pipe : this.pipes) {
                        err.write((" " + pipe.getDescription().getName()).getBytes());
                        
//                        pipe.run(structure); // XXX TODO
                    }
//                } catch (DescriptionException d) {
//                    err.println();
//                    err.println("Description problem " + d.getMessage());
//                }
                err.println();
            }
            
        } catch (IOException ioe) {
            err.println(ioe.getMessage());
            return;
        }
    }
}
