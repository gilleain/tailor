package tailor.description;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import tailor.condition.PropertyCondition;

public class DescriptionGenerator {

	public static final Map<String, double[][]> PREDEFINED_BOUNDS = new HashMap<>();
	public static final Map<String, String[]> PREDEFINED_VALUES = new HashMap<>();

	static {
		PREDEFINED_BOUNDS.put("AR", new double[][]{{-90, 30}, {0, 30}});
		PREDEFINED_BOUNDS.put("AL", new double[][]{{90, 30}, {0, 30}});
		PREDEFINED_BOUNDS.put("BR", new double[][]{{-90, 30}, {150, 30}});

		PREDEFINED_VALUES.put("RLnest", new String[]{"AR", "AL"});
		PREDEFINED_VALUES.put("LRNest", new String[]{"AL", "AR"});
		PREDEFINED_VALUES.put("RCatmat3", new String[]{"AR", "BR"});
		PREDEFINED_VALUES.put("RCatmat4", new String[]{"AR", "AR", "BR"});
	}

	public static Map<String, ChainDescription> getPredefinedDescriptions() {
		return generatePredefinedFromDictionary(PREDEFINED_VALUES);
	}


	public static Map<String, ChainDescription> generatePredefinedFromDictionary(Map<String, String[]> valueDict) {
		Map<String, ChainDescription> result = new HashMap<>();
		for (Map.Entry<String, String[]> entry : valueDict.entrySet()) {
			Map.Entry<String, ChainDescription> generated = generatePredefined(entry.getKey(), entry.getValue());
			result.put(generated.getKey(), generated.getValue());
		}
		return result;
	}

	private static Map.Entry<String, ChainDescription> generatePredefined(String name, String[] boundNames) {
		double[][][] bounds = new double[boundNames.length][][];
		for (int i = 0; i < boundNames.length; i++) {
			bounds[i] = PREDEFINED_BOUNDS.get(boundNames[i]);
		}
		return new AbstractMap.SimpleEntry<>(name, generateBackboneDescription(name, bounds));
	}

	private static ChainDescription generateBackboneDescription(String name, double[][][] bounds) {
		ChainDescription chain = new ChainDescription();
		chain.addCondition(new PropertyCondition("chainName", name)); // TODO - very fragile!
		chain.createResidues(bounds.length + 2);

		for (int i = 0; i < bounds.length; i++) {
			int residueNumber = i + 2;
			double[][] bound = bounds[i];
			double[] phiBound = bound[0];
			double[] psiBound = bound[1];
			double phiCenter = phiBound[0];
			double phiRange = phiBound[1];
			double psiCenter = psiBound[0];
			double psiRange = psiBound[1];

			chain.createPhiBoundCondition(residueNumber, phiCenter, phiRange);
			chain.createPsiBoundCondition(residueNumber, psiCenter, psiRange);
		}

		return chain;
	}

}
