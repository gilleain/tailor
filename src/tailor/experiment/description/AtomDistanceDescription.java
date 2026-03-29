package tailor.experiment.description;

public class AtomDistanceDescription {
	
	private final AtomDescription atomDescriptionA;
	
	private final AtomDescription atomDescriptionB;

	public AtomDistanceDescription(AtomDescription atomDescriptionA, AtomDescription atomDescriptionB) {
		this.atomDescriptionA = atomDescriptionA;
		this.atomDescriptionB = atomDescriptionB;
	}

	public AtomDescription getAtomDescriptionA() {
		return atomDescriptionA;
	}

	public AtomDescription getAtomDescriptionB() {
		return atomDescriptionB;
	}

}
