package tops.translation.model;

public class Terminus extends BackboneSegment {

    private String label;

    private Type type;

    public Terminus(String label, Type type) {
        this.label = label;
        assert type == Type.NTERMINUS || type == Type.CTERMINUS;
        this.type = type;
    }

    @Override
    public Orientation getOrientation() {
        return Orientation.UP;	// ??
    }

    @Override
    public String toString() {
        return this.label;
    }

    @Override
    public String toFullString() {
        return this.toString();
    }

	@Override
	public String toCompactString() {
		return this.toString();
	}

	@Override
	public Type getType() {
		return type;
	}

}
