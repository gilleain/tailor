package tailor.datasource;

import tailor.structure.Chain;
import tailor.structure.Protein;
import tailor.structure.Segment;
import tailor.structure.Segment.Type;

public class PymolScriptWriter {

	public static String toScript(Protein protein) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Chain chain : protein.getChains()) {
			stringBuilder.append(toScript(chain));
		}
		return stringBuilder.toString();
	}

	public static String toScript(Chain chain) {
		StringBuilder script = new StringBuilder();

		int segmentNumber = 1;
		for (Segment bs : chain.getSegments()) {
			String name = bs.getTopsSymbol() + "" + segmentNumber + "(" + bs.firstPDB() + "-" + bs.lastPDB() + ")";
			String selection = "not hetatm and name ca+c+n+o+h and resi " + bs.firstPDB() + "-" + bs.lastPDB();
			script.append("cmd.select(\"" + name + "\", \"" + selection + "\")\n");
			String color = "green";
			if (bs.getType() == Type.STRAND) {
				script.append("line(");
				//	                Axis axis = bs.getAxis();
				//	                Point3d start = axis.getStart();
				//	                Point3d end = axis.getEnd();
				//	                script.append(String.format("%6.2f, %6.2f, %6.2f, ", start.x, start.y, start.z));
				//	                script.append(String.format("%6.2f, %6.2f, %6.2f, ", end.x, end.y, end.z));
				script.append("\"").append(name).append("axis\")\n");
				color = "yellow";
			} else if (bs.getType() == Type.HELIX) {
				color = "red";
			}
			script.append("cmd.color(\"" + color + "\", \"" + name + "\")\n");
			segmentNumber++;
		}
		return script.toString(); 
	}

}
