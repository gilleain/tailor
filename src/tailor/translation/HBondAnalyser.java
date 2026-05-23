package tailor.translation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.vecmath.Point3d;

import tailor.geometry.Geometer;
import tailor.structure.Chain;
import tailor.structure.Group;
import tailor.structure.HBond;
import tailor.structure.Protein;
import tailor.structure.Segment;
import tailor.structure.Segment.Type;

public class HBondAnalyser {
    
    private final Logger LOG = Logger.getLogger(HBondAnalyser.class.getName());

    private Properties properties;

    private int threeTenHelixStart;
    private int threeTenHelixEnd;

    private int alphaHelixStart;
    private int alphaHelixEnd;

    private int piHelixStart;
    private int piHelixEnd;

    private int strandStart;
    private int strandEnd;
    
    private enum Tag {
        LOOP, 
        HELIX_THREE_TEN_END,
        HELIX_ALPHA_END,
        HELIX_PI_END, 
        SINGLE_STRAND_BOND,
        HELIX_THREE_TEN_START,
        HELIX_ALPHA_START, 
        HELIX_PI_START,
        HELIX_ALPHA_MIDDLE,
        STRAND_PARALLEL,
        STRAND_ANTIPARALLEL;
    }

    public HBondAnalyser() {
        this.properties = new Properties();
        setDefaultProperties();
    }

    public HBondAnalyser(Properties properties) {
        this.properties = properties;
    }

    public void setDefaultProperties() {
        this.properties.setProperty("CALCULATE_BACKBONE_AMIDE_HYDROGENS", "TRUE");
        this.properties.setProperty("MAX_HO_DISTANCE", "3.0");
        this.properties.setProperty("MIN_NHO_ANGLE", "120");
        this.properties.setProperty("MIN_HOC_ANGLE", "90");
    }

    public void resetEndpoints() {
        this.threeTenHelixStart = -1;
        this.threeTenHelixEnd   = -1;

        this.alphaHelixStart    = -1;
        this.alphaHelixEnd      = -1;

        this.piHelixStart       = -1;
        this.piHelixEnd         = -1;

        this.strandStart        = -1;
        this.strandEnd          = -1;
    }

    public void setProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    public void analyse(Protein protein) throws PropertyException {
        boolean calculateBackboneHydrogens = this.properties.getProperty("CALCULATE_BACKBONE_AMIDE_HYDROGENS").equals("TRUE");

        for (Chain chain : protein.getChains()) {
            if (calculateBackboneHydrogens) {
                chain.addBackboneAmideHydrogens();
            }
            this.analyse(chain);
        }
    }
    
    private record NHOCCords(Point3d n, Point3d h, Point3d o, Point3d c) { 
    	public boolean anyNull() {
    		return n == null || h == null || o == null || c == null;
    	}
    }
    
    private record HBondParameters(double maxHODistance, double minNHOAngle, double minHOCAngle ) {
    	
    }
    
    private record HBondValues(double hoDistance, double nhoAngle, double hocAngle ) {
    	
    	public boolean matches(HBondParameters hBondParameters) {
    		return this.hoDistance < hBondParameters.maxHODistance 
    				&& this.nhoAngle > hBondParameters.minNHOAngle 
    				&& this.hocAngle > hBondParameters.minHOCAngle;
    	}
    	
    	public String toString() {
    		return String.format("HO: %2.2f, NHO: %2.2f, HOC: %2.2f", hoDistance, nhoAngle, hocAngle);
    	}
    }

    public void analyse(Chain chain) throws PropertyException {
        double maxHODistance = 0.0;
        double minNHOAngle   = 0.0;
        double minHOCAngle   = 0.0;

        try {
            maxHODistance = Double.parseDouble(this.properties.getProperty("MAX_HO_DISTANCE"));
            minNHOAngle   = Double.parseDouble(this.properties.getProperty("MIN_NHO_ANGLE"));
            minHOCAngle   = Double.parseDouble(this.properties.getProperty("MIN_HOC_ANGLE"));
            LOG.info("HO " + maxHODistance + " NHO " + minNHOAngle + " HOC " + minHOCAngle);
            
        } catch (NumberFormatException nfe) {
            throw new PropertyException("Error in properties!");
        }
        
        HBondParameters hBondParameters = new HBondParameters(maxHODistance, minNHOAngle, minHOCAngle);

        List<Group> groups = chain.getGroups();
        for (int index = 0; index < groups.size(); index++) {
            Group first = groups.get(index);
            try {
	            List<Tag> tags = findTags(chain, first, index, hBondParameters);
	            LOG.info(first.toFullString() + " " + tags);
	            this.updateSSEEndpoints(index, tags, chain);
            }catch (NullPointerException npe) {
            	System.err.println(npe);
            }
        }

        // finally, finish off the SSEs
        this.finishSSES(chain);
        chain.sortSegments();
        chain.mergeHelices();
        addTerminii(chain);
        
        // TODO
        int bbIndex = 0;
        for (Segment segment : chain.getSegments()) {
        	segment.setNumber(bbIndex);
        	bbIndex++;
        }
    }
    
