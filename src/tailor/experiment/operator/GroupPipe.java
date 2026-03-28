package tailor.experiment.operator;

import java.util.Iterator;

import tailor.experiment.api.Source;
import tailor.structure.Chain;
import tailor.structure.Group;

public class GroupPipe implements Source<Group> {
	
	private Iterator<Group> groupIterator;	// TODO - convert to true stream
	private Chain chain; // TODO - should this be a specialised 'ChainSource'?
	
	public GroupPipe(Chain chain) {
		this.groupIterator = null;
		this.chain = chain;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public Group getNext() {
		return groupIterator.next();
	}

	@Override
	public boolean hasNext() {
		if (groupIterator == null) {
			groupIterator = chain.getGroups().iterator();
		}
		return groupIterator.hasNext();
	}

}
