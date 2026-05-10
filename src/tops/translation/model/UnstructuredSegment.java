package tops.translation.model;

public class UnstructuredSegment extends BackboneSegment {

    public UnstructuredSegment() {
        super();
    }

    public UnstructuredSegment(Residue r) {
        super(r);
    }

    @Override
    public String toString() {
        return "Unstructured (" + this.number + ") from " + this.firstResidue().getPDBNumber()
                + " to " + this.lastResidue().getPDBNumber();
    }
    
    @Override
	public String toCompactString() {
		return "U(" + this.number + ")[" + this.firstResidue().getPDBNumber() + " to " + this.lastResidue().getPDBNumber() + "]";
	}

    @Override
    public String toFullString() {
        return this.toString();
    }

	@Override
	public Type getType() {
		return Type.OTHER;
	}
	
}
