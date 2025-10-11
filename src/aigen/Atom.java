package aigen;

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