# test the list reader

import sys
#sys.path.append("../Tailor")

from Tailor.Description import constructSelection
from Tailor.Measure import DistanceMeasure
from Tailor.Description import ChainDescription
from Tailor.ResultParser import ExampleDescription, generateExamples

filename = sys.argv[1]
pdbdir = sys.argv[2]

def parse(line):
    pdbid, chain, start, stop = line.split(".")
    return ExampleDescription(pdbid, chain, start, stop)

abstractDescription = ChainDescription({"chainID" : " "})
abstractDescription.createResidues(3)

o = constructSelection(residuePosition=1, atomName="O")
n = constructSelection(residuePosition=3, atomName="N")
measures = [DistanceMeasure(o, n)]

for e in generateExamples(filename, pdbdir, parse):
    for measure in measures:
        # XXX hack!
        print(measure.measure(e))
