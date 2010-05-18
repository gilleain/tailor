import sys

from Tailor.Description import ChainDescription
from Tailor.DataSource import structureFromFile
from Tailor.Engine import Matcher
from Tailor.Measure import DistanceMeasure

# read in the structure
filepath = sys.argv[1]
structure = structureFromFile(filepath)

# create a simple description
description = ChainDescription({"chainType" : "Protein"})
description.createResidues(3)

# create a simple measure
o = description.selectResidue(1).selectAtom("O")
n = description.selectResidue(3).selectAtom("N")
distanceMeasure = DistanceMeasure(o, n)

# match and measure
matcher = Matcher(description)
for fragment in matcher.findAll(structure):
    print fragment.chainID, "".join([str(r) for r in fragment]), distanceMeasure.measure(fragment)

