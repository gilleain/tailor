package tailor.translation;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import tailor.datasource.PDBReader;
import tailor.structure.Protein;

public class TestHBondAnalyser {

	@Test
	public void test() {
		String pdbFilename        = "";
		String propertiesFilename = "";

		try {
			HBondAnalyser hBondAnalyser = new HBondAnalyser();
			hBondAnalyser.loadProperties(new FileInputStream(propertiesFilename));
			hBondAnalyser.storeProperties(System.err);

			Protein protein = PDBReader.read(pdbFilename);

			hBondAnalyser.analyse(protein);
			System.out.println(protein);
		} catch (IOException ioe) {
			System.err.println(ioe);
		} catch (PropertyException pe) {
			System.err.println(pe);
		}
	}

}
