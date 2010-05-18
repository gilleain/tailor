import sys

from Tailor.Condition import HBondCondition, NoneCombiner
from Tailor.Description import ChainDescription, AtomDescription
from Tailor.Measure import PropertyMeasure, DistanceMeasure
from Tailor.Run import Run, Pipe

path = sys.argv[1]

tripep = ChainDescription({"chainID" : "h"})
tripep.createResidues(3)

# for proline searches
#tripep.getResidue(2).children.pop()

measures = []
measures.extend(tripep.createPhiPsiMeasure(2))
measures.append(PropertyMeasure(tripep.selectResidue(1), "resname", str))
measures.append(PropertyMeasure(tripep.selectResidue(2), "resname", str))
measures.append(PropertyMeasure(tripep.selectResidue(3), "resname", str))
pipes = [Pipe(tripep, measures, sys.stdout)]
run = Run(pipes, path)
run.run()
