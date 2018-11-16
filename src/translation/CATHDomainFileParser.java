package translation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tops.translation.model.Domain;

public class CATHDomainFileParser {
    private static Pattern domainPattern = Pattern.compile("((?:\\d)(?:\\s\\s[\\d\\w]\\s+\\d+\\s\\-\\s[\\w\\d]\\s+\\d+\\s\\-)+)+");
    private static Pattern segmentPattern = Pattern.compile("[\\d\\w]\\s+(\\d+)\\s\\-\\s[\\w\\d]\\s+(\\d+)\\s\\-");

    public static Map<String, ChainDomainMap> parseWholeFile(String filename) throws IOException {
        Map<String, ChainDomainMap> pdbChainDomainMap = new HashMap<>();

        String line;
        BufferedReader bufferer = null;
        try {
            bufferer = new BufferedReader(new FileReader(filename));
            while ((line = bufferer.readLine()) != null) {
                if (line.substring(0,1).equals("#")) {
                    continue;
                }
                // analyze the line
                String pdbid = line.substring(0, 4);
                String chain = line.substring(4, 5);
                List<Domain> domains = CATHDomainFileParser.parseLine(line);
    
                // store the result
                ChainDomainMap chainDomainMap;
                if (pdbChainDomainMap.containsKey(pdbid)) {
                	chainDomainMap = pdbChainDomainMap.get(pdbid);
                } else {
                    chainDomainMap = new ChainDomainMap();
                    pdbChainDomainMap.put(pdbid, chainDomainMap);
                }
                chainDomainMap.put(chain, domains);
            }
        } finally {
            if (bufferer != null) bufferer.close();
        }

        return pdbChainDomainMap;
    }

    public static ChainDomainMap parseUpToParticularID(String filename, String pdbid) throws IOException {
        ChainDomainMap chainDomainMap = new ChainDomainMap();

        String line;
        BufferedReader bufferer = null;
        try {
            bufferer = new BufferedReader(new FileReader(filename));
            while ((line = bufferer.readLine()) != null) {
                if (line.substring(0,1).equals("#") || !line.substring(0, 4).equals(pdbid)) {
                    continue;
                }
                // analyze the line
                String chain = line.substring(4, 5);
                List<Domain> domains = CATHDomainFileParser.parseLine(line);

                // store the result
                chainDomainMap.put(chain, domains);
                //System.err.println("Storing " + domains.size() + " domains for " + pdbid + chain);
            }
        } finally {
            if (bufferer != null) bufferer.close();
        }

        return chainDomainMap;
    }

    public static List<Domain> parseLine(String line) {
    	List<Domain> domains = new ArrayList<>();

        Matcher domainMatcher = domainPattern.matcher(line.substring(14));
        int domainID = 1;
        while (domainMatcher.find()) {
            Domain domain = new Domain(domainID);
            String domainString = domainMatcher.group(0);
            Matcher segmentMatcher = segmentPattern.matcher(domainString);
            int numberOfSegments = Integer.parseInt(domainString.substring(0, 1));
            int segmentCount = 0;
            while (segmentMatcher.find() && segmentCount < numberOfSegments) {
                int start = Integer.parseInt(segmentMatcher.group(1));
                int end = Integer.parseInt(segmentMatcher.group(2));
                domain.addSegment(start, end); 
                segmentCount++;
            }
            domains.add(domain);
            domainID++;
        }
        return domains;
    }

    public static void main(String[] args) {
        try {
            Map<String, ChainDomainMap> pdbChainDomainMap = 
            		CATHDomainFileParser.parseWholeFile(args[0]);
            
            for (String pdbID : pdbChainDomainMap.keySet()) {
                ChainDomainMap chainDomainMap = pdbChainDomainMap.get(pdbID);
                for (String chainID : chainDomainMap)  {
                    List<Domain> domains = chainDomainMap.get(chainID);
                    for (Domain domain : domains) {
                        System.out.println(pdbID + chainID + " " + domain);
                    }
                } 
            } 
        } catch (IOException ioe) {
            System.err.println(ioe.toString());
        }
    }
}
