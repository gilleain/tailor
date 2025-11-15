package tailor.measurement;

import tailor.description.Description;
import tailor.match.Match;

public class PropertyMeasure implements Measure<PropertyMeasurement> {
	 private Description description;
	 private String propertyName;
	 private Class valueType; // XXX TODO  -split into StringPropertyType, DoublePropertyType, etc

	 public PropertyMeasure(Description description, String propertyName, Class valueType) {
	     this.description = description;
	     this.propertyName = propertyName;
	     this.valueType = valueType;
	 }
	 
	 

	 @Override
	 public PropertyMeasurement measure(Match match) {
//	     try {
	    	 return new PropertyMeasurement("");	//TODO
//	         Feature f = ((Feature) feature).findFeature(this.description);
//	         java.lang.reflect.Field field = f.getClass().getDeclaredField(this.propertyName);
//	         field.setAccessible(true);
//	         return field.get(f);
//	     } catch (NoSuchFieldException | IllegalAccessException e) {
//	         return "None";
//	     } catch (DescriptionException e) {
//	         return "!";
//	     }
	 }

	 @Override
	 public int getNumberOfColumns() {
	     return 1;
	 }

	 @Override
	 public String[] getColumnHeaders() {
	     String name = getName();
	     if (name == null) {
	         return new String[] { this.propertyName };
	     } else {
	         return new String[] { name };
	     }
	 }

	 @Override
	 public String[] getFormatStrings() {
	     if (this.valueType == Integer.class || this.valueType == int.class) {
	         return new String[] { "%d" };
	     } else if (this.valueType == Float.class || this.valueType == float.class || 
	                this.valueType == Double.class || this.valueType == double.class) {
	         return new String[] { "%.2f" };
	     } else {
	         return new String[] { "%s" };
	     }
	 }

	@Override
	public String getName() {
		return this.propertyName;
	}
	
	@Override
	public String toString() {
		return String.format("%s=%s", this.description, this.propertyName);
	}

}
