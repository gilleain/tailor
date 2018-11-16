import sys

from Tailor.Description import ChainDescription 
from Tailor.Measure import DistanceMeasure
from Tailor.Run import Run, Pipe

path = sys.argv[1]

rlNest = ChainDescription({"chainID" : "RL"})
rlNest.createResidues(4)

for i in range(1, 5):
    print(rlNest.getResidue(i).children.pop())

rlNest.createPhiBoundCondition(2, -90, 45)
rlNest.createPsiBoundCondition(2,   0, 45)
rlNest.createPhiBoundCondition(3,  80, 45)
rlNest.createPsiBoundCondition(3,  10, 45)

measuresRL = []
measuresRL.extend(rlNest.createPhiPsiMeasures([2, 3]))

lrNest = ChainDescription({"chainID" : "LR"})
lrNest.createResidues(4)

for i in range(1, 5):
    print(lrNest.getResidue(i).children.pop())

lrNest.createPhiBoundCondition(2,  80, 45)
lrNest.createPsiBoundCondition(2,  10, 45)
lrNest.createPhiBoundCondition(3, -90, 45)
lrNest.createPsiBoundCondition(3,   0, 45)

measuresLR = []
measuresLR.extend(lrNest.createPhiPsiMeasures([2, 3]))

pipes = [Pipe(rlNest, measuresRL, open("rl_nest.txt", 'w')), Pipe(lrNest, measuresLR, open("lr_nest.txt", 'w'))]
run = Run(pipes, path)
run.run()
