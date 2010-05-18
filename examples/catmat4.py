#! /usr/bin/python
import sys

from Tailor.Condition import HBondCondition
from Tailor.Description import predefined_descriptions
from Tailor.Measure import AngleMeasure, DistanceMeasure, HBondMeasure
from Tailor.Run import Run

try:
    directory_or_file_path = sys.argv[1]

    catmat4Description = predefined_descriptions["RCatmat4"]

    firstO    = catmat4Description.selectResidue(1).selectAtom("O")
    secondO   = catmat4Description.selectResidue(2).selectAtom("O")
    fourthO   = catmat4Description.selectResidue(4).selectAtom("O")

    firstC    = catmat4Description.selectResidue(1).selectAtom("C")
    fourthH    = catmat4Description.selectResidue(4).selectAtom("H")
    fourthN    = catmat4Description.selectResidue(4).selectAtom("N")

    firstCOToFourthNHBondCondition = HBondCondition(fourthN, fourthH, firstO, firstC, 3.0, 120.0, 90.0)
    catmat4Description.addCondition(firstCOToFourthNHBondCondition) 

    oooAngleMeasure              = AngleMeasure(firstO, secondO, fourthO)
    ooDistanceMeasure            = DistanceMeasure(firstO, fourthO)
    firstCOToFourthNHBondMeasure = HBondMeasure(fourthN, fourthH, firstO, firstC)

    measures = [oooAngleMeasure, ooDistanceMeasure, firstCOToFourthNHBondMeasure]

    run = Run(catmat4Description, directory_or_file_path, [], measures)
    run.run()

# do not let the script continue if there is a problem
except IOError, ioe:
    sys.stderr.write(str(ioe))
    sys.exit(0)
