package aigen;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

class DescriptionGenerator {
	public static ChainDescription generateBackboneDescription(String name, double[][][] bounds) {
		Map<String, Object> props = new HashMap<>();
		props.put("chainID", name);
		ChainDescription chain = new ChainDescription(props);
		chain.createResidues(bounds.length + 2);

		for (int i = 0; i < bounds.length; i++) {
			int r = i + 2;
			double[][] bound = bounds[i];
			double[] phiBound = bound[0];
			double[] psiBound = bound[1];
			double phiCenter = phiBound[0];
			double phiRange = phiBound[1];
			double psiCenter = psiBound[0];
			double psiRange = psiBound[1];

			chain.createPhiBoundCondition(r, phiCenter, phiRange);
			chain.createPsiBoundCondition(r, psiCenter, psiRange);
		}

		return chain;
	}

	public static Map.Entry<String, ChainDescription> generatePredefined(String name, String[] boundNames) {
		double[][][] bounds = new double[boundNames.length][][];
		for (int i = 0; i < boundNames.length; i++) {
			bounds[i] = PredefinedData.PREDEFINED_BOUNDS.get(boundNames[i]);
		}
		return new AbstractMap.SimpleEntry<>(name, generateBackboneDescription(name, bounds));
	}

	public static Map<String, ChainDescription> generatePredefinedFromDictionary(Map<String, String[]> valueDict) {
		Map<String, ChainDescription> result = new HashMap<>();
		for (Map.Entry<String, String[]> entry : valueDict.entrySet()) {
			Map.Entry<String, ChainDescription> generated = generatePredefined(entry.getKey(), entry.getValue());
			result.put(generated.getKey(), generated.getValue());
		}
		return result;
	}

	public static Map<String, ChainDescription> getPredefinedDescriptions() {
		return generatePredefinedFromDictionary(PredefinedData.PREDEFINED_VALUES);
	}
}
