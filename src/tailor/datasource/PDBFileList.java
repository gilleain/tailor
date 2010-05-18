package tailor.datasource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class PDBFileList implements StructureSource {
	
	private File path;
	private ArrayList<File> structurePaths;
    private int currentIndex;

    public PDBFileList(File file, String[] filenames) throws IOException {
    	this(file.toString(), filenames);
    }
    
    /**
	 * If the path parameter is a file, the filenames array is ignored.
	 * If the path is instead a directory, then either use the filenames
	 * in the filenames array or list the contents of the directory.
	 * 
	 * @param path the directory or file to use
	 * @param filenames an array of filenames
	 */
	public PDBFileList(String path, String[] filenames) throws IOException {
		if (filenames == null) {
			filenames = new String[0];
		}
		this.path = new File(path);
		if (this.path.exists()) {
			this.structurePaths = new ArrayList<File>();
			if (this.path.isDirectory()) {
				if (filenames.length == 0) {
					filenames = this.path.list();
					if (filenames.length == 0) {
						throw new IOException("No files in directory " + this.path);
					}
				} 
				for (String filename : filenames) {
					// FIXME : quick hack to exclude .svn files
					if (filename.startsWith(".")) { continue; }
					File currentPath = new File(this.path, filename);
					this.structurePaths.add(currentPath);
				}
			} else {
				this.structurePaths.add(this.path);
			}
		} else {
			throw new IOException("Path " + this.path.toString() + " does not exist");
		}
        this.currentIndex = 0;
	}
	
	public ArrayList<File> getStructurePaths() {
		return this.structurePaths;
	}
    
    public Structure next() throws IOException {
        Structure next = PDBReader.read(this.structurePaths.get(this.currentIndex));
        this.currentIndex++;
        return next;
    }
    
    public boolean hasNext() {
        if (this.currentIndex < this.structurePaths.size()) {
            return true;
        }
        return false;
    }
    
    public int size() {
        return this.structurePaths.size();
    }
	
	public String toString() {
		return this.path.toString();
	}

}
