# turns defined by a CAlpha(i)<->CAlpha(i + 3) < 7.0 Ang
import sys

from Tailor.Condition import DistanceBoundCondition, AllCombiner, NoneCombiner
from Tailor.Description import ChainDescription, AtomDescription
from Tailor.Measure import PropertyMeasure, DistanceMeasure
from Tailor.Run import Run, Pipe

path = sys.argv[1]

tetrapep = ChainDescription({"chainID" : "t"})
tetrapep.createResidues(4)

# the < 7 distance constraint
ca1 = tetrapep.selectResidue(1).selectAtom("CA")
ca2 = tetrapep.selectResidue(4).selectAtom("CA")
tetrapep.addCondition(DistanceBoundCondition(ca1, ca2, 3.5, 3.5))

# no helical angles
#phi2 = tetrapep.createPhiBoundCondition(2, -60, 10)
#psi2 = tetrapep.createPsiBoundCondition(2, -60, 10)
#phi3 = tetrapep.createPhiBoundCondition(3, -60, 10)
#psi3 = tetrapep.createPsiBoundCondition(3, -60, 10)
#for torsion in [phi2, psi2, phi3, psi3]: tetrapep.conditions.remove(torsion)
#tetrapep.addCondition(NoneCombiner([phi2, psi2]))
#tetrapep.addCondition(NoneCombiner([phi3, psi3]))

# for proline searches
#for i in range(1, 5): tetrapep.getResidue(i).children.pop()

measures = []
measures.extend(tetrapep.createPhiPsiMeasure(2))
measures.extend(tetrapep.createPhiPsiMeasure(3))
measures.append(PropertyMeasure(tetrapep.selectResidue(1), "resname", str))
measures.append(PropertyMeasure(tetrapep.selectResidue(2), "resname", str))
measures.append(PropertyMeasure(tetrapep.selectResidue(3), "resname", str))
measures.append(PropertyMeasure(tetrapep.selectResidue(4), "resname", str))
pipes = [Pipe(tetrapep, measures, sys.stdout)]
run = Run(pipes, path)
run.run()