    private List<Tag> findTags(Chain chain, Group first, int firstIndex, HBondParameters hBondParameters) {

    	// we ignore gamma turns!
    	int position = firstIndex;
    	int nextPosition = position + 3;

    	NHOCCords firstCoords = getNHOCCoords(first);

    	// FIXME : unfortunately, this misses out on PRO residues (also below)
    	if (firstCoords == null || firstCoords.anyNull()) {
    		return List.of();
    	}

    	// allow for chain breaks, or return if we have reached the end
    	if (!chain.hasResidueByAbsoluteNumbering(nextPosition)) {
    		Group second = chain.getNextResidue(position);
    		if (second == null) {   //probably reached the end of the chain!
    			return List.of();
    		}
    		nextPosition = second.getAbsoluteNumber();
    	}

    	// now, compare the first residue to the residues further on in the chain
    	if (nextPosition >= chain.length()) return List.of();
    	List<Group> groups = chain.getGroups();
    	for (int secondPosition = nextPosition; secondPosition < groups.size(); secondPosition++) {
    		// we must still check this, since a chain break might move us to i + 2
    		if (secondPosition < (position + 2)) {
    			continue;
    		}

    		Group second;
    		try {
    			second = groups.get(secondPosition);
    			if (!second.isStandardAminoAcid()) {
    				continue;
    			}
    		} catch (IndexOutOfBoundsException i) {
    			System.err.println("IOOBE " + i);
    			break;
    		}

    		NHOCCords secondCoords = getNHOCCoords(second);

    		// FIXME : PRO residues...
    		if (secondCoords == null || secondCoords.anyNull()) {
    			continue;
    		}

    		// bonds from first N-H to second C=O
    		HBondValues firstToSecond = calculateValues(firstCoords, secondCoords);

    		HBond firstSecondBond = null;
    		if (firstToSecond.matches(hBondParameters)) {
    			firstSecondBond = new HBond(first, second, firstToSecond.hoDistance, firstToSecond.nhoAngle, firstToSecond.hocAngle);
    		}

    		if (firstSecondBond != null) {
    			add(chain, first, second, firstSecondBond);
    		}

    		// bonds from second N-H to first C=O
    		HBondValues secondToFirst = calculateValues(secondCoords, firstCoords);

    		HBond secondFirstBond = null;
    		if (secondToFirst.matches(hBondParameters)) {
    			secondFirstBond = new HBond(second, first, secondToFirst.hoDistance, secondToFirst.nhoAngle, secondToFirst.hocAngle);
    		}

    		if (secondFirstBond != null) {
    			add(chain, first, second, secondFirstBond);
    		}
    	}

    	// now, use these hbond assignments to determine the residue's environment
    	return this.convertBondsToTags(first);
    }
    
    private void add(Chain chain, Group first, Group second, HBond hbond) {
    	chain.addHBond(hbond);
		first.addHBond(hbond);
		second.addHBond(hbond);
    }
    
    private HBondValues calculateValues(NHOCCords firstCoords, NHOCCords secondCoords) {
    	double hoDistance = firstCoords.h.distance(secondCoords.o);
		double nhoAngle = Geometer.angle(firstCoords.n, firstCoords.h, secondCoords.o);
		double hocAngle = Geometer.angle(firstCoords.h, secondCoords.o, secondCoords.c);
		return new HBondValues(hoDistance, nhoAngle, hocAngle);
    }
    
    private NHOCCords getNHOCCoords(Group group) {
    	try {
	    	 Point3d n = group.getCoordinates("N");
	         Point3d h = group.getCoordinates("H");
	         Point3d o = group.getCoordinates("O");
	         Point3d c = group.getCoordinates("C");
	         return new NHOCCords(n, h, o, c);
    	} catch (NullPointerException npe) {
    		return null;	// TODO
    	}
        
    }
    
    public void addTerminii(Chain chain) {
    	List<Segment> segments = chain.getSegments();
        if (segments.isEmpty()) {
            return;
        }

        if (segments.get(0).getType() != Type.NTERMINUS) {
        	segments.add(0, new Segment(Type.NTERMINUS));
        }

        if (segments.get(segments.size() - 1).getType() != Type.CTERMINUS) {
            segments.add(new Segment(Type.CTERMINUS));
        }
    }

