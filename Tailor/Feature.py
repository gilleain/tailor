from copy import copy, deepcopy
from Geometry import Vector
#from Description import ChainDescription, ResidueDescription, AtomDescription
from Exception import DescriptionException

def chunk(l, i):
    if i > len(l):
        return
    else:
        k = 0
        for j in range(i, len(l)):
            yield l[k:j]
            k += 1

class Feature(object):

    def __init__(self):
        self.subFeatures = []

    def add(self, feature):
        self.subFeatures.append(feature)

    def listFeaturesThatMatch(self, description):
        return iter(self.searchForFeaturesThatMatch(description))

    def searchForFeaturesThatMatch(self, description):
        matchingFeatures = []
        subFeatures = self.getSubFeaturesBelowLevel(description.levelCode)
        descriptionLength = len(description)

        featureType = descriptionToFeatureMap[type(description)]

        for subFeatureList in chunk(subFeatures, descriptionLength):
            # note that this is not a normal method call as such - featureType is a class
            feature = featureType(description.getName())
            for i, subFeature in enumerate(subFeatureList):
                subFeatureCopy = copy(subFeature)
                subFeatureCopy.position = i + 1
                feature.add(subFeatureCopy)

            if description.describes(feature):
                matchingFeatures.append(feature)

        return matchingFeatures

    def getSubFeaturesBelowLevel(self, levelCode):
        if self.levelCode == levelCode or self.levelCode == "A":
            return self.subFeatures
        else:
            for subFeature in self.subFeatures:
                return subFeature.getSubFeaturesBelowLevel(levelCode)

    def findFeature(self, description):
        if self.levelCode == description.levelCode:
            if description.describes(self):
                copySelf = copy(self)
                copySelf.subFeatures = []
                for subDescription in description:
                    matchingSubFeature = self.searchSubFeatures(subDescription)
                    if matchingSubFeature is not None:
                        copySelf.add(matchingSubFeature)
                    else:
                        raise DescriptionException, "no match for description %s" % subDescription
                """
                copySelf.subFeatures = [copy(sf) for sf in self]
                """

                return copySelf
        else:
            return self.searchSubFeatures(description)

    def searchSubFeatures(self, description):
        for subFeature in self:
            result = subFeature.findFeature(description)
            if result is not None:
                return result
        return None

    def __len__(self):
        return len(self.subFeatures)

    def __iter__(self):
        return iter(self.subFeatures)

    def __getitem__(self, index):
        return self.subFeatures[index]

    def getCenter(self):
        if len(self) > 0:
            total = Vector(0,0,0)
            for subFeature in self.subFeatures:
                total += subFeature.center
            return total / len(self)
        else:
            if self.levelCode == "A":
                return self.getAtomCenter()
            else:
                # this shouldn't really happen!
                return Vector(0,0,0)

    center = property(getCenter)

class Structure(Feature):

    def __init__(self, id):
        super(type(self), self).__init__()
        self.id = id
        self.levelCode = "S"

    def chainsWithID(self, chainID):
        for model in self:
            for chain in model:
                if chainID == "" or chain.chainID == chainID:
                    yield chain

    def chainsOfType(self, chainType):
        chainsToReturn = []
        for model in self:
            for chain in model:
                #print "comparing", chain.chainType, "with", chainType, "in", self.id
                if chainType == "" or chain.chainType == chainType:
                    chainsToReturn.append(chain)
                else:
                    continue
        return chainsToReturn

    def __repr__(self):
        return "%s" % self.id

class Model(Feature):

    def __init__(self, number):
        super(type(self), self).__init__()
        self.number = number
        self.levelCode = "M"

    def __repr__(self):
        return "%s" % self.number 

class Chain(Feature):

    def __init__(self, chainID, chainType=None):
        super(type(self), self).__init__()
        self.chainID = chainID
        self.chainType = chainType
        self.levelCode = "C"

    def getFirst(self):
        return self.subFeatures[0]
    first = property(getFirst)

    def getLast(self):
        return self.subFeatures[-1]
    last = property(getLast)

    def getResidueRange(self):
        if len(self) > 1:
            return "%s-%s" % (self.first.number, self.last.number)
        else:
            return "%s" % self.first.number
    residueRange = property(getResidueRange)

    def getResidueNumber(self, residueNumber):
        for r in self:
            if r.number == residueNumber:
                return r
        return None

    def __repr__(self):
        return "%s %s %s" % (self.chainType, self.chainID, self.residueRange)

class Residue(Feature):

    def __init__(self, residueID, resname, segID=None):
        super(type(self), self).__init__()
        if isinstance(residueID, tuple):
            self.residueID = residueID
        else:
            self.residueID = (residueID, '')
        self.resname = resname
        self.segID = segID
        self.levelCode = "R"

    def getNumber(self):
        return self.residueID[0]
    number = property(getNumber)

    def getAtom(self, atomName):
        for atom in self:
            if atom.name == atomName:
                return atom
        raise KeyError, "No atom with name %s" % atomName

    def getAtomPosition(self, atomName):
        return self.getAtom(atomName).getAtomCenter()

    def toFullString(self):
        return "%s%s" % (str(self), str(self.subFeatures))

    def __repr__(self):
        return "%s%s%s" % (self.resname, self.residueID[0], self.residueID[1])

class Atom(Feature):

    def __init__(self, name, coord, b_factor=None, occupancy=1.0, altloc=None):
        super(type(self), self).__init__()
        self.name = name.strip()
        self.coord = coord
        self.b_factor = b_factor
        self.occupancy = occupancy
        self.altloc = altloc
        self.levelCode = "A"

    def getAtomCenter(self):
        if isinstance(self.coord, Vector):
            return self.coord
        else:
            return Vector(self.coord[0], self.coord[1], self.coord[2])

    def __repr__(self):
        return "%s" % self.name

descriptionToFeatureMap = {}
"""
    ChainDescription : Chain,
    ResidueDescription : Residue,
    AtomDescription : Atom
}
"""

featureToSubFeatureMap = {
    Structure : Model,
    Model : Chain,
    Chain : Residue,
    Residue : Atom,
    Atom : None
}


if __name__ == "__main__":
    import sys
    from Parse import PDBFileList
    from Feature import *

    path = sys.argv[1]
    structure = PDBFileList(path)[0]
    nestFeature = predefined_features["RLnest"]
    extraResidue = ResidueDescription({"position" : 4})
    extraResidue.addMatchCondition("resname", "GLY")
    #nestFeature.addResidue(extraResidue)
    print nestFeature
    for i, nest in enumerate(structure.list_features(predefined_descriptions["RLnest"])):
        print "nest", i, nest
