import sys

from Tailor.Description import ChainDescription 
from Tailor.Measure import DistanceMeasure
from Tailor.Run import Run, Pipe

path = sys.argv[1]

catmat3 = ChainDescription({"chainID" : "c3"})
catmat3.createResidues(4)

for i in range(1, 5):
    print catmat3.getResidue(i).children.pop()

catmat3.createPhiBoundCondition(2,  -85, 45)
catmat3.createPsiBoundCondition(2,   -5, 45)
catmat3.createPhiBoundCondition(3,  -70, 40)
catmat3.createPsiBoundCondition(3,  135, 45)

measures3 = []
measures3.extend(catmat3.createPhiPsiMeasures([2, 3]))

catmat4 = ChainDescription({"chainID" : "c4"})
catmat4.createResidues(5)

for i in range(1, 6):
    print catmat4.getResidue(i).children.pop()

catmat4.createPhiBoundCondition(2,  -75, 45)
catmat4.createPsiBoundCondition(2,  -15, 45)
catmat4.createPhiBoundCondition(3,  -95, 45)
catmat4.createPsiBoundCondition(3,   -5, 45)
catmat4.createPhiBoundCondition(4, -100, 60)
catmat4.createPsiBoundCondition(4,  135, 45)

measures4 = []
measures4.extend(catmat4.createPhiPsiMeasures([2, 3, 4]))

pipes = [Pipe(catmat3, measures3, open("c3.txt", 'w')), Pipe(catmat4, measures4, open("c4.txt", 'w'))]
run = Run(pipes, path)
run.run()
