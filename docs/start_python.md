# Using Tailor in python

## Scripting interface

There are two ways to use the modules and classes in Tailor. The first way is to write a script:

```
filepath         = ./data
filenames        = [test.pdb]
motifDescription = PREDEFINED 'RLnest'
measures         = Distance (1.N 3.N)
```

Note that all the parts are essential; even the list of measures. They can appear in any order in the file, and the layout is not important - the equals signs are aligned here for purely aesthetic reasons.

[More information on scripting](scripting_python.md)

## Python API

Alternatively, write a python program that `import`s the modules and uses them directly:

```python
from Tailor.Description import predefined_descriptions
from Tailor.Measure import DistanceMeasure
from Tailor.Script import Run

nest = predefined_descriptions["RLnest"]
n1   = nest.getResidue(1).getAtom("N")
n3   = nest.getResidue(3).getAtom("N")
nn   = DistanceMeasure(n1, n3)
run = Run(nest, "./data", ["test.pdb"], [nn])
run.run()
```

This uses the predefined `Description` called "RLNest", and finds all examples in the file "test.pdb" in the directory called "data". The output goes to `sys.stdout` by default, just as the error goes to `sys.stderr`. 


