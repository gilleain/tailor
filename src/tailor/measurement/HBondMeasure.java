package tailor.measurement;

import tailor.description.Description;
import tailor.geometry.Geometry;
import tailor.geometry.Vector;
import tailor.match.Match;


/**
 * @author maclean
 *
 */
public class HBondMeasure implements Measure<HBondMeasurement> {
    
    private Description donorAtomDescription;
    
    private Description hydrogenAtomDescription;
    
    private Description acceptorAtomDescription;
    
    private Description attachedAtomDescription;
    
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
    
    private Vector getPoint(Description description, Match match) {
        return null;    // TODO
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

    public void setDonorAtomDescription(Description donorAtomDescription) {
		this.donorAtomDescription = donorAtomDescription;
	}

	public void setHydrogenAtomDescription(Description hydrogenAtomDescription) {
		this.hydrogenAtomDescription = hydrogenAtomDescription;
	}

	public void setAcceptorAtomDescription(Description acceptorAtomDescription) {
		this.acceptorAtomDescription = acceptorAtomDescription;
	}

	public void setAttachedAtomDescription(Description attachedAtomDescription) {
		this.attachedAtomDescription = attachedAtomDescription;
	}
	
	public boolean contains(Description d) {
		return this.acceptorAtomDescription.contains(d)
				|| this.attachedAtomDescription.contains(d)
				|| this.donorAtomDescription.contains(d)
				|| this.hydrogenAtomDescription.contains(d);
	}

}
