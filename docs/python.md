# Usage: python

This is a quick overview of how to use the python api.

## Describe a motif

The first step is to describe the motif to search for. The description is in two parts; the structure of the motif, and the conditions. The structure can be as simple as a chain of a few residues or could be multiple chains, each with more than one residue.

```
from Tailor.Description import ChainDescription
my_motif = ChainDescription({"chainID" : "my_motif"})
my_motif.createResidues(3)
```

The conditions (also known as constraints) are what really makes the motif description. Each condition is attached to part of the motif structure.

```
my_motif.createPhiBoundCondition(2, -90, 30)
my_motif.createPsiBoundCondition(2,  90, 30)
```

## Define some measurements

The point of tailor is to measure parts of structure; to do this a list of measures need to be defined. Each measure produces a measurement, in the same order, to produce a list of results.

```
from Tailor.Measure import DistanceMeasure

firstN = my_motif.selectResidue(1).selectAtom("N")
thirdO = my_motif.selectResidue(3).selectAtom("O")

distanceNOMeasure = DistanceMeasure(firstN, thirdO)
```

## Run on a data source

A data source is a database or flat file directory of crystal structures. For example, a single pdb file is a very simple data source. The same motif can be run on several different sources, or several motifs used on one or more sources.

```
import sys
from Tailor.Run import Run, Pipe

pipe = Pipe(my_motif, [distanceNOMeasure], sys.stdout)
run = Run("/home/pdb_directory", [], [pipe])
run.run()
```

