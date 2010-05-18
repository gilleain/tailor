from copy import copy
from Condition import PropertyCondition, TorsionBoundCondition, HBondCondition
from Measure import HBondMeasure, TorsionMeasure

def constructSelection(chainID=None, chainType=None, residuePosition=None, atomName=None):
    """ Make a selection-Description for use in Measures.
    """
    if chainID != None or chainType != None:
        #selection = ChainDescription({"chainID" : chainID, "chainType" : chainType})
        selection = ChainDescription({"chainType" : chainType})

        selection.addResidue(ResidueDescription({"position" : residuePosition}))
        selection.children[0].addAtom(atomName)
    else:
        selection = ResidueDescription({"position" : residuePosition})
        selection.addAtom(atomName)
    return selection

class Description(object):

    def __init__(self, propertyConditions):
        self.children = []
        self.conditions = []
        self.addPropertyConditions(propertyConditions)

    def addPropertyConditions(self, propertyConditions):
        for conditionKey, conditionValue in propertyConditions.iteritems():
            self.addPropertyCondition(conditionKey, conditionValue)

    def addPropertyCondition(self, propertyName, propertyValue):
        self.__dict__[propertyName] = propertyValue
        self.conditions.append(PropertyCondition(propertyName, propertyValue))
    
    def addCondition(self, condition):
        self.conditions.append(condition)

    def describes(self, feature):
        for condition in self.conditions:
            if condition.satisfiedBy(feature):
                continue
            else:
                return False

        return self.matchesSubFeatures(feature)

    def matchesSubFeatures(self, feature):
        for subDescription in self:
            matchingSubFeature = None
            for subFeature in feature:
                #print "trying", subDescription, "against", subFeature
                if subDescription.describes(subFeature):
                    matchingSubFeature = subFeature
                else:
                    continue
            if matchingSubFeature is None:
                #print subDescription, "had no match"
                return False
            else:
                #print subDescription, "matched", matchingSubFeature
                continue
        return True

    def addSubDescription(self, subDescription):
        self.children.append(subDescription)

    def hasSubDescriptions(self):
        return len(self) > 0

    def __getattr__(self, key):
        for condition in self.conditions:
            if type(condition) == PropertyCondition and condition.propertyKey == key:
                return condition.propertyValue
        raise AttributeError, "No attribute or PropertyCondition found with key %s" % key 

    def hasattr(self, key):
        for condition in self.conditions:
            if type(condition) == PropertyCondition and condition.propertyKey == key:
                return True
        return False

    def __len__(self):
        return len(self.children)

    def __iter__(self):
        return iter(self.children)

    def to_str(self):
        return "%s %s" % (self, self.children)

class StructureDescription(Description):

    def __init__(self, propertyConditions, name=None):
        super(type(self), self).__init__(propertyConditions)
        self.name = name
        self.levelCode = "S"

    def addChain(self, chainDescription):
        self.children.append(chainDescription)

    def selectChain(self, chainID):
        for chainDescription in self:
            if chainDescription.chainID == chainID:
                return copy(chainDescription)
        return None

    def selectChainType(self, chainType):
        for chainDescription in self:
            if chainDescription.chainType == chainType:
                return copy(chainDescription)
        return None

    def __repr__(self):
        try:
            return "%s" % self.name
        except AttributeError:
            return "?"