    public List<Tag> convertBondsToTags(Group residue) {
    	List<HBond> nTerminalHBonds = residue.getNTerminalHBonds();
    	List<HBond> cTerminalHBonds = residue.getCTerminalHBonds();
    	LOG.info(residue.getAbsoluteNumber() + " " + nTerminalHBonds + " " + cTerminalHBonds);

        List<Tag> tags = new ArrayList<>();

        for (int nIndex = 0; nIndex < nTerminalHBonds.size(); nIndex++) {
            int nSeparation = nTerminalHBonds.get(nIndex).getResidueSeparation();
            Tag nTag = this.convertBondsToTag(nSeparation, 0);
            if (nTag != null) {
                tags.add(nTag);
            }
        }

        for (int cIndex = 0; cIndex < cTerminalHBonds.size(); cIndex++) {
            int cSeparation = cTerminalHBonds.get(cIndex).getResidueSeparation();
            Tag cTag = this.convertBondsToTag(0, cSeparation);
            if (cTag != null) {
                tags.add(cTag);
            }
        }

        for (int nIndex = 0; nIndex < nTerminalHBonds.size(); nIndex++) {
            int nSeparation = nTerminalHBonds.get(nIndex).getResidueSeparation();

            for (int cIndex = 0; cIndex < cTerminalHBonds.size(); cIndex++) {
                int cSeparation = cTerminalHBonds.get(cIndex).getResidueSeparation();

                Tag ncTag = this.convertBondsToTag(nSeparation, cSeparation);
                if (ncTag != null) {
                    tags.add(ncTag);
                }
            }
        }
        
        return tags;
    }

    public Tag convertBondsToTag(int nSeparation, int cSeparation) {
    	LOG.info(String.format("%s %s", nSeparation, cSeparation));

        // neither has a bond
        if (nSeparation == 0 && cSeparation == 0) {
            return Tag.LOOP;
        }

        // one or the other has a bond
        else if (nSeparation != 0 && cSeparation == 0) {
            if (nSeparation == 3) {
                return Tag.HELIX_THREE_TEN_END;
            } else if (nSeparation == 4) {
                return Tag.HELIX_ALPHA_END;
            } else if (nSeparation == 5) {
                return Tag.HELIX_PI_END;
            } else if (nSeparation < -5 || nSeparation > 5) {
                return Tag.SINGLE_STRAND_BOND;
            }
        }

        else if (nSeparation == 0 && cSeparation != 0) {
            if (cSeparation == 3) {
                return Tag.HELIX_THREE_TEN_START;
            } else if (cSeparation == 4) {
                return Tag.HELIX_ALPHA_START;
            } else if (cSeparation == 5) {
                return Tag.HELIX_PI_START;
            } else if (cSeparation < -5 || cSeparation > 5) {
                return Tag.SINGLE_STRAND_BOND;
            }
        }

        // both have one bond
        else if (nSeparation != 0 && cSeparation != 0) {

            // a standard : middle of a helix
            if (nSeparation == 4 && cSeparation == 4) {
                return Tag.HELIX_ALPHA_MIDDLE;
            // we counter-intuitively take the SUM here, because one will always be negative, and the other positive 
            } else if ((Math.abs(nSeparation - cSeparation) == 2) && (nSeparation > 5 || nSeparation < -5)) {
                return Tag.STRAND_PARALLEL;
            } else if ((nSeparation == cSeparation)  && (nSeparation > 5 || nSeparation < -5)) {
                return Tag.STRAND_ANTIPARALLEL;
            }
        }

        return null;
    }

