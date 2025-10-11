package aigen.description;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aigen.feature.Feature;

public class ResidueDescription extends Description {
	private String levelCode;

	public ResidueDescription(Map<String, Object> propertyConditions) {
		super(propertyConditions);
		this.levelCode = "R";
	}

	public ResidueDescription copy() {
		ResidueDescription copy = new ResidueDescription(new HashMap<>(this.properties));
		copy.children = new ArrayList<>(this.children);
		copy.conditions = new ArrayList<>(this.conditions);
		return copy;
	}

	public void addAtom(String atomName) {
		Map<String, Object> props = new HashMap<>();
		props.put("name", atomName);
		addSubDescription(new AtomDescription(props));
	}

	public AtomDescription getAtom(String atomName) {
		for (Description desc : children) {
			AtomDescription atom = (AtomDescription) desc;
			if (atomName.equals(atom.getProperty("name"))) {
				return atom;
			}
		}
		return null;
	}

	public ResidueDescription selectAtom(String atomName) {
		children.clear();
		addAtom(atomName);
		return this;
	}

	public String getResname() {
		Object name = getProperty("name");
		return name != null ? name.toString() : "Any";
	}
	
	public int getPosition() {
		Integer p = (Integer) getProperty("position");
		return p == null? -1 : p;	// TODO
	}

	public String getPositionString() {
		Integer p = (Integer) getProperty("position");
		if (p == null)
			return "?";

		if (p < 1) {
			return "i - " + Math.abs(p - 1);
		} else if (p == 1) {
			return "i";
		} else {
			return "i + " + (p - 1);
		}
	}

	@Override
	public String toString() {
		return getPositionString() + " " + children.toString();
	}

	@Override
	public String getLevelCode() {
		return levelCode;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean describes(Feature feature) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<? extends Feature> getFeatureType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<AtomDescription> getAtomDescriptions() {
		return null;	// TODO
	}
}