class ChainDescription(Description):

    def __init__(self, propertyConditions):
        super(type(self), self).__init__(propertyConditions)
        self.levelCode = "C"

    def createResidues(self, n):
        self.createResidueFromToInclusive(1, n)

    def createResidueFromToInclusive(self, i, j):
        for x in range(i - 1, j):
            self.createResidueWithBackbone(x)

    def createResidueWithBackbone(self, i):
        residue = ResidueDescription({"position" : i + 1})
        for atomName in ["N", "CA", "C", "O", "H"]:
        #for atomName in ["N", "CA", "C", "O"]:
            residue.addAtom(atomName)
        self.addResidue(residue) 

    def addResidue(self, residue):
        self.children.append(residue)

    def getResidue(self, i):
        return self.children[i - 1]
    
    def selectResidue(self, i):
        return copy(self.children[i - 1])

    def getName(self):
        return self.chainID

    def createBackboneHBondConditions(self, donorAcceptorResidueNumbers, maxHO, minHOC, minNHO):
        conditions = []
        for donorResidueNumber, acceptorResidueNumber in donorAcceptorResidueNumbers:
            conditions.append(self.createBackboneHBondCondition(donorResidueNumber, acceptorResidueNumber, maxHO, minHOC, minNHO))
        return conditions

    def createBackboneHBondCondition(self, donorResidueNumber, acceptorResidueNumber, maxHO, minHOC, minNHO):
        """
        N = self.selectResidue(donorResidueNumber).selectAtom("N")
        H = self.selectResidue(donorResidueNumber).selectAtom("H")
        C = self.selectResidue(acceptorResidueNumber).selectAtom("C")
        O = self.selectResidue(acceptorResidueNumber).selectAtom("O")
        """
        N = constructSelection(chainType="Protein", residuePosition=donorResidueNumber, atomName="N")
        H = constructSelection(chainType="Protein", residuePosition=donorResidueNumber, atomName="H")
        C = constructSelection(chainType="Protein", residuePosition=acceptorResidueNumber, atomName="C")
        O = constructSelection(chainType="Protein", residuePosition=acceptorResidueNumber, atomName="O")
        
        name = "NH(%i)->CO(%i)" % (donorResidueNumber, acceptorResidueNumber)

        # note the dha/haa swap - would be nice to fix this!
        bond = HBondCondition(N, H, O, C, maxHO, minNHO, minHOC, name=name)
        self.addCondition(bond) 
        return bond

    def createBackboneHBondMeasures(self, donorAcceptorResidueNumbers):
        measures = []
        for donorResidueNumber, acceptorResidueNumber in donorAcceptorResidueNumbers:
            measures.append(self.createBackboneHBondMeasure(donorResidueNumber, acceptorResidueNumber))
        return measures

    def createBackboneHBondMeasure(self, donorResidueNumber, acceptorResidueNumber):
        """
        N = self.selectResidue(donorResidueNumber).selectAtom("N")
        H = self.selectResidue(donorResidueNumber).selectAtom("H")
        C = self.selectResidue(acceptorResidueNumber).selectAtom("C")
        O = self.selectResidue(acceptorResidueNumber).selectAtom("O")
        """
        N = constructSelection(chainType="Protein", residuePosition=donorResidueNumber, atomName="N")
        H = constructSelection(chainType="Protein", residuePosition=donorResidueNumber, atomName="H")
        C = constructSelection(chainType="Protein", residuePosition=acceptorResidueNumber, atomName="C")
        O = constructSelection(chainType="Protein", residuePosition=acceptorResidueNumber, atomName="O")

        bond = HBondMeasure(N, H, O, C)
        bond.setName("NH(%i)->CO(%i)" % (donorResidueNumber, acceptorResidueNumber))
        return bond

    def createPhiPsiMeasures(self, residueNumbers):
        measures = []
        for residueNumber in residueNumbers:
            measures.extend(self.createPhiPsiMeasure(residueNumber))
        return measures

    def createPhiPsiMeasure(self, residueNumber):
        return self.createPhiMeasure(residueNumber), self.createPsiMeasure(residueNumber)

    def createPhiMeasure(self, residueNumber):
        i = residueNumber
        phi = self.createTorsionMeasure(i - 1, "C", i, "N", i, "CA", i, "C")
        phi.setName("phi%i" % residueNumber)
        return phi

    def createPsiMeasure(self, residueNumber):
        i = residueNumber
        psi = self.createTorsionMeasure(i, "N", i, "CA", i, "C", i + 1, "N")
        psi.setName("psi%i" % residueNumber)
        return psi

    def createPhiBoundCondition(self, residueNumber, center, range):
        i = residueNumber
        condition = self.createTorsionBoundCondition(i - 1, "C", i, "N", i, "CA", i, "C", center, range, "phi%s" % i)
        self.addCondition(condition)
        return condition
        
    def createPsiBoundCondition(self, residueNumber, center, range):
        i = residueNumber
        condition = self.createTorsionBoundCondition(i, "N", i, "CA", i, "C", i + 1, "N", center, range, "psi%s" % i)
        self.addCondition(condition)
        return condition

    def createTorsionBoundCondition(self, firstR, firstA, secondR, secondA, thirdR, thirdA, fourthR, fourthA, center, range, name=None):
        """
        a = self.selectResidue(firstR).selectAtom(firstA)
        b = self.selectResidue(secondR).selectAtom(secondA)
        c = self.selectResidue(thirdR).selectAtom(thirdA)
        d = self.selectResidue(fourthR).selectAtom(fourthA)
        """
        a = constructSelection(chainType="Protein", residuePosition=firstR, atomName=firstA)
        b = constructSelection(chainType="Protein", residuePosition=secondR, atomName=secondA)
        c = constructSelection(chainType="Protein", residuePosition=thirdR, atomName=thirdA)
        d = constructSelection(chainType="Protein", residuePosition=fourthR, atomName=fourthA)

        if name is None:
            return TorsionBoundCondition(a, b, c, d, center, range)
        else:
            return TorsionBoundCondition(a, b, c, d, center, range, name)

    def createTorsionMeasure(self, firstR, firstA, secondR, secondA, thirdR, thirdA, fourthR, fourthA):
        """
        a = self.selectResidue(firstR).selectAtom(firstA)
        b = self.selectResidue(secondR).selectAtom(secondA)
        c = self.selectResidue(thirdR).selectAtom(thirdA)
        d = self.selectResidue(fourthR).selectAtom(fourthA)
        """
        a = constructSelection(chainType="Protein", residuePosition=firstR, atomName=firstA)
        b = constructSelection(chainType="Protein", residuePosition=secondR, atomName=secondA)
        c = constructSelection(chainType="Protein", residuePosition=thirdR, atomName=thirdA)
        d = constructSelection(chainType="Protein", residuePosition=fourthR, atomName=fourthA)

        return TorsionMeasure(a, b, c, d)

    def __repr__(self):
        try:
            return "%s" % self.chainID
        except AttributeError:
            try :
                return "%s" % self.chainType
            except AttributeError:
                return "?"

