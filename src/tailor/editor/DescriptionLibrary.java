package tailor.editor;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import tailor.datasource.XmlDescriptionReader;
import tailor.description.Description;

public class DescriptionLibrary {
	
	private static final String[] defaultLibraryPaths = {
		"descriptions/helix_three_ten.xml",
		"descriptions/helix_alpha.xml",
		"descriptions/hairpin_turn.xml",
		"descriptions/schellman_loop.xml"
	};
	
	private ArrayList<Description> descriptions;
	
	/**
	 * Read all the ".xml" files from {@code directory} and turn them into Descriptions.
	 * 
	 * @param directoryName where to look for files.
	 */
	public DescriptionLibrary(String directoryName) {
		this.descriptions = new ArrayList<Description>();
		XmlDescriptionReader reader = new XmlDescriptionReader();
		File directory = new File(directoryName);
		if (directory.exists()) {
			String[] fileList = directory.list();
			for (String fileName : fileList) {
				if (fileName.endsWith(".xml")) {
					File f = new File(directoryName, fileName);
					System.out.println(f);
					Description d = reader.readDescription(f);
					this.descriptions.add(d);
				}
			}
		}
	}
	
	public ArrayList<Description> getDescriptions() {
		return this.descriptions;
	}
	
	/**
	 * Read in the Descriptions from the jar files containing the classes. 
	 * 
	 * @return an ArrayList of Descriptions from the standard library.
	 */
	public static ArrayList<Description> getDefaultLibrary() {
		try {
			ArrayList<Description> defaultLib = new ArrayList<Description>();
			
			ClassLoader loader = DescriptionLibrary.class.getClassLoader();
			XmlDescriptionReader reader = new XmlDescriptionReader();
			
			for (String filename : DescriptionLibrary.defaultLibraryPaths) {
				InputStream stream = loader.getResourceAsStream(filename);
				defaultLib.add(reader.readDescription(stream));
			}
			
			return defaultLib;
		} catch (SecurityException s) {
			System.out.println("Security Exception!");
			return null;
		}
	}
	
	public static void main(String[] args) {
//		ArrayList<Description> lib = DescriptionLibrary.getDefaultLibrary();
		ArrayList<Description> lib = new DescriptionLibrary("descriptions").getDescriptions();
		for (Description d : lib) {
			System.out.println(d);
		}
	}

}
