# Simple complete example

A complete java program, using the api, is shown below.

```java
    import tailor.description.DescriptionFactory;
    import tailor.engine.BasicEngine;
    import tailor.engine.Engine;
    import tailor.engine.Run;

    public class SimpleExample {

        public static void main(String[] args) {

            String path = args[0];
        
            DescriptionFactory factory = new DescriptionFactory();
            factory.addResidues(4);
            factory.addHBondCondition(3.5, 90.0, 90.0, 3, 0);
        
            Run run = new Run(path);
            run.addMeasure(factory.createPhiMeasure("psi2", 2));
            run.addMeasure(factory.createPsiMeasure("phi2", 2));
        
            run.addDescription(factory.getProduct());
        
            Engine engine = new BasicEngine();
            engine.run(run);
        }

    }
```

This code is making a very simple 4-residue description, with the condition that it have a backbone hydrogen bond from the CO of residue i + 4 to the NH of residue i. It then sets two torsion measures - phi and psi of residue 2 and runs against the file(s) represented by `path`.

 Most of these calls are to convenience methods, especially those to the `DescriptionFactory`. For more complex descriptions, the relevant objects can be created directly. Or, for more advanced use, entirely new measures can be defined as classes that implement the `Measure` interface.
