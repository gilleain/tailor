package tailor.datasource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tailor.structure.Chain;
import tailor.structure.Domain;
import tailor.structure.Edge;
import tailor.structure.Protein;
import tailor.structure.Segment;
import tailor.structure.Segment.Type;
import tailor.structure.Sheet;
import tailor.translation.ChainDomainMap;

public class TopsWriter {


	public static Map<String, Map<String, String>> toTopsDomainStrings(Protein protein, ChainDomainMap chainDomainMap) {
		Map<String, Map<String, String>> chainDomainStringMap = new HashMap<>();
		for (int i = 0; i < protein.getChains().size(); i++) {
			Chain chain = protein.getChains().get(i);
			chainDomainStringMap.put(
					chain.getCathCompatibleName(), 
					toTopsDomainStrings(chain, chainDomainMap));
		}
		return chainDomainStringMap;
	}

	public static String[] toTopsChainStringArray(Protein protein) {
		String[] chainStrings = new String[protein.getChains().size()];
		for (int i = 0; i < protein.getChains().size(); i++) {
			Chain chain = protein.getChains().get(i);
			chainStrings[i] = toTopsString(chain, new Domain(0));
		}
		return chainStrings;
	}

	public static Map<String, String> toTopsDomainStrings(Chain chain, ChainDomainMap chainDomainMap) {
		if (chainDomainMap != null && chainDomainMap.containsKey(chain.getCathCompatibleName())) {
			List<Domain> domainsForChain = chainDomainMap.get(chain.getCathCompatibleName());
			return toTopsDomainStrings(chain, domainsForChain);
		} else {
			Map<String, String> map = new HashMap<>();
			map.put("0", toTopsString(chain, new Domain(0)));
			return map;
		}
	}

	public static Map<String, String> toTopsDomainStrings(Chain chain, List<Domain> domainsToConvert) {
		Map<String, String> domainStrings = new HashMap<>();

		for (Domain domain : domainsToConvert) {
			//System.err.println("Getting tops string for domain " + d);
			domainStrings.put(domain.getID(), toTopsString(chain, domain));
		}

		return domainStrings;
	}


	public static String toTopsString(Chain chain, Domain domain) {
		//name
		StringBuffer s = new StringBuffer();
		s.append(chain.getCathCompatibleName() + domain.getNumber() + " ");

		//vertexstring
		List<Segment> backboneSegments;
		if (domain.isEmpty()) {
			backboneSegments = chain.getSegments();
		} else {
			List<Segment> segmentsFilteredByDomain = domain.filter(chain.getSegments());
			segmentsFilteredByDomain.add(0, new Segment(Type.NTERMINUS));
			segmentsFilteredByDomain.add(new Segment(Type.CTERMINUS));
			backboneSegments = segmentsFilteredByDomain;
		}

		int vertexNumber = 0;
		for (Segment nextBackboneSegment : backboneSegments) {
			if (nextBackboneSegment.getType() == Type.UNSTRUCTURED) {
				continue;
			} else {
				nextBackboneSegment.setNumber(vertexNumber);
				s.append(nextBackboneSegment.getTopsSymbol());
				vertexNumber++;
			}
		}

		s.append(" ");

		//edgestring
		List<Edge> edges = new ArrayList<>();
		for (Sheet sheet : chain.getSheets()) {
			edges.addAll(sheet.toTopsEdges(domain));
		}

		Collections.sort(edges);

		//merge the chirals with the hbonds
		Iterator<Edge> chiralIterator = chain.getChiralities().iterator();
		Iterator<Edge> edgeIterator;
		while (chiralIterator.hasNext()) {
			boolean merged = false;
			Edge chiral = chiralIterator.next();
			edgeIterator = edges.iterator();
			while (edgeIterator.hasNext()) {
				Edge hbond = edgeIterator.next();
				if (hbond.equals(chiral)) {
					hbond.mergeWith(chiral);
					merged = true;
					break;
				}
			}
			// those chirals that are not merged are simply added
			if (!merged && chiral.containedIn(domain)) {
				edges.add(chiral);
			} 
		}

		Collections.sort(edges);

		edgeIterator = edges.iterator();
		while (edgeIterator.hasNext()) {
			Edge edge = (Edge) edgeIterator.next();
			s.append(edge.toString());
		}

		return s.toString();
	}

}
