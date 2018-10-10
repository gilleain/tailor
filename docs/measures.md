# Measures

## Defining a custom measure

### The `Measure` base class

The abstract class `Measure` defines a single abstract method :

    public abstract Measurement measure(Structure structure) throws DescriptionException;

However, this is not especially helpful as a guide for implementing the method itself. It might be more helpful to look at one of the basic classes provided with Tailor : the `DistanceMeasure`.

### The `DistanceMeasure` class

This class simply measures the distance between two atoms. This is actually done in the `measure` method of the class, as seen below.

    public Measurement measure(Structure structure) 
   	                                  throws DescriptionException {
        Vector a = this.descriptionA.findStructureCenter(structure);
        Vector b = this.descriptionB.findStructureCenter(structure);
        
        double distance = Geometry.distance(a, b);
        return new Measurement(this.getName(), new Double(distance));
    }

The class holds a pair of `Description` references - `descriptionA` and `descriptionB` - that it uses to find points. These points (actually, `Vector` objects) are passed to a standard distance method. Finally, the result is wrapped in an instance of a `Measurement`.

### Parameters for the `measure` method

The `structure` parameter passed to the `measure` method is a fragment found by the search for the motif. Any descriptions used by the measure must therefore be able to be found within such a fragment. If there is some problem finding it, the method can throw a
`DescriptionException`.
