package tailor.experiment.operator;

import java.util.List;

import tailor.experiment.api.Operator;
import tailor.experiment.api.Sink;
import tailor.experiment.api.Source;
import tailor.experiment.condition.AtomDistanceCondition;
import tailor.structure.Atom;

/**
 * TODO - why is this distinct from Combine and Filter? We are effectively doing a Combine and Filtering each one?
 */
public class ExpandByCondition implements Operator {
	
	private AtomDistanceCondition condition;	// make more general
	
	private Source<Atom> sourceA;
	
	private Source<Atom> sourceB;
	
	private Sink<List<Atom>> sink;

	public ExpandByCondition(
			AtomDistanceCondition condition, Source<Atom> sourceA, Source<Atom> sourceB, Sink<List<Atom>> sink) {
		this.condition = condition;
		this.sourceA = sourceA;
		this.sink = sink;
	}
	
	public void run() { 
		while (sourceA.hasNext()) {
			Atom sourceAAtom = sourceA.getNext();
			while (sourceB.hasNext()) {
				Atom sourceBAtom = sourceB.getNext();
				if (condition.accept(sourceAAtom, sourceBAtom)) {
					sink.put(List.of(sourceAAtom, sourceBAtom));
				}
			}
		}
	}
}
