package tailor.datasource.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import tailor.api.AtomListDescription;
import tailor.description.AtomDescription;
import tailor.description.ChainDescription;
import tailor.description.GroupDescription;
import tailor.description.ProteinDescription;

public class XmlDescriptionWriter {
	
	public static void writeToFile(File file, ChainDescription description) throws IOException {
		FileWriter writer = new FileWriter(file);
		XmlDescriptionWriter.writeChainDescription(description, writer);
		writer.flush();
	}
	
	private static void writeProteinDescription(ProteinDescription p, FileWriter writer) throws IOException {
		writer.write(String.format("<ProteinDescription name=\"%s\">\n", p.getName()));
		for (ChainDescription chain : p.getChainDescriptions()) {
			XmlDescriptionWriter.writeChainDescription(chain, writer);
		}
		writer.write("</ProteinDescription>\n");
	}
	
	private static void writeChainDescription(ChainDescription c, FileWriter writer) throws IOException {
		writer.write("\t");
		Optional<String> label = c.getLabel();
		if (label.isPresent()) {
			writer.write(String.format("<ChainDescription name=\"%s\">\n", label.get()));
		} else {
			writer.write("<ChainDescription>\n");
		}
		for (GroupDescription group : c.getGroupDescriptions()) {
			XmlDescriptionWriter.writeGroupDescription(group, writer);
		}
		for (AtomListDescription condition : c.getAtomListDescriptions()) {
			XmlDescriptionWriter.writeCondition(condition, writer);
		}
		writer.write("\t</ChainDescription>\n");
	}
	
	private static void writeGroupDescription(GroupDescription g, FileWriter writer) throws IOException {
		writer.write("\t\t");
		Optional<String> name = g.getName();
		String out;
		if (name.isEmpty()) {
			out = String.format("" +"<GroupDescription name=\"%s\">","*");
		} else {
			out = String.format("<GroupDescription name=\"%s\">", name.get());
		}
		writer.write(out + "\n");
		for (AtomDescription atom : g.getAtomDescriptions()) {
			XmlDescriptionWriter.writeAtomDescription(atom, writer);
		}
		writer.write("\t\t</GroupDescription>\n");
	}
	
	private static void writeAtomDescription(AtomDescription description, FileWriter writer) throws IOException {
		AtomDescription a = (AtomDescription) description;
		writer.write("\t\t\t");
		writer.write(String.format("<AtomDescription name=\"%s\"/>\n", a.getLabel()));
	}
	
	
	private static void writeCondition(AtomListDescription condition, FileWriter writer) 
						throws IOException {
		writer.write(toXML(condition));
	}
	
	private static String toXML(AtomListDescription atomListCondition) {
		return null;	// TODO
	}
	
	public static void main(String[] args) {
		try {
			XmlDescriptionReader reader = new XmlDescriptionReader(); 
			ChainDescription description = reader.readDescription(new File(args[0]));
			XmlDescriptionWriter.writeToFile(new File(args[1]), description);
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	}

}
