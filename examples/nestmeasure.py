#! /usr/bin/python
import sys

from Tailor.Description import predefined_descriptions, ResidueDescription
from Tailor.Measure import AngleMeasure, DistanceMeasure
from Tailor.Parse import PDBFileList
from Tailor.Condition import HBondCondition

"""
 the only argument to the script is a path 
 either like './structure_dir', 'C:\structure_dir' (a directory of structures)
 or     like './structure_dir/1a2p.pdb', C:\structure_dir\1a2p.pdb' (a single file)

 e.g. python nestmeasure.py 1a2pA.pdb
"""
try:
    # make an object that manages the files
    directory_or_file_path = sys.argv[1]
    structures = PDBFileList(directory_or_file_path)

    # there may be better ways to do this, but for now, this is simplest
    rlNestDescription = predefined_descriptions["RLnest"]

    # add a condition that the second residue must be ASP
    secondResidue = rlNestDescription.getResidue(2)
    secondResidue.addPropertyCondition("resname", "ASP")

    # require an hbond between the OD1 atom of residue 2 and the N of residue 4
    secondOD1 = rlNestDescription.selectResidue(2).selectAtom("OD1")
    secondCG  = rlNestDescription.selectResidue(2).selectAtom("CG")
    fourthN   = rlNestDescription.selectResidue(4).selectAtom("N")
    fourthH   = rlNestDescription.selectResidue(4).selectAtom("H")

    rlNestDescription.addCondition(HBondCondition(fourthN, fourthH, secondOD1, secondCG, 2.5, 120, 90))

    # these are not 'real' atoms, but descriptions of those atoms we want in the nest
    secondN  = rlNestDescription.selectResidue(2).selectAtom("N")
    thirdN   = rlNestDescription.selectResidue(3).selectAtom("N")
    fourthN  = rlNestDescription.selectResidue(4).selectAtom("N")

    # a 'Measure' is like a ruler or a compass - an instrument for making measurements
    nnnAngleMeasure = AngleMeasure(secondN, thirdN, fourthN)
    nnDistanceMeasure = DistanceMeasure(secondN, fourthN)

    # provide a 'header' for the columns
    print("pdbid\tmotif\t%s\t%s" % (nnnAngleMeasure, nnDistanceMeasure))

    # this works for 1 or many structures
    for structure in structures:

        # let the user know what file is being processed now
        sys.stderr.write("Processing : %s\n" % structure)

        # this structure may have a number of nests, check them all
        for nest in structure.listFeaturesThatMatch(rlNestDescription):

            # use the measure on this object...
            nnnAngle = nnnAngleMeasure.measure(nest)
            nnDistance = nnDistanceMeasure.measure(nest)

            # ...and print(out the result)
            print("%s\t%s\t%0.2f\t%0.2f" % (structure, nest.subFeatures, nnnAngle, nnDistance))

# do not let the script continue if there is a problem
except IOError, ioe:
    sys.stderr.write(str(ioe))
    sys.exit(0)
