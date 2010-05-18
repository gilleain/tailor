import sys

from Tailor.Condition import HBondCondition, NoneCombiner
from Tailor.Description import ChainDescription
from Tailor.Measure import PropertyMeasure
from Tailor.Run import Run, Pipe

path = sys.argv[1]

maxHO = 3.5
minHOC = 90.0
minNHO = 90.0

helixTerm = ChainDescription({"chainID" : "h"})
helixTerm.createResidues(12)
helixTerm.createBackboneHBondCondition(6, 2, maxHO, minHOC, minNHO)
helixTerm.createBackboneHBondCondition(7, 3, maxHO, minHOC, minNHO)
helixTerm.createBackboneHBondCondition(8, 4, maxHO, minHOC, minNHO)
helixTerm.createBackboneHBondCondition(9, 5, maxHO, minHOC, minNHO)
helixTerm.createBackboneHBondCondition(10, 6, maxHO, minHOC, minNHO)

helixTerm.getResidue(11).addPropertyCondition("resname", "PRO")
helixTerm.getResidue(11).children.pop()
#antiBond = helixTerm.createBackboneHBondCondition(11, 7, maxHO, minHOC, minNHO)
#antiBond.negate()

#antiPhiBound9 = helixTerm.createPhiBoundCondition(11, -90, 90)
#antiPsiBound9 = helixTerm.createPsiBoundCondition(11, -30, 60)

#helixTerm.conditions.remove(antiPhiBound9)
#helixTerm.conditions.remove(antiPsiBound9)

#antiPhiPsiBound9 = NoneCombiner([antiPhiBound9, antiPsiBound9])

#helixTerm.addCondition(antiPhiPsiBound9)

measures = []
measures.extend(helixTerm.createPhiPsiMeasures([9, 10, 11]))
measures.append(PropertyMeasure(helixTerm.selectResidue(9), "resname", str))
measures.append(PropertyMeasure(helixTerm.selectResidue(10), "resname", str))
measures.append(PropertyMeasure(helixTerm.selectResidue(11), "resname", str))
pipes = [Pipe(helixTerm, measures, sys.stdout)]
run = Run(pipes, path)
run.run()
