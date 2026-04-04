package tailor.experiment.description;

public class AtomDistanceDescription {
	
	private final double distance;
	
	private final DescriptionPath atomDescriptionA;
	
	private final DescriptionPath atomDescriptionB;

	public AtomDistanceDescription(double distance, DescriptionPath atomDescriptionA, DescriptionPath atomDescriptionB) {
		this.distance = distance;
		this.atomDescriptionA = atomDescriptionA;
		this.atomDescriptionB = atomDescriptionB;
	}

	public double getDistance() {
		return distance;
	}

	public DescriptionPath getAtomDescriptionA() {
		return atomDescriptionA;
	}

	public DescriptionPath getAtomDescriptionB() {
		return atomDescriptionB;
	}

}
