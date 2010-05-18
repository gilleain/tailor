package tailor.datasource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import tailor.Level;
import tailor.condition.Condition;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.Description;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;

public class XmlDescriptionWriter {
	
	public static void writeToFile(File file, Description description) throws IOException {
		FileWriter writer = new FileWriter(file);
		XmlDescriptionWriter.writeDescription(description, writer);
		writer.flush();
	}
	
	private static void writeDescription(Description description, FileWriter writer) 
						throws IOException {
		Level level = description.getLevel(); 
		if (level == Level.PROTEIN) {
			ProteinDescription p = (ProteinDescription) description; 
			writer.write(String.format("<ProteinDescription name=\"%s\">\n", p.getName()));
			for (ChainDescription chain : p.getChainDescriptions()) {
				XmlDescriptionWriter.writeDescription(chain, writer);
			}
			writer.write("</ProteinDescription>\n");
		} else if (level == Level.CHAIN) {
			ChainDescription c = (ChainDescription) description;
			writer.write("\t");
			writer.write(String.format("<ChainDescription name=\"%s\">\n", c.getName()));
			for (GroupDescription group : c.getGroupDescriptions()) {
				XmlDescriptionWriter.writeDescription(group, writer);
			}
			for (Condition condition : c.getConditions()) {
				XmlDescriptionWriter.writeCondition(condition, writer);
			}
			writer.write("\t</ChainDescription>\n");
		} else if (level == Level.RESIDUE) {
			GroupDescription g = (GroupDescription) description;
			writer.write("\t\t");
			String name = g.getName();
			String out;
			int offset = g.getOffset();
			if (name == null) {
				out = String.format("" +
						"<GroupDescription name=\"%s\" position=\"%s\">","*", offset);
			} else {
				out = String.format(
						"<GroupDescription name=\"%s\" position=\"%s\">", name, offset);
			}
			writer.write(out + "\n");
			for (AtomDescription atom : g.getAtomDescriptions()) {
				XmlDescriptionWriter.writeDescription(atom, writer);
			}
			writer.write("\t\t</GroupDescription>\n");
		} else if (level == Level.ATOM) {
			AtomDescription a = (AtomDescription) description;
			writer.write("\t\t\t");
			writer.write(String.format("<AtomDescription name=\"%s\"/>\n", a.getName()));
		}
	}
	
	private static void writeCondition(Condition condition, FileWriter writer) 
						throws IOException {
		writer.write(condition.toXml());
	}
	
	public static void main(String[] args) {
		try {
			XmlDescriptionReader reader = new XmlDescriptionReader(); 
			Description description = reader.readDescription(new File(args[0]));
			XmlDescriptionWriter.writeToFile(new File(args[1]), description);
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}

}
