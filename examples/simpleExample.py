# a copy of SimpleExample.java, for comparative testing
import sys

from Tailor.Description import ChainDescription, ResidueDescription, AtomDescription
from Tailor.Condition import PropertyCondition
from Tailor.Measure import DistanceMeasure
from Tailor.Run import Run


motif = ChainDescription({"chainID" : "valmot"})
residueDescription = ResidueDescription({"position" : 1, "resname" : "VAL"})
nAtom = AtomDescription({"name" : "N"})
oAtom = AtomDescription({"name" : "O"})

residueDescription.addSubDescription(nAtom)
residueDescription.addSubDescription(oAtom)
motif.addSubDescription(residueDescription)

atom1 = motif.selectResidue(1).selectAtom("N")
atom2 = motif.selectResidue(1).selectAtom("O")

Run(motif, sys.argv[1], [], [DistanceMeasure(atom1, atom2)]).run()
