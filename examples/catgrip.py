import sys

from Tailor.Description import ChainDescription 
from Tailor.Measure import DistanceMeasure
from Tailor.Run import Run, Pipe

path = sys.argv[1]

rlCatgrip = ChainDescription({"chainID" : "RL"})
rlCatgrip.createResidues(4)

for i in range(1, 5):
    print rlCatgrip.getResidue(i).children.pop()

rlCatgrip.createPhiBoundCondition(2,  -90, 45)
rlCatgrip.createPsiBoundCondition(2,  135, 45)
rlCatgrip.createPhiBoundCondition(3,   90, 45)
rlCatgrip.createPsiBoundCondition(3, -135, 45)

measuresRL = []
measuresRL.extend(rlCatgrip.createPhiPsiMeasures([2, 3]))

lrCatgrip = ChainDescription({"chainID" : "LR"})
lrCatgrip.createResidues(4)

for i in range(1, 5):
    print lrCatgrip.getResidue(i).children.pop()

lrCatgrip.createPhiBoundCondition(2,   90, 45)
lrCatgrip.createPsiBoundCondition(2, -135, 45)
lrCatgrip.createPhiBoundCondition(3,  -90, 45)
lrCatgrip.createPsiBoundCondition(3,  135, 45)

measuresLR = []
measuresLR.extend(lrCatgrip.createPhiPsiMeasures([2, 3]))

pipes = [Pipe(rlCatgrip, measuresRL, open("rl.txt", 'w')), Pipe(lrCatgrip, measuresLR, open("lr.txt", 'w'))]
run = Run(pipes, path)
run.run()
