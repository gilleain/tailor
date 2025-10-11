package aigen;

import java.util.*;

// Utility class for chunking operations
class ChunkIterator<T> implements Iterator<List<T>> {
    private final List<T> list;
    private final int chunkSize;
    private int currentIndex = 0;

    public ChunkIterator(List<T> list, int chunkSize) {
        this.list = list;
        this.chunkSize = chunkSize;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < list.size();
    }

    @Override
    public List<T> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        int end = Math.min(currentIndex + chunkSize, list.size());
        List<T> chunk = list.subList(currentIndex, end);
        currentIndex++;
        return chunk;
    }
}

// Base Feature class
public class Feature implements Iterable<Feature> {
    protected List<Feature> subFeatures;
    protected String levelCode;
    protected Integer position;

    public Feature() {
        this.subFeatures = new ArrayList<>();
    }

    public void add(Feature feature) {
        this.subFeatures.add(feature);
    }

    public Iterator<Feature> listFeaturesThatMatch(Description description) {
        return searchForFeaturesThatMatch(description).iterator();
    }

    public List<Feature> searchForFeaturesThatMatch(Description description) {
        List<Feature> matchingFeatures = new ArrayList<>();
        List<Feature> subFeatures = getSubFeaturesBelowLevel(description.getLevelCode());
        int descriptionLength = description.length();

        Class<? extends Feature> featureType = description.getFeatureType();

        ChunkIterator<Feature> chunks = new ChunkIterator<>(subFeatures, descriptionLength);
        int index = 0;
        while (chunks.hasNext()) {
            List<Feature> subFeatureList = chunks.next();
            try {
                Feature feature = featureType.getDeclaredConstructor(String.class)
                    .newInstance(description.getName());
                
                for (int i = 0; i < subFeatureList.size(); i++) {
                    Feature subFeature = subFeatureList.get(i);
                    Feature subFeatureCopy = subFeature.copy();
                    subFeatureCopy.position = i + 1;
                    feature.add(subFeatureCopy);
                }

                if (description.describes(feature)) {
                    matchingFeatures.add(feature);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create feature instance", e);
            }
        }

        return matchingFeatures;
    }

    public List<Feature> getSubFeaturesBelowLevel(String levelCode) {
        if (this.levelCode.equals(levelCode) || this.levelCode.equals("A")) {
            return this.subFeatures;
        } else {
            for (Feature subFeature : this.subFeatures) {
                return subFeature.getSubFeaturesBelowLevel(levelCode);
            }
            return new ArrayList<>();
        }
    }

    public Feature findFeature(Description description) throws DescriptionException {
        if (this.levelCode.equals(description.getLevelCode())) {
            if (description.describes(this)) {
                Feature copySelf = this.copy();
                copySelf.subFeatures = new ArrayList<>();
                
                for (Description subDescription : description) {
                    Feature matchingSubFeature = this.searchSubFeatures(subDescription);
                    if (matchingSubFeature != null) {
                        copySelf.add(matchingSubFeature);
                    } else {
                        throw new DescriptionException("No match for description " + subDescription);
                    }
                }
                return copySelf;
            }
        } else {
            return this.searchSubFeatures(description);
        }
        return null;
    }

    public Feature searchSubFeatures(Description description) {
        for (Feature subFeature : this) {
            Feature result = subFeature.findFeature(description);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public int size() {
        return subFeatures.size();
    }

    @Override
    public Iterator<Feature> iterator() {
        return subFeatures.iterator();
    }

    public Feature get(int index) {
        return subFeatures.get(index);
    }

    public Vector getCenter() {
        if (size() > 0) {
            Vector total = new Vector(0, 0, 0);
            for (Feature subFeature : subFeatures) {
                total = total.add(subFeature.getCenter());
            }
            return total.divide(size());
        } else {
            if (levelCode.equals("A")) {
                return getAtomCenter();
            } else {
                return new Vector(0, 0, 0);
            }
        }
    }

    protected Vector getAtomCenter() {
        throw new UnsupportedOperationException("getAtomCenter must be implemented by Atom class");
    }

    protected Feature copy() {
        try {
            return (Feature) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone not supported", e);
        }
    }
}

// Structure class
class Structure extends Feature {
    private String id;

    public Structure(String id) {
        super();
        this.id = id;
        this.levelCode = "S";
    }

    public List<Chain> chainsWithID(String chainID) {
        List<Chain> chains = new ArrayList<>();
        for (Feature model : this) {
            for (Feature chain : model) {
                if (chain instanceof Chain) {
                    Chain c = (Chain) chain;
                    if (chainID.isEmpty() || c.getChainID().equals(chainID)) {
                        chains.add(c);
                    }
                }
            }
        }
        return chains;
    }

    public List<Chain> chainsOfType(String chainType) {
        List<Chain> chainsToReturn = new ArrayList<>();
        for (Feature model : this) {
            for (Feature chain : model) {
                if (chain instanceof Chain) {
                    Chain c = (Chain) chain;
                    if (chainType.isEmpty() || c.getChainType().equals(chainType)) {
                        chainsToReturn.add(c);
                    }
                }
            }
        }
        return chainsToReturn;
    }

    @Override
    public String toString() {
        return id;
    }
}

// Model class
class Model extends Feature {
    private int number;

    public Model(int number) {
        super();
        this.number = number;
        this.levelCode = "M";
    }

    @Override
    public String toString() {
        return String.valueOf(number);
    }
}

// Chain class
class Chain extends Feature {
    private String chainID;
    private String chainType;

    public Chain(String chainID) {
        this(chainID, null);
    }

    public Chain(String chainID, String chainType) {
        super();
        this.chainID = chainID;
        this.chainType = chainType;
        this.levelCode = "C";
    }

    public String getChainID() {
        return chainID;
    }

    public String getChainType() {
        return chainType;
    }

    public Residue getFirst() {
        return (Residue) subFeatures.get(0);
    }

    public Residue getLast() {
        return (Residue) subFeatures.get(subFeatures.size() - 1);
    }

    public String getResidueRange() {
        if (size() > 1) {
            return String.format("%s-%s", getFirst().getNumber(), getLast().getNumber());
        } else {
            return String.valueOf(getFirst().getNumber());
        }
    }

    public Residue getResidueNumber(int residueNumber) {
        for (Feature r : this) {
            if (r instanceof Residue) {
                Residue residue = (Residue) r;
                if (residue.getNumber() == residueNumber) {
                    return residue;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", chainType, chainID, getResidueRange());
    }
}

// Residue class
class Residue extends Feature {
    private ResidueID residueID;
    private String resname;
    private String segID;

    public Residue(Object residueID, String resname) {
        this(residueID, resname, null);
    }

    public Residue(Object residueID, String resname, String segID) {
        super();
        if (residueID instanceof ResidueID) {
            this.residueID = (ResidueID) residueID;
        } else if (residueID instanceof Integer) {
            this.residueID = new ResidueID((Integer) residueID, "");
        } else {
            throw new IllegalArgumentException("Invalid residueID type");
        }
        this.resname = resname;
        this.segID = segID;
        this.levelCode = "R";
    }

    public int getNumber() {
        return residueID.number;
    }

    public Atom getAtom(String atomName) throws IllegalArgumentException {
        for (Feature a : this) {
            if (a instanceof Atom) {
                Atom atom = (Atom) a;
                if (atom.getName().equals(atomName)) {
                    return atom;
                }
            }
        }
        throw new IllegalArgumentException("No atom with name " + atomName);
    }

    public Vector getAtomPosition(String atomName) {
        return getAtom(atomName).getAtomCenter();
    }

    public String toFullString() {
        return toString() + subFeatures.toString();
    }

    @Override
    public String toString() {
        return String.format("%s%d%s", resname, residueID.number, residueID.insertionCode);
    }

    static class ResidueID {
        int number;
        String insertionCode;

        public ResidueID(int number, String insertionCode) {
            this.number = number;
            this.insertionCode = insertionCode;
        }
    }
}

// Atom class
class Atom extends Feature {
    private String name;
    private Vector coord;
    private Double bFactor;
    private double occupancy;
    private String altloc;

    public Atom(String name, Vector coord) {
        this(name, coord, null, 1.0, null);
    }

    public Atom(String name, Vector coord, Double bFactor, double occupancy, String altloc) {
        super();
        this.name = name.trim();
        this.coord = coord;
        this.bFactor = bFactor;
        this.occupancy = occupancy;
        this.altloc = altloc;
        this.levelCode = "A";
    }

    public String getName() {
        return name;
    }

    @Override
    protected Vector getAtomCenter() {
        return coord;
    }

    @Override
    public String toString() {
        return name;
    }
}

// Vector class (placeholder - implement based on your Geometry module)
class Vector {
    private double x, y, z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector divide(int divisor) {
        return new Vector(this.x / divisor, this.y / divisor, this.z / divisor);
    }
}

// Description class (placeholder - implement based on your Description module)
abstract class Description implements Iterable<Description> {
    public abstract String getLevelCode();
    public abstract String getName();
    public abstract int length();
    public abstract boolean describes(Feature feature);
    public abstract Class<? extends Feature> getFeatureType();
    
    @Override
    public Iterator<Description> iterator() {
        return Collections.emptyIterator();
    }
}

// Custom exception
class DescriptionException extends Exception {
    public DescriptionException(String message) {
        super(message);
    }
}