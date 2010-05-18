import sys

from Tailor.Condition import HBondCondition, NoneCombiner
from Tailor.Description import ChainDescription, AtomDescription
from Tailor.Measure import PropertyMeasure, DistanceMeasure
from Tailor.Run import Run, Pipe

path = sys.argv[1]

maxHO = 3.5
minHOC = 90.0
minNHO = 90.0

helixTerm = ChainDescription({"chainID" : "h"})
helixTerm.createResidues(9)
helixTerm.createBackboneHBondCondition(6, 2, maxHO, minHOC, minNHO)
helixTerm.createBackboneHBondCondition(7, 3, maxHO, minHOC, minNHO)

helixTerm.getResidue(8).addPropertyCondition("resname", "PRO")
helixTerm.getResidue(8).children.pop()
helixTerm.getResidue(8).children.append(AtomDescription({"name" : "2HD"}))

measures = []
measures.extend(helixTerm.createPhiPsiMeasures([6, 7, 8]))
o = helixTerm.selectResidue(5).selectAtom("O")
h = helixTerm.selectResidue(8).selectAtom("2HD")
measures.append(DistanceMeasure(o, h))
pipes = [Pipe(helixTerm, measures, sys.stdout)]
run = Run(pipes, path)
run.run()
