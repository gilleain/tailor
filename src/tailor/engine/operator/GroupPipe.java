package tailor.engine.operator;

import java.util.Iterator;

import tailor.api.Operator;
import tailor.structure.Chain;
import tops.translation.model.Group;

public class GroupPipe  {
	
	private Iterator<Group> groupIterator;	// TODO - convert to true stream
	private Chain chain; // TODO - should this be a specialised 'ChainSource'?
	
	public GroupPipe(Chain chain) {
		this.groupIterator = null;
		this.chain = chain;
	}


	public Group getNext() {
		return groupIterator.next();
	}

	public boolean hasNext() {
		if (groupIterator == null) {
			groupIterator = chain.getGroups().iterator();
		}
		return groupIterator.hasNext();
	}

	public String getSourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerSource(Operator operator) {
		// TODO Auto-generated method stub
		
	}

	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
