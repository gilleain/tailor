package tailor.engine.operator;

import java.util.Iterator;

import tailor.api.Operator;
import tailor.api.Source;
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

	@Override
	public String getSourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerSource(Operator operator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
