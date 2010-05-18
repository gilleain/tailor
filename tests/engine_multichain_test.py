import sys
from Tailor.Condition import DistanceBoundCondition
from Tailor.Description import StructureDescription, ChainDescription, ResidueDescription, constructSelection
from Tailor.Measure import DistanceMeasure, AngleMeasure
from Tailor.Run import Pipe, Run

# read in the structure
filepath = sys.argv[1]

# create a multichain description
proteinChain = ChainDescription({"chainType" : "Protein", "chainID" : ""})
proteinChain.createResidues(4)

waterResidue = ResidueDescription({"position" : 1 })
waterResidue.addAtom("O")
waterChain = ChainDescription({"chainType" : "Water"})
waterChain.addResidue(waterResidue)

description = StructureDescription({}, name="classAStructure")
description.addChain(proteinChain)
description.addChain(waterChain)

# create some simple measures and conditions
o1 = constructSelection(chainType="Protein", residuePosition=1, atomName="O")
o3 = constructSelection(chainType="Protein", residuePosition=3, atomName="O")
ow = constructSelection(chainType="Water",   residuePosition=1, atomName="O")
phi1, psi1 = proteinChain.createPhiPsiMeasure(2)
phi2, psi2 = proteinChain.createPhiPsiMeasure(3)

description.addCondition(DistanceBoundCondition(o1, ow, 1.8, 1.8))
description.addCondition(DistanceBoundCondition(o3, ow, 1.8, 1.8))

distanceMeasure1 = DistanceMeasure(o1, ow)
distanceMeasure2 = DistanceMeasure(o3, ow)
angleMeasure = AngleMeasure(o1, ow, o3)
measures = [phi1, psi1, phi2, psi2, distanceMeasure1, distanceMeasure2, angleMeasure]

# match and measure
pipe = Pipe(description, measures)
run = Run([pipe], filepath)
run.run()
