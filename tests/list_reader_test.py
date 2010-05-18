# test the list reader

import sys
from Tailor.Engine import TmpDistanceMeasure, constructSelection
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
measures = [TmpDistanceMeasure(o, n)]

for e in generateExamples(filename, pdbdir, parse):
    for measure in measures:
        # XXX hack!
        print measure.measure(e[0][0])
