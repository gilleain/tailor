import sys
from Geometry import distance, angle, torsion
from Engine import lookup
from Exception import DescriptionException

class Condition(object):
    negated = False

    def negate(self):
        self.negated = True
        self.name = "!" + self.name

class AllCombiner(Condition):

    def __init__(self, conditions=[]):
        self.conditions = conditions

    def satisfiedBy(self, feature):
        for condition in self.conditions:
            if condition.satisfiedBy(feature):
                continue
            else:
                return False
        return True

    def __repr__(self):
        return "All of [" + ", ".join([str(c) for c in self.conditions]) + "]"

class AnyCombiner(Condition):

    def __init__(self, conditions=[]):
        self.conditions = conditions

    def satisfiedBy(self, feature):
        for condition in self.conditions:
            if condition.satisfiedBy(feature):
                return True
            else:
                continue
        return False

    def __repr__(self):
        return "Any of [" + ", ".join([str(c) for c in self.conditions]) + "]"

class OnlyOneCombiner(Condition):

    def __init__(self, conditions=[]):
        self.conditions = conditions

    def satisfiedBy(self, feature):
        satisfied = False
        for condition in self.conditions:
            if condition.satisfiedBy(feature):
                if not satisfied:
                    satisfied = True
                else:
                    return False
            else:
                continue
        return satisfied

    def __repr__(self):
        return "One of [" + ", ".join([str(c) for c in self.conditions]) + "]"

class NoneCombiner(Condition):

    def __init__(self, conditions=[]):
        self.conditions = conditions

    def satisfiedBy(self, feature):
        numberSatisfied = 0
        for condition in self.conditions:
            if condition.satisfiedBy(feature):
                numberSatisfied += 1
            else:
                continue
        return numberSatisfied == 0

    def __repr__(self):
        return "None of [" + ", ".join([str(c) for c in self.conditions]) + "]"

class PropertyCondition(Condition):

    def __init__(self, propertyKey, propertyValue):
        self.propertyKey = propertyKey
        self.propertyValue = propertyValue

    def satisfiedBy(self, feature):
        if feature.__dict__.has_key(self.propertyKey) and feature.__dict__[self.propertyKey] == self.propertyValue:
            return True
        else:
            return False

    def __repr__(self):
        return "%s : %s" % (self.propertyKey, self.propertyValue)

class DistanceBoundCondition(Condition):

    def __init__(self, descriptionA, descriptionB, center, range, name="Distance"):
        self.descriptionA = descriptionA
        self.descriptionB = descriptionB

        self.center = float(center)
        self.range  = float(range)

        self.name = name

    def satisfiedBy(self, example):
        try:
            a = lookup(self.descriptionA, example).center
            b = lookup(self.descriptionB, example).center

        except DescriptionException:
            sys.stderr.write("description exception")
            return False
        except AttributeError, e:
            #dstr = "%s %s" % (self.descriptionA.to_str(), self.descriptionB.to_str())
            #sys.stderr.write("attribute error %s for %s %s\n" % (e, dstr, example))
            return False

        dd = distance(a, b)

        satisfied = self.center - self.range < dd < self.center + self.range
        if self.negated:
            satisfied = not satisfied

        if satisfied:
            example.__dict__[self.name] = dd 
            return satisfied # ho ho
        else:
            return False

    def __repr__(self):
        return "%s (%s : %s)" % (self.name, self.center - self.range, self.center + self.range)

class AngleBoundCondition(Condition):

    def __init__(self, descriptionA, descriptionB, descriptionC, center, range, name="Angle"):
        self.descriptionA = descriptionA
        self.descriptionB = descriptionB
        self.descriptionC = descriptionC

        self.center = float(center)
        self.range  = float(range)

        self.name = name

    def satisfiedBy(self, example):
        try:
            a = lookup(self.descriptionA, example).center
            b = lookup(self.descriptionB, example).center
            c = lookup(self.descriptionC, example).center

        except DescriptionException:
            return False
        except AttributeError:
            return False

        aa = angle(a, b, c)

        satisfied = self.center - self.range < aa < self.center + self.range
        if self.negated:
            satisfied = not satisfied

        if satisfied:
            chain.__dict__[self.name] = aa
            return satisfied # ho ho
        else:
            return False

    def __repr__(self):
        return "%s (%s : %s)" % (self.name, self.center - self.range, self.center + self.range)

