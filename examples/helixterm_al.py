import sys

from Tailor.Condition import HBondCondition, NoneCombiner
from Tailor.Description import ChainDescription
from Tailor.Measure import PropertyMeasure
from Tailor.Run import Run

path = sys.argv[1]

maxHO = 3.5
minHOC = 90.0
minNHO = 90.0

helixTerm = ChainDescription({"chainID" : "helix_terminus"})
helixTerm.createResidues(9)
helixTerm.createBackboneHBondCondition(6, 2, maxHO, minHOC, minNHO)
helixTerm.createBackboneHBondCondition(7, 3, maxHO, minHOC, minNHO)

antiBond = helixTerm.createBackboneHBondCondition(8, 4, maxHO, minHOC, minNHO)
antiBond.negate()

helixTerm.createPhiBoundCondition(7, 75, 50)
helixTerm.createPsiBoundCondition(7, 30, 50)

#antiPhiBound8 = helixTerm.createPhiBoundCondition(8, -50, 50)
#antiPhiBound8.negate()

#antiPsiBound8 = helixTerm.createPsiBoundCondition(8, -50, 50)
#antiPsiBound8.negate()

#helixTerm.conditions.remove(antiPhiBound8)
#helixTerm.conditions.remove(antiPsiBound8)
#antiPhiPsiBound8 = NoneCombiner([antiPhiBound8, antiPsiBound8])

#helixTerm.addCondition(antiPhiPsiBound8)

measures = []
measures.extend(helixTerm.createPhiPsiMeasures([6, 7, 8]))
#measures.append(helixTerm.createBackboneHBondMeasure(8, 4))
run = Run(helixTerm, path, [], measures)
run.run()
