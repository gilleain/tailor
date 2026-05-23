package tailor.translation;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import tailor.datasource.PDBReader;
import tailor.datasource.TopsWriter;
import tailor.structure.Protein;

public class TestStructureFinder {
	
	@Test
	public void test() {
		String filename = "";
		String cathFilename = "";
		try {
			Protein protein = PDBReader.read(filename);
			StructureFinder structureFinder = new StructureFinder(protein);
			protein = structureFinder.getProtein();
			System.out.println(protein.toString());

			ChainDomainMap cathChainDomainMap = 
					CATHDomainFileParser.parseUpToParticularID(cathFilename, protein.getID());
			Map<String, Map<String, String>> chainDomainStringMap = 
					TopsWriter.toTopsDomainStrings(protein, cathChainDomainMap);

			for (String chainID : chainDomainStringMap.keySet()) {
				Map<String, String> domainStrings = chainDomainStringMap.get(chainID);
				for (String domainString : domainStrings.keySet()) {
					System.out.println(protein.getID() + domainString);
				}
			}

		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

}
