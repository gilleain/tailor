package aigen;

import java.util.Map;

class AtomDescription extends Description {
	private String levelCode;

	public AtomDescription(Map<String, Object> propertyConditions) {
		super(propertyConditions);
		this.levelCode = "A";
	}

	@Override
	public String toString() {
		Object name = getProperty("name");
		return name != null ? name.toString() : "?";
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
