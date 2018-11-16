package tailor.editor;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tailor.datasource.xml.XmlDescriptionReader;
import tailor.description.Description;

public class DescriptionLibrary {
	
	private static final String[] defaultLibraryPaths = {
		"descriptions/helix_three_ten.xml",
		"descriptions/helix_alpha.xml",
		"descriptions/hairpin_turn.xml",
		"descriptions/schellman_loop.xml"
	};
	
	private List<Description> descriptions;
	
	/**
	 * Read all the ".xml" files from {@code directory} and turn them into Descriptions.
	 * 
	 * @param directoryName where to look for files.
	 */
	public DescriptionLibrary(String directoryName) {
		this.descriptions = new ArrayList<>();
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
	
	public List<Description> getDescriptions() {
		return this.descriptions;
	}
	
	/**
	 * Read in the Descriptions from the jar files containing the classes. 
	 * 
	 * @return an ArrayList of Descriptions from the standard library.
	 */
	public static List<Description> getDefaultLibrary() {
		try {
			List<Description> defaultLib = new ArrayList<>();
			
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
//		List<Description> lib = DescriptionLibrary.getDefaultLibrary();
		List<Description> lib = new DescriptionLibrary("descriptions").getDescriptions();
		for (Description d : lib) {
			System.out.println(d);
		}
	}

}
