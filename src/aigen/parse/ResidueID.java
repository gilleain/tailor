package aigen.parse;

class ResidueID {
    private int resseq;
    private String icode;
    
    public ResidueID(int resseq, String icode) {
        this.resseq = resseq;
        this.icode = icode;
    }
    
    public int getResseq() {
        return resseq;
    }
    
    public String getIcode() {
        return icode;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ResidueID other = (ResidueID) obj;
        return resseq == other.resseq && icode.equals(other.icode);
    }
    
    @Override
    public int hashCode() {
        return 31 * resseq + icode.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("(%d, %s)", resseq, icode);
    }
}