class ResidueDescription(Description):

    def __init__(self, propertyConditions):
        super(type(self), self).__init__(propertyConditions)
        self.levelCode = "R"

    def addAtom(self, atomName):
        self.addSubDescription(AtomDescription({"name" : atomName}))

    def getAtom(self, atomName):
        for atomDescription in self.children:
            for condition in atomDescription.conditions:
                if condition.satisfiedBy(atomName):
                    return atomDescription
        return None

    def selectAtom(self, atomName):
        self.children = []
        self.addAtom(atomName)
        return self

    def getResname(self):
        try:
            return self.name
        except AttributeError:
            return "Any"

    def getPosition(self):
        p = self.position
        if p < 1:
            return "i - %i" % abs((p - 1))
        elif p == 1:
            return "i"
        elif p > 1:
            return "i + %i" % (p - 1)

    def __repr__(self):
        return "%s %s" % (self.getPosition(), self.children)
    
class AtomDescription(Description):

    def __init__(self, propertyConditions):
        super(type(self), self).__init__(propertyConditions)
        self.levelCode = "A"

    def __repr__(self):
        return "%s" % self.name

"""
    The following utility functions generate descriptions from dictionaries or lists of values.
"""

predefined_bounds = {
    "AR" : ((-90, 30), (  0, 30)),
    "AL" : (( 90, 30), (  0, 30)),
    "BR" : ((-90, 30), (150, 30)),
}

predefined_values = {
                        "RLnest"   : ("AR", "AL"),
                        "LRNest"   : ("AL", "AR"),
                        "RCatmat3" : ("AR", "BR"),
                        "RCatmat4" : ("AR", "AR", "BR")
                    }

def generateBackboneDescription(name, bounds):
    chain = ChainDescription({"chainID" : name})
    chain.createResidues(len(bounds) + 2)    

    for i in range(len(bounds)):
        r = i + 2
        phiBound, psiBound = bounds[i]
        phiCenter, phiRange = phiBound
        psiCenter, psiRange = psiBound
        chain.createPhiBoundCondition(r, phiCenter, phiRange)
        chain.createPsiBoundCondition(r, psiCenter, psiRange)

    return chain

def generatePredefined(name, boundNames):
    return (name, generateBackboneDescription(name, [predefined_bounds[b] for b in boundNames]))

def generatePredefinedFromDictionary(valueDict):
    return dict([generatePredefined(name, bounds) for name, bounds in valueDict.iteritems()])
        
predefined_descriptions = generatePredefinedFromDictionary(predefined_values)
