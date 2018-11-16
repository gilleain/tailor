"""
Re-read the results of the search for metal ions and measure the point-
plane distance of the metal from the carbonyl oxygen ion plane.
""" 

import sys
from Tailor.Engine import constructSelection, lookup
from Tailor.Geometry import Vector
from Tailor.ResultParser import ExampleDescription, generateExamples

def signedPointPlaneDistance(point, p1, p2, p3):
    v1 = p2 - p1
    v2 = p3 - p2
    n = (v1.cross(v2)).normalize()
    distanceFromZero = n * p2
    return (n * point) - distanceFromZero

class PointPlaneDistanceMeasure(object):

    def __init__(self, pointD, planeD1, planeD2, planeD3):
        self.pointD = pointD
        self.planeD1 = planeD1
        self.planeD2 = planeD2
        self.planeD3 = planeD3

    def measure(self, structure):
        try:
            point = lookup(self.pointD, structure).center
            plane1 = lookup(self.planeD1, structure).center
            plane2 = lookup(self.planeD2, structure).center
            plane3 = lookup(self.planeD3, structure).center
            return signedPointPlaneDistance(point, plane1, plane2, plane3)
        except AttributeError, ae:
            print(str(ae)    )

filename = sys.argv[1]
pdbdir = sys.argv[2]

def parse(line):
    pdbid, chain, start, stop = line.split(".")
    return ExampleDescription(pdbid, chain, start, stop)

metal = constructSelection(chainType="Ligand", residuePosition=1, atomName="CA")
o1 = constructSelection(chainType="Protein", residuePosition=1, atomName="O")
o2 = constructSelection(chainType="Protein", residuePosition=3, atomName="O")
o3 = constructSelection(chainType="Protein", residuePosition=6, atomName="O")
measure = PointPlaneDistanceMeasure(metal, o1, o2, o3)

for e in generateExamples(filename, pdbdir, parse):
    # XXX hack!
    print(measure.measure(e[0][0]))

