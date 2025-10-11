package aigen.description;

import java.util.Map;

import aigen.feature.Feature;

class StructureDescription extends Description {
	private String name;
	private String levelCode;

	public StructureDescription(Map<String, Object> propertyConditions, String name) {
		super(propertyConditions);
		this.name = name;
		this.levelCode = "S";
	}

	public void addChain(ChainDescription chainDescription) {
		children.add(chainDescription);
	}

	public ChainDescription selectChain(String chainID) {
		for (Description desc : children) {
			ChainDescription chain = (ChainDescription) desc;
			if (chainID.equals(chain.getProperty("chainID"))) {
				return chain.copy();
			}
		}
		return null;
	}

	public ChainDescription selectChainType(String chainType) {
		for (Description desc : children) {
			ChainDescription chain = (ChainDescription) desc;
			if (chainType.equals(chain.getProperty("chainType"))) {
				return chain.copy();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return name != null ? name : "?";
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
}
