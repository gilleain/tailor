from Engine import lookup
from Exception import DescriptionException
from Geometry import angle, distance, torsion

class Measure(object):

    def __init__(self):
        pass

    def setName(self, name):
        self.name = name

    def getName(self):
        try:
            return self.name
        except AttributeError:
            return None

class PropertyMeasure(Measure):

    def __init__(self, description, propertyName, valueType):
        self.description = description
        self.propertyName = propertyName
        self.valueType = valueType

    def measure(self, feature):
        try:
            f = feature.findFeature(self.description)
            propertyValue = f.__dict__[self.propertyName]
            return propertyValue
        except AttributeError:
            return "None"
        except DescriptionException:
            return "!"

    def getNumberOfColumns(self):
        return 1

    def getColumnHeaders(self):
        name = self.getName()
        if name is None:
            return self.propertyName
        else:
            return name

    def getFormatStrings(self):
        if self.valueType == int:
            return "%i"
        elif self.valueType == float:
            return "%0.2f"
        else:
            return "%s"

    def __repr__(self):
        return "%s=%s" % (self.description, self.propertyName)

class DistanceMeasure(Measure):

    def __init__(self, descriptionA, descriptionB):
        self.descriptionA = descriptionA
        self.descriptionB = descriptionB

    def measure(self, structure):
        try:
            a = lookup(self.descriptionA, structure).center
            b = lookup(self.descriptionB, structure).center

            return distance(a, b)
        except AttributeError, ae:
            print("DISTANCE MEASURE FALIURE :", self.descriptionA, self.descriptionB, str(ae))
            return 0.0

    def getNumberOfColumns(self):
        return 1

    def getColumnHeaders(self):
        name = self.getName()
        if name is None:
            return "Distance"
        else:
            return name

    def getFormatStrings(self):
        return "%0.2f"

    def __repr__(self):
        return "D(%s, %s)" % (self.descriptionA, self.descriptionB)

class AngleMeasure(Measure):

    def __init__(self, descriptionA, descriptionB, descriptionC):
        self.descriptionA = descriptionA
        self.descriptionB = descriptionB
        self.descriptionC = descriptionC

    def measure(self, structure):
        try:
            a = lookup(self.descriptionA, structure).center
            b = lookup(self.descriptionB, structure).center
            c = lookup(self.descriptionC, structure).center

            return angle(a, b, c)
        except AttributeError:
            print("ANGLE MEASURE FALIURE :", self.descriptionA, self.descriptionB, self.descriptionC)
            return 0.0

    def getNumberOfColumns(self):
        return 1

    def getColumnHeaders(self):
        name = self.getName()
        if name is None:
            return "Angle"
        else:
            return name

    def getFormatStrings(self):
        return "%0.2f"

    def __repr__(self):
        return "A(%s, %s, %s)" % (self.descriptionA, self.descriptionB, self.descriptionC)

class TorsionMeasure(Measure):

    def __init__(self, descriptionA, descriptionB, descriptionC, descriptionD):
        self.descriptionA = descriptionA
        self.descriptionB = descriptionB
        self.descriptionC = descriptionC
        self.descriptionD = descriptionD

    def measure(self, structure):
        try:
            a = lookup(self.descriptionA, structure).center
            b = lookup(self.descriptionB, structure).center
            c = lookup(self.descriptionC, structure).center
            d = lookup(self.descriptionD, structure).center

            return torsion(a, b, c, d)
        except AttributeError:
            print("TORSION MEASURE FALIURE :", self.descriptionA, self.descriptionB, self.descriptionC, self.descriptionD)
            return 0.0

    def getNumberOfColumns(self):
        return 1

    def getColumnHeaders(self):
        name = self.getName()
        if name is None:
            return "Torsion"
        else:
            return name

    def getFormatStrings(self):
        return "%0.2f"

    def __repr__(self):
        return "T(%s, %s, %s, %s)" % (self.descriptionA, self.descriptionB, self.descriptionC, self.descriptionD)

class HBondMeasure(Measure):

    def __init__(self, donorDescription, hydrogenDescription, acceptorDescription, attachedDescription):
        self.donorDescription = donorDescription
        self.hydrogenDescription = hydrogenDescription
        self.acceptorDescription = acceptorDescription
        self.attachedDescription = attachedDescription

    def measure(self, structure):
        try:
            donorAtom    = lookup(self.donorDescription, structure)
            hydrogenAtom = lookup(self.hydrogenDescription, structure)
            acceptorAtom = lookup(self.acceptorDescription, structure)
            attachedAtom = lookup(self.attachedDescription, structure)

            #print(donorAtom.toFullString(), hydrogenAtom.toFullString(), acceptorAtom.toFullString(), attachedAtom.toFullString())

        except DescriptionException, d:
            # better to re-raise some kind of error?
            return (0,0,0)

        dhDistance = distance(hydrogenAtom.center, acceptorAtom.center)
        dhaAngle   = angle(donorAtom.center, hydrogenAtom.center, acceptorAtom.center)
        haaAngle   = angle(hydrogenAtom.center, acceptorAtom.center, attachedAtom.center)

        return (dhDistance, dhaAngle, haaAngle)

    def getNumberOfColumns(self):
        return 3

    def getColumnHeaders(self):
        name = self.getName()
        if name is None:
            name = "HBond"
        return ("%s:D_H" % name, "%s:DHA" % name, "%s:HAA" % name)

    def getFormatStrings(self):
        return ["%0.2f", "%0.2f", "%0.2f"]

    def __repr__(self):
        return "HB(%s, %s, %s, %s)" % (self.donorDescription, self.hydrogenDescription, self.acceptorDescription, self.attachedDescription)
