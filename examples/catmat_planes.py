"""
Re-read the results of the search for catmats and measure the angle
between the O-X-O plane normal and the O-O-O plane normal.
""" 

import math, sys
from Tailor.Engine import TmpDistanceMeasure, constructSelection, lookup
from Tailor.Geometry import Vector
from Tailor.ResultParser import ExampleDescription, generateExamples

def planePlaneNormalAngle(l, o1, o2, o3):
    v1 = l - o1
    v2 = o2 - l
    n1 = (v1.cross(v2)).normalize()
    oo1 = o3 - o1
    oo2 = o2 - o3
    n2 = (oo1.cross(oo2)).normalize()
    return "%0.2f" % math.degrees(n1.angle(n2))

class OXOMeasure(object):

    def __init__(self, ligand, oxy1, oxy2, oxy3):
        self.ligand = ligand
        self.oxy1 = oxy1
        self.oxy2 = oxy2
        self.oxy3 = oxy3

    def measure(self, structure):
        try:
            l = lookup(self.ligand, structure).center
            o1 = lookup(self.oxy1, structure).center
            o2 = lookup(self.oxy2, structure).center
            o3 = lookup(self.oxy3, structure).center
            return planePlaneNormalAngle(l, o1, o2, o3)
        except AttributeError, ae:
            print(str(ae)    )

filename = sys.argv[1]
pdbdir = sys.argv[2]

def parse(line):
    bits = line.split("\t")
    pdbid, chain = bits[0].split(".")
    start, stop = bits[1].split("-")
    ligands = eval(bits[-1])
    for lig in ligands:
        name = lig[0:3]
        if name == "HOH":
            ligand_num = lig[3:]
            break
    return ExampleDescription(pdbid, chain, start, stop, ligand_num)

hoh = constructSelection(chainType="Water", residuePosition=1, atomName="O")
o1 = constructSelection(chainType="Protein", residuePosition=1, atomName="O")
o2 = constructSelection(chainType="Protein", residuePosition=2, atomName="O")
o3 = constructSelection(chainType="Protein", residuePosition=3, atomName="O")
measure = OXOMeasure(hoh, o1, o2, o3)

for e in generateExamples(filename, pdbdir, parse):
    try:
        # XXX hack!
        print("%s\t%s\t%s" % (e, e[0][0].residueRange, measure.measure(e)))
    except Exception, ex:
        sys.stderr.write("%s in %s\n" % (str(ex), e))

