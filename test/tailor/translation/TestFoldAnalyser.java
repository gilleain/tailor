package tailor.translation;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;

import tailor.datasource.PDBReader;
import tailor.datasource.TopsWriter;
import tailor.structure.Protein;

public class TestFoldAnalyser {

	@Test
	public void test() {

		String pdbFilename = "";
		String cathDomainFilename = "";
		String logLevelString = "";

		try {
			Protein protein = PDBReader.read(pdbFilename);

			Logger packageLevelLogger = Logger.getLogger(FoldAnalyser.class.getCanonicalName());
			Level logLevel = Level.parse(logLevelString);
			packageLevelLogger.setLevel(logLevel);
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new Formatter() { public String format(LogRecord record) { return record.getMessage() + "\n"; } } );
			packageLevelLogger.addHandler(consoleHandler);
			packageLevelLogger.setUseParentHandlers(false);

			FoldAnalyser foldAnalyser = new FoldAnalyser();
			foldAnalyser.analyse(protein);

			System.out.println(protein);

			ChainDomainMap cathChainDomainMap = 
					CATHDomainFileParser.parseUpToParticularID(cathDomainFilename, protein.getID());
			Map<String, Map<String, String>> chainDomainStringMap = 
					TopsWriter.toTopsDomainStrings(protein, cathChainDomainMap);

			Iterator<String> itr = chainDomainStringMap.keySet().iterator();
			while (itr.hasNext()) {
				String chainID = itr.next();
				Map<String, String> domainStrings = chainDomainStringMap.get(chainID);
				for (String domainString : domainStrings.keySet()) {
					System.out.println(protein.getID() + domainString);
				}
			}

		} catch (IOException ioe) {
			System.err.println(ioe);
		} catch (PropertyException pe) {
			System.err.println(pe);
		}

	}

}
