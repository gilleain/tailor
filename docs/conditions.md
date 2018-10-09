# Conditions

A condition could also be called a constraint - it is a general way to make the description more specific. They are also closely related to measures since a condition has to make a measurement in order to check if it is valid.

That is all quite abstract, so here is a concrete example:

     Description a = ...
     Description b = ...
     DistanceBoundCondition distance = 
         new DistanceBoundCondition("dON", a, b, 3.0, 1.0);
     chain.addCondition(distance);

The two `Description` instances (a and b) at the beginning are just descriptions of atoms in a motif. These are passed to the condition, along with two geometric parameters : the center of the bound (3.0) and the range (1.0). This condition therefore applies to certain atoms within 2-4&Aring; of each other.

It can be easier for some conditions to use the convenience methods in the
`DescriptionFactory` class. These will find the right atom descriptions in the current description and will also add the resulting condition to the right part of the description.

So, for example, to easily add a TorsionBound on phi and psi to a particular residue:

    DescriptionFactory factory = ...
    factory.createPhiCondition(-90.0, 30.0, 2);
    factory.createPsiCondition(  0.0, 30.0, 2);

Now, the description will only match fragments where the second residue has a &gamma;<sub>R</sub> conformation. A complete list of the convenience methods available is in the <a href="function_reference.html">function_reference</a>.
