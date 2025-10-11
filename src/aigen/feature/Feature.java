package aigen.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aigen.description.Description;
import aigen.description.DescriptionException;
import aigen.geometry.Vector;

public class Feature implements Iterable<Feature> {
 protected List<Feature> subFeatures;
 protected String levelCode;
 protected Integer position;

 public Feature() {
     this.subFeatures = new ArrayList<>();
 }

 public void add(Feature feature) {
     this.subFeatures.add(feature);
 }

 public Iterator<Feature> listFeaturesThatMatch(Description description) {
     return searchForFeaturesThatMatch(description).iterator();
 }

 public List<Feature> searchForFeaturesThatMatch(Description description) {
     List<Feature> matchingFeatures = new ArrayList<>();
     List<Feature> subFeatures = getSubFeaturesBelowLevel(description.getLevelCode());
     int descriptionLength = description.length();

     Class<? extends Feature> featureType = description.getFeatureType();

     ChunkIterator<Feature> chunks = new ChunkIterator<>(subFeatures, descriptionLength);
     while (chunks.hasNext()) {
         List<Feature> subFeatureList = chunks.next();
         try {
             Feature feature = featureType.getDeclaredConstructor(String.class)
                 .newInstance(description.getName());
             
             for (int i = 0; i < subFeatureList.size(); i++) {
                 Feature subFeature = subFeatureList.get(i);
                 Feature subFeatureCopy = subFeature.copy();
                 subFeatureCopy.position = i + 1;
                 feature.add(subFeatureCopy);
             }

             if (description.describes(feature)) {
                 matchingFeatures.add(feature);
             }
         } catch (Exception e) {
             throw new RuntimeException("Failed to create feature instance", e);
         }
     }

     return matchingFeatures;
 }

 public List<Feature> getSubFeaturesBelowLevel(String levelCode) {
     if (this.levelCode.equals(levelCode) || this.levelCode.equals("A")) {
         return this.subFeatures;
     } else {
         for (Feature subFeature : this.subFeatures) {
             return subFeature.getSubFeaturesBelowLevel(levelCode);
         }
         return new ArrayList<>();
     }
 }

 public Feature findFeature(Description description) throws DescriptionException {
     if (this.levelCode.equals(description.getLevelCode())) {
         if (description.describes(this)) {
             Feature copySelf = this.copy();
             copySelf.subFeatures = new ArrayList<>();
             
             for (Description subDescription : description) {
                 Feature matchingSubFeature = this.searchSubFeatures(subDescription);
                 if (matchingSubFeature != null) {
                     copySelf.add(matchingSubFeature);
                 } else {
                     throw new DescriptionException("No match for description " + subDescription);
                 }
             }
             return copySelf;
         }
     } else {
         return this.searchSubFeatures(description);
     }
     return null;
 }

 public Feature searchSubFeatures(Description description) throws DescriptionException {
     for (Feature subFeature : this) {
         Feature result = subFeature.findFeature(description);
         if (result != null) {
             return result;
         }
     }
     return null;
 }

 public int size() {
     return subFeatures.size();
 }

 @Override
 public Iterator<Feature> iterator() {
     return subFeatures.iterator();
 }

 public Feature get(int index) {
     return subFeatures.get(index);
 }

 public Vector getCenter() {
     if (size() > 0) {
         Vector total = new Vector(0, 0, 0);
         for (Feature subFeature : subFeatures) {
             total = total.add(subFeature.getCenter());
         }
         return total.divide(size());
     } else {
         if (levelCode.equals("A")) {
             return getAtomCenter();
         } else {
             return new Vector(0, 0, 0);
         }
     }
 }

 protected Vector getAtomCenter() {
     throw new UnsupportedOperationException("getAtomCenter must be implemented by Atom class");
 }

 protected Feature copy() {
     try {
         return (Feature) super.clone();
     } catch (CloneNotSupportedException e) {
         throw new RuntimeException("Clone not supported", e);
     }
 }
}
