//package translation;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import tops.dw.protein.DomainDefinition;
//import tops.dw.protein.SecStrucElement;
//import translation.model.BackboneSegment;
//import translation.model.Domain;
//
///**
// * @author maclean
// *
// */
//public class SSETest {
//    
//    public static ChainDomainMap convertDomains(Enumeration<?> dw_domains, Enumeration<?> dw_sses) {
//    	ChainDomainMap chainDomainMap = new ChainDomainMap();
//        while (dw_domains.hasMoreElements()) {
//            DomainDefinition domainDefinition = (DomainDefinition) dw_domains.nextElement();
//            SecStrucElement rootSSE = (SecStrucElement) dw_sses.nextElement();
//            
//            SecStrucElement s = rootSSE.GetTo();
//            int domainStart = s.PDBStartResidue;
//            while (s.GetTo() != null) { 
//                s = s.GetTo();
//            }
//            int domainEnd = s.GetFrom().PDBFinishResidue;
//            
////            System.err.println(domainDefinition.getCATHcode());
//            int domainNumber = domainDefinition.getCATHcode().getDomain();
//            
//            Domain domain = new Domain(domainNumber);
//            domain.addSegment(domainStart, domainEnd);
////            Enumeration sequenceFragments = domainDefinition.getSequenceFragments();
////            while (sequenceFragments.hasMoreElements()) {
////                IntegerInterval interval = (IntegerInterval) sequenceFragments.nextElement();
////                domain.addSegment(interval.getLower(), interval.getUpper());
////            }
////            System.err.println("domain " + domain);
//            
//            String chain = String.valueOf(domainDefinition.getChain());
//            if (chainDomainMap.containsKey(chain)) {
//                List<Domain> domains = chainDomainMap.get(chain);
//                domains.add(domain);
//            } else {
//                List<Domain> domains = new ArrayList<Domain>();
//                domains.add(domain);
//                chainDomainMap.put(String.valueOf(chain), domains);
//            }
//                
//        }
//        return chainDomainMap;
//    }
//    
//    public static BackboneSegment findMatch(SecStrucElement s, List<BackboneSegment> backboneSegments) {
//        int sseStart = s.PDBStartResidue;
//        int sseEnd = s.PDBFinishResidue;
//        int maxOverlap = 0;
//        BackboneSegment hit = null;
//        
//        for (int i = 0; i < backboneSegments.size(); i++) {
//            BackboneSegment segment = (BackboneSegment) backboneSegments.get(i);
//            int segmentStart = segment.firstPDB();
//            int segmentEnd = segment.lastPDB();
//            
//            //int s1 = Math.min(sseStart, segmentStart);
//            int s2 = Math.max(sseStart, segmentStart);
//            int e1 = Math.min(sseEnd, segmentEnd);
//            //int e2 = Math.max(sseEnd, segmentEnd);
//            
//            if (e1 < s2) {
//                continue;
//            } else {
//                int overlap = (e1 - s2) + 1;
//                if (overlap > maxOverlap) {
//                    hit = segment;
//                    maxOverlap = overlap;
//                }
//            }
//        }
//        
//        return hit;
//    }
//    
//    public static void compareSSEs(tops.dw.protein.Protein dw_protein, translation.model.Protein gmt_protein) {
//        Enumeration<?> dw_domains = dw_protein.GetDomainDefs().elements();
//        Enumeration<?> dw_sselists = dw_protein.GetLinkedLists().elements();
//        ChainDomainMap chainDomainMap  = SSETest.convertDomains(dw_domains, dw_sselists);
//        
//        Map<String, Map<String, List<BackboneSegment>>> chainSegmentMap = 
//        		gmt_protein.getBackboneSegmentsByDomain(chainDomainMap);
//        
//        dw_domains = dw_protein.GetDomainDefs().elements();
//        dw_sselists = dw_protein.GetLinkedLists().elements();
//        while (dw_domains.hasMoreElements()) {
//            DomainDefinition domain = (DomainDefinition) dw_domains.nextElement();
//            SecStrucElement sseStart = (SecStrucElement) dw_sselists.nextElement();
//            
//            char chain = domain.getChain();
//            Map<String, List<BackboneSegment>> domainSegmentMap = 
//            		chainSegmentMap.get(String.valueOf(chain));
//            List<BackboneSegment> backboneSegments = domainSegmentMap.get(String.valueOf(domain.getDomainID()));
//
//            //System.err.println(backboneSegments.size() + " segments");
//            //for (int i = 0; i < backboneSegments.size(); i++) { System.out.println(backboneSegments.get(i)); }
//            
//            List<BackboneSegment> matchedSegments = new ArrayList<BackboneSegment>();
//            String id = dw_protein.Name;
//            for (SecStrucElement s = sseStart; s != null; s = s.GetTo()) {
//                if (s.IsTerminus()) { continue; }
//                BackboneSegment hit = SSETest.findMatch(s, backboneSegments);
//                
//                if (hit == null) {
//                    System.out.println(id + " " + s + " has no match");
//                } else {
//                    int targetLength = s.Length();
//                    int length = hit.length();
//                    if (length == targetLength) {
//                        System.out.println(id + " " + s + " matches " + hit + " perfectly");
//                    } else {
//                        System.out.println(id + " " + s + " matches " + hit + " sorta");
//                    }
//                    
//                    matchedSegments.add(hit);
//                }
//            }
//            
//            for (int i = 0; i < backboneSegments.size(); i++) {
//                BackboneSegment segment = (BackboneSegment) backboneSegments.get(i);
//                if (matchedSegments.contains(segment)) {
//                    continue;
//                } else {
//                    System.out.println(id + " " + segment + " superfluous");
//                }
//            }
//        }
//    }
//
//    /**
//     * @param args
//     */
//    public static void main(String[] args) {
//        Logger.getLogger("translation.FoldAnalyser").setLevel(Level.OFF);
//        FoldAnalyser foldAnalyser = new FoldAnalyser();
////        String topsFilename = args[0];
////        String pdbFilename = args[1];
//        String pdbDirectoryName = args[0];
//        String topsDirectoryName = args[1];
//        //try {
//            File structureDirectory = new File(pdbDirectoryName);
//            File topsDirectory = new File(topsDirectoryName);
//            String[] fileList = structureDirectory.list();
//            for (int i = 0; i < fileList.length; i++) {
//                try {
//                    String pdbFilename = fileList[i];
//                    System.err.println(pdbFilename);
//                    
//                    String pdbPath = new File(structureDirectory, pdbFilename).getPath();
//                    String topsFilename = pdbFilename.substring(0, 4) + ".tops";
//                    File topsFile = new File(topsDirectory, topsFilename);
//                    tops.dw.protein.Protein dw_protein = new tops.dw.protein.Protein(topsFile);
//                    translation.model.Protein gmt_protein = foldAnalyser.analyse(PDBReader.read(pdbPath));
//                
//                    SSETest.compareSSEs(dw_protein, gmt_protein);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            /*
//        } catch (TopsFileFormatException tffe) {
//            tffe.printStackTrace();
//            System.err.println("TopsFileFormatException");
//        } catch (FileNotFoundException fnf) {
//            System.err.println("FileNotFoundException " + fnf);
//        } catch (IOException ioe) {
//            System.err.println("IOException " + ioe);
//        }
//        */
//    }
//
//}
