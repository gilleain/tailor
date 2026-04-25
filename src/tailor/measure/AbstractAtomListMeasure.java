package tailor.measure;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import tailor.api.AtomListMeasure;
import tailor.condition.AtomMatcher;
import tailor.condition.AtomMatcher.Match;
import tailor.condition.AtomPartition;
import tailor.condition.LabelPartition;
import tailor.description.DescriptionPath;
import tailor.description.GroupDescription;
import tailor.structure.Atom;

public abstract class AbstractAtomListMeasure implements AtomListMeasure {
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AtomMatcher atomMatcher;
	
	private final List<DescriptionPath> atomDescriptionPaths;
	
	private String name;
	
	public AbstractAtomListMeasure(String name, DescriptionPath... descriptionPaths) {
		this(name, Arrays.asList(descriptionPaths));
	}
		
	public AbstractAtomListMeasure(String name, List<DescriptionPath> descriptionPaths) {	
		this.name = name;
		this.atomDescriptionPaths = descriptionPaths;
		this.atomMatcher = new AtomMatcher(LabelPartition.fromDescriptionPaths(descriptionPaths));
	}
	
	protected abstract DoubleMeasurement measure(List<Atom> atoms);
	
	public DoubleMeasurement measure(AtomPartition atomPartition) {
		Optional<Match> match = atomMatcher.containedIn(atomPartition);
		if (match.isPresent()) {
			return measure(match.get().getAtoms());
		} else {
			logger.fine("NO match " + atomMatcher + " to " + atomPartition);
			return new DoubleMeasurement();
		}
	}
	
	public List<GroupDescription> getGroupDescriptions() {
		return atomDescriptionPaths.stream().map(DescriptionPath::getGroupDescription).toList();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
