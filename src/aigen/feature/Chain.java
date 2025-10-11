package aigen.feature;

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