class TorsionBoundCondition(Condition):

    def __init__(self, descriptionA, descriptionB, descriptionC, descriptionD, center, range, name="Torsion"):
        self.descriptionA = descriptionA
        self.descriptionB = descriptionB
        self.descriptionC = descriptionC
        self.descriptionD = descriptionD

        self.center = float(center)
        self.range  = float(range)

        self.name = name

    def satisfiedBy(self, example):
        try:
            a = lookup(self.descriptionA, example).center
            b = lookup(self.descriptionB, example).center
            c = lookup(self.descriptionC, example).center
            d = lookup(self.descriptionD, example).center

        except DescriptionException:
            return False
        except AttributeError:
            return False

        t = torsion(a, b, c, d)
        #print a, b, c, d
        #print self.center -self.range, t, self.center + self.range

        satisfied = self.center - self.range < t < self.center + self.range
        if self.negated:
            satisfied = not satisfied

        if satisfied:
            example.__dict__[self.name] = t
            return satisfied # ho ho
        else:
            return False

    def __repr__(self):
        return "%s (%s : %s)" % (self.name, self.center - self.range, self.center + self.range)

class HBondCondition(Condition):

    def __init__(self, donorDescription, hydrogenDescription, acceptorDescription, attachedDescription, distanceHACutoff, angleDHACutoff, angleHAACutoff, name="HBond"):
        self.donorDescription = donorDescription
        self.hydrogenDescription = hydrogenDescription
        self.acceptorDescription = acceptorDescription
        self.attachedDescription = attachedDescription

        self.distanceHACutoff = distanceHACutoff
        self.angleDHACutoff = angleDHACutoff
        self.angleHAACutoff = angleHAACutoff

        self.name = name

    def satisfiedBy(self, example):
        try:
            donorAtomPosition    = lookup(self.donorDescription, example).center
            hydrogenAtomPosition = lookup(self.hydrogenDescription, example).center
            acceptorAtomPosition = lookup(self.acceptorDescription, example).center
            attachedAtomPosition = lookup(self.attachedDescription, example).center

        except DescriptionException, d:
            return False
        except AttributeError, ae:
            #sys.stderr.write(str(ae) +"\n")
            #import traceback
            #traceback.print_exc(limit=4, file=sys.stdout)
            return False

        satisfied = False
        #h_a, dha, haa = None, None, None
        h_a = distance(hydrogenAtomPosition, acceptorAtomPosition)
        if h_a < self.distanceHACutoff:
            dha = angle(donorAtomPosition, hydrogenAtomPosition, acceptorAtomPosition)
            if dha > self.angleDHACutoff:
                haa = angle(hydrogenAtomPosition, acceptorAtomPosition, attachedAtomPosition)
                if haa > self.angleHAACutoff:
                    satisfied = True
        #sys.stderr.write("satisfied %s %s %s %s\n" % (satisfied, h_a, dha, haa)) 

        if self.negated:
            if satisfied:
                return False
            else:
                return True
        else:
            if satisfied:
                example.__dict__[self.name + ":H_A"] = h_a
                example.__dict__[self.name + ":DHA"] = dha
                example.__dict__[self.name + ":HAA"] = haa
                return True
            else:
                return False

    def featureDonorAcceptorStr(self):
        return "%s %s %s %s" % (self.donorDescription, self.hydrogenDescription, self.acceptorDescription, self.attachedDescription)
    
    def geometricParameterStr(self):
        return "%0.2f %f %f" % (self.distanceHACutoff, self.angleDHACutoff, self.angleHAACutoff)

    def __repr__(self):
        return "%s (%s %s)" % (self.name, self.featureDonorAcceptorStr(), self.geometricParameterStr())
