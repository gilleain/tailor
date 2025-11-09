package tailor.measurement;

import tailor.description.Description;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.match.Match;


/**
 * @author maclean
 *
 */
public class HBondMeasure extends GeometricMeasure implements Measure<HBondMeasurement> {
    
    private final Description donorAtomDescription;
    
    private final Description hydrogenAtomDescription;
    
    private final Description acceptorAtomDescription;
    
    private final Description attachedAtomDescription;
    
    public HBondMeasure(String name, Description donorAtomDescription,
            Description hydrogenAtomDescription,
            Description acceptorAtomDescription,
            Description attachedAtomDescription) {
        super(name);
        this.donorAtomDescription = donorAtomDescription;
        this.hydrogenAtomDescription = hydrogenAtomDescription;
        this.acceptorAtomDescription = acceptorAtomDescription;
        this.attachedAtomDescription = attachedAtomDescription;
    }

    @Override
    public HBondMeasurement measure(Match match) {
        Vector d = getPoint(donorAtomDescription, match);
        Vector h = getPoint(hydrogenAtomDescription, match);
        Vector a = getPoint(acceptorAtomDescription, match);
        Vector aa = getPoint(attachedAtomDescription, match);
        
        double haDistance = Geometry.distance(h, a);
        double dhaAngle = Geometry.angle(d, h, a);
        double haaAngle = Geometry.angle(h, a, aa);
        
        return new HBondMeasurement(haDistance, dhaAngle, haaAngle);
    }

    public Description getDonorAtomDescription() {
		return this.donorAtomDescription;
	}

	public Description getHydrogenAtomDescription() {
		return this.hydrogenAtomDescription;
	}

	public Description getAcceptorAtomDescription() {
		return this.acceptorAtomDescription;
	}

	public Description getAttachedAtomDescription() {
		return this.attachedAtomDescription;
	}

	public boolean contains(Description d) {
		return this.acceptorAtomDescription.contains(d)
				|| this.attachedAtomDescription.contains(d)
				|| this.donorAtomDescription.contains(d)
				|| this.hydrogenAtomDescription.contains(d);
	}
	

	@Override
	public int getNumberOfColumns() {
		return 3;
	}

	@Override
	public String[] getColumnHeaders() {
		String name = getName();
		if (name == null) {
			name = "HBond";
		}
		return new String[] { String.format("%s:D_H", name), String.format("%s:DHA", name),
				String.format("%s:HAA", name) };
	}

	@Override
	public String[] getFormatStrings() {
		return new String[] { "%.2f", "%.2f", "%.2f" };
	}
	
	@Override
	public String toString() {
		return String.format("HB(%s, %s, %s, %s)", 
				this.donorAtomDescription, this.hydrogenAtomDescription,
				this.acceptorAtomDescription, this.attachedAtomDescription);
	}

}
