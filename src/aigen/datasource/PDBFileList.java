package aigen.datasource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PDBFileList implements Iterable<Structure> {
    private String path;
    private Map<String, String> pathMap;
    private List<String> structurePaths;
    private PDBParser parser;
    
    public PDBFileList(String path) throws IOException {
        this(path, new ArrayList<>());
    }
    
    public PDBFileList(String path, List<String> structureList) throws IOException {
        this.path = path;
        this.pathMap = new HashMap<>();
        this.structurePaths = new ArrayList<>();
        
        File file = new File(path);
        
        if (file.isDirectory()) {
            if (structureList.isEmpty()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String filename = f.getName();
                        if (filename.startsWith(".")) continue; // avoid dotfiles (eg .svn dirs)
                        String fullpath = f.getAbsolutePath();
                        mapFilename(filename, fullpath);
                        this.structurePaths.add(fullpath);
                    }
                }
            } else {
                System.out.println(structureList);
                File[] filesInDir = file.listFiles();
                
                if (filesInDir == null || filesInDir.length == 0) {
                    throw new IOException("WARNING: No files in directory " + path);
                }
                
                List<String> filenamesInDir = new ArrayList<>();
                for (File f : filesInDir) {
                    filenamesInDir.add(f.getName());
                }
                
                for (String filename : structureList) {
                    if (filenamesInDir.contains(filename)) {
                        String fullpath = new File(file, filename).getAbsolutePath();
                        mapFilename(filename, fullpath);
                        this.structurePaths.add(fullpath);
                    } else {
                        throw new IOException("WARNING: File " + filename + " is not in directory " + path);
                    }
                }
            }
        } else if (file.isFile()) {
            this.structurePaths.add(path);
        } else {
            throw new IOException("WARNING: The path " + path + " is not a directory or a file - it may not exist!");
        }
        
        this.parser = new PDBParser(new StructureBuilder());
    }
    
    private void mapFilename(String filename, String fullpath) {
        // Take the first four letters of a filename
        String id = filename.length() >= 4 ? filename.substring(0, 4) : filename;
        this.pathMap.put(id, fullpath);
    }
    
    @Override
    public Iterator<Structure> iterator() {
        return new Iterator<Structure>() {
            private int currentIndex = 0;
            
            @Override
            public boolean hasNext() {
                return currentIndex < structurePaths.size();
            }
            
            @Override
            public Structure next() {
                try {
                    String path = structurePaths.get(currentIndex);
                    currentIndex++;
                    return parser.getStructure(path);
                } catch (IOException ioe) {
                    throw new RuntimeException("I/O problem with path [" + 
                        structurePaths.get(currentIndex - 1) + "] " + ioe.getMessage(), ioe);
                }
            }
        };
    }
    
    public Structure get(String key) throws IOException {
        String filepath = this.pathMap.get(key);
        if (filepath == null) {
            throw new IllegalArgumentException("No structure found for key: " + key);
        }
        return this.parser.getStructure(filepath);
    }
    
    @Override
    public String toString() {
        return this.path;
    }
}
