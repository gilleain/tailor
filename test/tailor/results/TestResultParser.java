package tailor.results;

import java.util.function.Function;

import org.junit.Test;

import tailor.structure.Atom;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.Protein;

public class TestResultParser {

	@Test
	public void test() {        
		String filename = "";
		String pdbdir = "";

		Function<String, ExampleDescription> parser = line -> {
			String[] parts = line.split("\\.");
			return new ExampleDescription(parts[0], parts[1], parts[2], parts[3], "0");
		};

		for (Protein e : ResultParser.generateExamples(filename, pdbdir, parser)) {
			recurse(e);
		}
	}

	/**
	 * Recursively print the structure tree
	 */
	private static void recurse(Protein tree) {
		System.out.println(tree.getClass().getSimpleName() + " " + tree);
		for (Chain child : tree.getChains()) {
			recurse(child);
		}
	}
	private static void recurse(Chain tree) {
		System.out.println(tree.getClass().getSimpleName() + " " + tree);
		for (Group child : tree.getGroups()) {
			recurse(child);
		}
	}
	private static void recurse(Group group) {
		System.out.println(group.getClass().getSimpleName() + " " + group);
		for (Atom atom : group.getAtoms()) {
			System.out.println(atom);
		}
	}

}
