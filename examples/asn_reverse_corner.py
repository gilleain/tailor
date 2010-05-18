import sys
from Tailor.Condition import DistanceBoundCondition
from Tailor.Description import StructureDescription, ChainDescription, ResidueDescription, constructSelection
from Tailor.Measure import DistanceMeasure, AngleMeasure
from Tailor.Run import Pipe, Run

# read in the structure
filepath = sys.argv[1]

# create a multichain description
proteinChain = ChainDescription({"chainType" : "Protein", "chainID" : ""})
proteinChain.createResidues(3)

waterResidue = ResidueDescription({"position" : 1 })
waterResidue.addAtom("O")
waterChain = ChainDescription({"chainType" : "Water"})
waterChain.addResidue(waterResidue)

description = StructureDescription({}, name="niche")
description.addChain(proteinChain)
description.addChain(waterChain)

# create some simple measures and conditions
c1 = constructSelection(chainType="Protein", residuePosition=1, atomName="C")
n3 = constructSelection(chainType="Protein", residuePosition=3, atomName="N")
ow = constructSelection(chainType="Water",   residuePosition=1, atomName="O")
phi, psi = proteinChain.createPhiPsiMeasure(2)

description.addCondition(DistanceBoundCondition(c1, ow, 1.5, 1.5))
description.addCondition(DistanceBoundCondition(n3, ow, 1.5, 1.5))

distanceMeasure1 = DistanceMeasure(c1, ow)
distanceMeasure2 = DistanceMeasure(n3, ow)
angleMeasure = AngleMeasure(c1, ow, n3)
measures = [phi, psi, distanceMeasure1, distanceMeasure2, angleMeasure]

# match and measure
pipe = Pipe(description, measures)
run = Run([pipe], filepath)
run.run()