    public void updateSSEEndpoints(int index, List<Tag> tags, Chain chain) {
        if (tags.contains(Tag.HELIX_THREE_TEN_START)) {

            // not seen any three ten bond before
            if (this.threeTenHelixStart == -1) {
                this.threeTenHelixStart = index;

            // this three ten bond does not overlap with the previous one
            } else if (index + 3 > this.threeTenHelixEnd + 1) {

                // only create three-ten helices with more than one bond
                if (this.threeTenHelixEnd - this.threeTenHelixStart > 3) {
                    chain.createHelix(this.threeTenHelixStart + 1, this.threeTenHelixEnd - 1); 
                    LOG.info("310 : " + this.threeTenHelixStart + ":" +  this.threeTenHelixEnd); 
                }

                // start a potential new helix with this bond
                this.threeTenHelixStart = index;
            }

            // in any case, the helix will start three places further on
            this.threeTenHelixEnd = index + 3;
        }

        if (tags.contains(Tag.HELIX_ALPHA_START)) {

            // not seen any alpha bonds before
            if (this.alphaHelixStart == -1) {
                this.alphaHelixStart = index;

            // this three ten bond does not overlap with the previous one
            } else if (index + 4 > this.alphaHelixEnd + 1) {

                // only create three-ten helices with more than one bond
                if (this.alphaHelixEnd - this.alphaHelixStart > 4) {
                    chain.createHelix(this.alphaHelixStart + 1, this.alphaHelixEnd - 1); 
                    LOG.info("Alpha: " + this.alphaHelixStart + ":" +  this.alphaHelixEnd); 
                }

                // start a potential new helix with this bond
                this.alphaHelixStart = index;
            }

            // in any case, the helix will start three places further on
            this.alphaHelixEnd = index + 4;
        }

        if (tags.contains(Tag.HELIX_PI_START)) {

            // not seen any three ten bond before
            if (this.piHelixStart == -1) {
                this.piHelixStart = index;

            // this three ten bond does not overlap with the previous one
            } else if (index + 5 > this.piHelixEnd + 1) {

                // only create three-ten helices with more than one bond
                if (this.piHelixEnd - this.piHelixStart > 5) {
                    chain.createHelix(this.piHelixStart + 1, this.piHelixEnd - 1); 
                    LOG.info("Pi: " + this.piHelixStart + ":" +  this.piHelixEnd); 
                }

                // start a potential new helix with this bond
                this.piHelixStart = index;
            }

            // in any case, the helix will start three places further on
            this.piHelixEnd = index + 5;
        }
        
        if (tags.contains(Tag.STRAND_ANTIPARALLEL) || 
                tags.contains(Tag.STRAND_PARALLEL) || 
                tags.contains(Tag.SINGLE_STRAND_BOND)) {
            LOG.info("Strand like bond at residue " + chain.getResidueByAbsoluteNumbering(index) + " indices = " + "" + " tags = " + tags);
            if (this.strandStart == -1) {
                this.strandStart = index;
                this.strandEnd = index;
            } else {
                boolean partnerStrandContinuous = this.residuePartnerNeighboursPrevious(index, chain);
                if (this.strandEnd < index - 2 || !partnerStrandContinuous) {
                    if (this.strandEnd - this.strandStart > 0) {
                        chain.createStrand(this.strandStart, this.strandEnd);
                    }
                    this.strandStart = index;
                    this.strandEnd = index;
                } else {
                    if (this.strandStart != -1) {
                        this.strandEnd = index;
                    }
                }
            }
        }
    }

    // there must be a better way. avoiding tags altogether, storing the n and c terminal hbond separately in Residue,
    // aaaaaaannnnnd all sorts of other optimisations
    public boolean residuePartnerNeighboursPrevious(int index, Chain chain) {
        // 2 or 1?
        if (index < 2) {
            return true;
        }

        // could, of course, just pass the residue along to this method...
        Group residue = chain.getResidueByAbsoluteNumbering(index);
        Group twoBehind = chain.getResidueByAbsoluteNumbering(index - 2);

        // get the hbond partners for these two residues as arrays of indices
        int[] residueHBondPartners = residue.getHBondPartners();
        int[] twoBehindHBondPartners = twoBehind.getHBondPartners();

        // this allows bond in the other register (odd/even) to start without starting a new strand
        if (twoBehindHBondPartners.length == 0) {
            return true;
        }

        // check to see if the indices match up
        for (int i = 0; i < residueHBondPartners.length; i++) {
            int iIndex = residueHBondPartners[i];
            for (int j = 0; j < twoBehindHBondPartners.length; j++) {
                int jIndex = twoBehindHBondPartners[j];
                if (this.indicesArePartners(iIndex, jIndex)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean indicesArePartners(int i, int j) {
        return (Math.abs(i - j) <= 2);
    }

    public void finishSSES(Chain chain) {
        if (this.threeTenHelixEnd != -1 && this.threeTenHelixEnd - this.threeTenHelixStart > 3) {
            LOG.info("Last 310 : " + this.threeTenHelixStart + ":" +  this.threeTenHelixEnd); 
            chain.createHelix(this.threeTenHelixStart + 1, this.threeTenHelixEnd - 1);
        }

        if (this.alphaHelixEnd != -1 && this.alphaHelixEnd - this.alphaHelixStart > 4) {
            LOG.info("Last Alpha: " + this.alphaHelixStart + ":" +  this.alphaHelixEnd); 
            chain.createHelix(this.alphaHelixStart + 1, this.alphaHelixEnd - 1);
        }

        if (this.piHelixEnd != -1 && this.piHelixEnd - this.piHelixStart > 5) {
            LOG.info("Last Pi: " + this.piHelixStart + ":" +  this.piHelixEnd); 
            chain.createHelix(this.piHelixStart + 1, this.piHelixEnd - 1);
        }

        if (this.strandStart != -1 && this.strandEnd - this.strandStart > 0) {
            chain.createStrand(this.strandStart, this.strandEnd);
        }

        this.resetEndpoints();
    }

    public void storeProperties(OutputStream out) throws IOException {
        String header = "HBond Analysis Parameters";

        this.properties.store(out, header);
    }

    public void loadProperties(InputStream in) throws IOException {
        this.properties.load(in); 
    }

    public String toString() {
        return this.properties.toString();
    }

}
