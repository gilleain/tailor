import sys

from Tailor.Condition import HBondCondition
from Tailor.Description import ChainDescription, ResidueDescription
from Tailor.Measure import PropertyMeasure
from Tailor.Run import Run, Pipe

# file or directory to search
path = sys.argv[1]

# hbond params
maxHO  =   2.5
minHOC =  90.0
minNHO = 120.0

hairpin_data =  [
                    ["c1_1r",  7, [(1,  7), ( 7, 1), (3, 5), (5, 3)], [], [4]],
                    ["c1_3r",  7, [(1,  7), ( 7, 1), (3, 5)],   [(5, 3)], [4]],
                    ["c1_5r",  7, [(1,  7), ( 7, 1)],   [(3, 5), (5, 3)], [4]],
                    ["c1_7r",  7, [(1,  7)],   [( 7, 1), (3, 5), (5, 3)], [4]],

                    ["c2_2r",  8, [(1,  8), ( 8, 1), (3, 6), (6, 3)], [], [4, 5]],
                    ["c2_4r",  8, [(1,  8), ( 8, 1), (3, 6)],   [(6, 3)], [4, 5]],
                    ["c2_6r",  8, [(1,  8), ( 8, 1)],   [(3, 6), (6, 3)], [4, 5]],
                    ["c2_8r",  8, [(1,  8)],   [( 8, 1), (3, 6), (6, 3)], [4, 5]],

                    ["c3_3r",  9, [(1,  9), ( 9, 1), (3, 7), (7, 3)], [], [4, 5, 6]],
                    ["c3_5r",  9, [(1,  9), ( 9, 1), (3, 7)],   [(7, 3)], [4, 5, 6]],
                    ["c3_7r",  9, [(1,  9), ( 9, 1)],   [(3, 7), (7, 3)], [4, 5, 6]],
                    ["c3_9r",  9, [(1,  9)],   [( 9, 1), (3, 7), (7, 3)], [4, 5, 6]],

                    ["c4_4r", 10, [(1, 10), (10, 1), (3, 8), (8, 3)], [], [4, 5, 6, 7]],
                    ["c4_6r", 10, [(1, 10), (10, 1), (3, 8)],   [(8, 3)], [4, 5, 6, 7]],
                    ["c4_8r", 10, [(1, 10), (10, 1)],   [(3, 8), (8, 3)], [4, 5, 6, 7]],
                    ["c4_10r", 10, [(1, 10)],  [(10, 1), (3, 8), (8, 3)], [4, 5, 6, 7]],
                ]

pipes = []
for name, resnum, hBondNumberList, antiHBondNumberList, phipsiNums in hairpin_data:

    # description
    hairpin = ChainDescription({"chainID" : name})
    hairpin.createResidues(resnum)
    hairpin.createBackboneHBondConditions(hBondNumberList, maxHO, minHOC, minNHO)

    # antibonds
    for i, j in antiHBondNumberList:
        bond = hairpin.createBackboneHBondCondition(i, j, maxHO, minHOC, minNHO)
        bond.negate()

    # measures
    measures = []

    measures.extend(hairpin.createPhiPsiMeasures(phipsiNums))

    # either this line or the other 3 would do...
    #measures.extend(hairpin.createBackboneHBondMeasures(hBondNumberList))

    # ...but this latter is arguably more efficient, since it means less calculations
    for i, j in hBondNumberList:
        measures.append(PropertyMeasure(hairpin, "NH(%i)->CO(%i):H_A" % (i, j), float))
        measures.append(PropertyMeasure(hairpin, "NH(%i)->CO(%i):DHA" % (i, j), float))
        measures.append(PropertyMeasure(hairpin, "NH(%i)->CO(%i):HAA" % (i, j), float))

    fileOut = open("%s.txt" % name, 'w')
    pipe = Pipe(hairpin, measures, fileOut)
    pipes.append(pipe)

# run
run = Run(pipes, path)
run.run()
