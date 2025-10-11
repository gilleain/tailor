package aigen.measure;

import aigen.description.DescriptionException;
import aigen.feature.Feature;

class PropertyMeasure extends Measure {
	 private String description;
	 private String propertyName;
	 private Class<?> valueType;

	 public PropertyMeasure(String description, String propertyName, Class<?> valueType) {
	     this.description = description;
	     this.propertyName = propertyName;
	     this.valueType = valueType;
	 }

	 @Override
	 public Object measure(Object feature) {
	     try {
	         Feature f = ((Feature) feature).findFeature(this.description);
	         java.lang.reflect.Field field = f.getClass().getDeclaredField(this.propertyName);
	         field.setAccessible(true);
	         return field.get(f);
	     } catch (NoSuchFieldException | IllegalAccessException e) {
	         return "None";
	     } catch (DescriptionException e) {
	         return "!";
	     }
	 }

	 @Override
	 public int getNumberOfColumns() {
	     return 1;
	 }

	 @Override
	 public String getColumnHeaders() {
	     String name = getName();
	     if (name == null) {
	         return this.propertyName;
	     } else {
	         return name;
	     }
	 }

	 @Override
	 public String getFormatStrings() {
	     if (this.valueType == Integer.class || this.valueType == int.class) {
	         return "%d";
	     } else if (this.valueType == Float.class || this.valueType == float.class || 
	                this.valueType == Double.class || this.valueType == double.class) {
	         return "%.2f";
	     } else {
	         return "%s";
	     }
	 }

	 @Override
	 public String toString() {
	     return String.format("%s=%s", this.description, this.propertyName);
	 }
	}
