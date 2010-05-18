import sys

from Tailor.Condition import HBondCondition, NoneCombiner
from Tailor.Description import ChainDescription, AtomDescription
from Tailor.Measure import PropertyMeasure, DistanceMeasure
from Tailor.Run import Run, Pipe

path = sys.argv[1]

maxHO = 2.5
minHOC = 90.0
minNHO = 120.0

helixTerm = ChainDescription({"chainID" : "h"})
helixTerm.createResidues(8)
helixTerm.createBackboneHBondCondition(5, 1, maxHO, minHOC, minNHO)
helixTerm.createBackboneHBondCondition(6, 2, maxHO, minHOC, minNHO)

helixTerm.createPhiBoundCondition(2, -90, 60)
helixTerm.createPsiBoundCondition(2,   0, 60)

helixTerm.createPhiBoundCondition(3, -90, 60)
helixTerm.createPsiBoundCondition(3,   0, 60)

helixTerm.createPhiBoundCondition(4, -90, 60)
helixTerm.createPsiBoundCondition(4,   0, 60)

hbond = helixTerm.createBackboneHBondCondition(7, 3, maxHO, minHOC, minNHO)
hbond.negate()

# for proline searches
#helixTerm.getResidue(7).addPropertyCondition("resname", "PRO")
#helixTerm.getResidue(7).children.pop()
#helixTerm.getResidue(7).children.append(AtomDescription({"name" : "2HD"}))

measures = []
measures.extend(helixTerm.createPhiPsiMeasures([5, 6, 7]))
pipes = [Pipe(helixTerm, measures, sys.stdout)]
run = Run(pipes, path)
run.run()
