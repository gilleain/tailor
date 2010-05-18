#! /usr/bin/python
import sys

from Tailor.Description import predefined_descriptions
from Tailor.Measure import AngleMeasure, DistanceMeasure
from Tailor.Run import Run, Pipe

try:
    directory_or_file_path = sys.argv[1]

    catmat3Description = predefined_descriptions["RCatmat3"]

    firstO  = catmat3Description.selectResidue(1).selectAtom("O")
    secondO  = catmat3Description.selectResidue(2).selectAtom("O")
    thirdO   = catmat3Description.selectResidue(3).selectAtom("O")

    oooAngleMeasure   = AngleMeasure(firstO, secondO, thirdO)
    ooDistanceMeasure = DistanceMeasure(firstO, thirdO)
    measures = [oooAngleMeasure, ooDistanceMeasure]

    run = Run([Pipe(catmat3Description, measures, sys.stdout)], directory_or_file_path, [])
    run.run()

# do not let the script continue if there is a problem
except IOError, ioe:
    sys.stderr.write(str(ioe))
    sys.exit(0)
