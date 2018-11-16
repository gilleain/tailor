from math import acos, sin, cos, degrees, sqrt, radians

def distance(a, b):
    return (a - b).length()

def angle(a, b, c):
    return degrees((b - a).angle(b - c))

def torsion(a, b, c, d):
    l = (b - a).cross(c - b)
    r = (d - c).cross(b - c)
    a = degrees(l.angle(r))

    if (l.cross(r) * (c - b)) < 0.0:
        return -a
    else:
        return a

def makeXYZFromAngle(a, b, distance, angle, plane_vector):
    angle_radians = radians(180 - angle)
    b_a = b - a
    b_a_normalised = b_a.normalize()
    x = distance * cos(angle_radians) * b_a_normalised
    y = distance * sin(angle_radians) * plane_vector
    v = x + y
    return b + v

def makeXYZ(previousXYZ, distance, previousButOneXYZ, angle, furthestXYZ, torsion):
    AB = furthestXYZ - previousButOneXYZ
    CB = previousXYZ - previousButOneXYZ
    normalAC = AB.cross(CB)

    if (normalAC.length() == 0.0):
        return makeXYZFromAngle(previousButOneXYZ, previousXYZ, distance, angle, Y)

    normalBC = CB.cross(normalAC)

    torsion_radians = radians(torsion)
    r = normalAC.normalize() * cos(torsion_radians) + normalBC.normalize() * sin(torsion_radians)
    newFrameOfReference = CB.normalize().cross(r)

    angle_radians = radians(angle)
    DC = -CB.normalize() * cos(angle_radians) + newFrameOfReference * sin(angle_radians)
    DC *= distance

    return previousXYZ + DC

def invertTorsion(torsion):
    if (torsion < 0):
        inverted_torsion = 180 + torsion
    else:
        inverted_torsion = torsion - 180
    return inverted_torsion

class Vector:

    def __init__(self, x, y, z):
        self.x = x
        self.y = y
        self.z = z

    def __repr__(self):
        return "(%.3f %.3f %.3f)" % (self.x, self.y, self.z)

    def __neg__(self):
        return Vector(-self.x, -self.y, -self.z)

    def __add__(self, other):
        if isinstance(other, Vector):
            return Vector(self.x + other.x, self.y + other.y, self.z + other.z)
        else:
            return Vector(self.x + other, self.y + other, self.z + other)

    def __radd__(self, other):
        return self.__add__(other)

    def __sub__(self, other):
        return Vector(self.x - other.x, self.y - other.y, self.z - other.z)

    def __rsub__(self, other):
        return Vector(other.x - self.x, other.y - self.y, other.z - self.z)

    def __mul__(self, other):
        if isinstance(other, Vector):
            return sum((self.x * other.x, self.y * other.y, self.z * other.z))
        else:
            return Vector(self.x * other, self.y * other, self.z * other)

    def __rmul__(self, other):
        return self.__mul__(other)

    def __div__(self, i):
        return Vector(self.x / i, self.y / i, self.z / i)

    def length(self):
        return sqrt(self * self)

    def normalize(self):
        self = self / self.length()
        return self

    def angle(self, other):
        try:
            c = (self * other) / (self.length() * other.length())
        except ZeroDivisionError, z:
            return 0
        return acos(max(-1., min(1., c)))

    def cross(self, other):
        return Vector(self.y * other.z - self.z * other.y,
                      self.z * other.x - self.x * other.z,
                      self.x * other.y - self.y * other.x)

    def copy(self):
        return Vector(self.x, self.y, self.z)

O = Vector(0, 0, 0)
X = Vector(1, 0, 0)
Y = Vector(0, 1, 0)
Z = Vector(0, 0, 1)

if __name__=="__main__":

        v1 = Vector(0, 0, 1)
        v2 = Vector(0, 1, 0)
        v3 = Vector(1, 0, 0)
        v4 = Vector(1, 1, 1)

        print(v1 + v2)
        print(v2 - v3)
        print(v1 * v3)
        print(v1 / 3)
        print(v2.length())
        print(v4.copy())
        print(v1.cross(v3))

        v4.normalize()
        print(v4)

        print(distance(v1, v2))
        print(angle(v1, v2, v3))
        print(torsion(v1, v2, v3, v4))

