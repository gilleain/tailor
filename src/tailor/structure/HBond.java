package tailor.structure;


public class HBond implements Comparable<HBond> {
    private Group donor;
    private Group acceptor;
    private double distance;
    private double nhoAngle;
    private double hocAngle;

    public HBond(Group donor, Group acceptor, double distance, double nhoAngle, double hocAngle) {
        this.donor = donor;
        this.acceptor = acceptor;
        this.distance = distance;
        this.nhoAngle = nhoAngle;
        this.hocAngle = hocAngle;
    }

    //sort by donor, then acceptor (well, why not, eh?)
    public int compareTo(HBond other) {
        int c = Integer.valueOf(this.donor.getAbsoluteNumber()).compareTo(Integer.valueOf(other.donor.getAbsoluteNumber()));
        if (c == 0) {
            return Integer.valueOf(this.acceptor.getAbsoluteNumber()).compareTo(Integer.valueOf(other.acceptor.getAbsoluteNumber()));
        } else {
            return c;
        }
    }

    public Group getPartner(Group residue) {
        if (residue == this.donor) {
            return this.acceptor;
        } else {
            return this.donor;
        }
    }

    public boolean contains(Group residue) {
        return this.donor == residue || this.acceptor == residue;
    }

    public int getResidueSeparation() {
        return Math.abs(this.donor.getAbsoluteNumber() - this.acceptor.getAbsoluteNumber());
    }

    public boolean hasHelixResidueSeparation() {
        return this.getResidueSeparation() == 4;
    }

    public boolean hasSheetResidueSeparation() {
        return this.getResidueSeparation() > 4;
    }

    public Group acceptor() {
        return this.acceptor;
    }

    public boolean residueIsAcceptor(Group residue) {
        return this.acceptor == residue;
    }

    public Group donor() {
        return this.donor;
    }

    public boolean residueIsDonor(Group residue) {
        return this.donor == residue;
    }

    public String toFullString() {
    	return String.format("%3s - %3s (%4.2f, %6.2f, %6.2f)", this.donor, this.acceptor, this.distance, this.nhoAngle, this.hocAngle);    	
    }
    
    public String toString() {
        return "";
    }
}
