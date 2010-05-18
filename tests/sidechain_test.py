import sys
from Tailor.Condition import DistanceBoundCondition
from Tailor.Description import StructureDescription, ChainDescription, ResidueDescription, constructSelection
from Tailor.Measure import DistanceMeasure, AngleMeasure
from Tailor.Run import Pipe, Run

# read in the structure
filepath = sys.argv[1]

# create a multichain description
backboneChain = ChainDescription({"chainType" : "Protein", "chainID" : ""})
backboneChain.createResidues(4)

sidechainResidue = ResidueDescription({"position" : 1, "resname" : "ASN" })
sidechainResidue.addAtom("ND2")
sidechainChain = ChainDescription({"chainType" : "Protein"})
sidechainChain.addResidue(sidechainResidue)

description = StructureDescription({}, name="classAStructure")
description.addChain(backboneChain)
description.addChain(sidechainChain)

# create some simple measures and conditions
o1 = constructSelection(chainType="Protein", residuePosition=1, atomName="O")
o3 = constructSelection(chainType="Protein", residuePosition=3, atomName="O")
n  = constructSelection(chainType="Protein", residuePosition=1, atomName="ND2")
phi1, psi1 = backboneChain.createPhiPsiMeasure(2)
phi2, psi2 = backboneChain.createPhiPsiMeasure(3)

description.addCondition(DistanceBoundCondition(o1, n, 1.8, 1.8))
description.addCondition(DistanceBoundCondition(o3, n, 1.8, 1.8))

distanceMeasure1 = DistanceMeasure(o1, n)
distanceMeasure2 = DistanceMeasure(o3, n)
angleMeasure = AngleMeasure(o1, n, o3)
measures = [phi1, psi1, phi2, psi2, distanceMeasure1, distanceMeasure2, angleMeasure]

# match and measure
pipe = Pipe(description, measures)
run = Run([pipe], filepath)
run.run()
