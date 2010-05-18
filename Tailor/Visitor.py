ANY     = -1
PROTEIN =  0
CHAIN   =  1
RESIDUE =  2
ATOM    =  3

class Structure(object):

    def __init__(self, label, level, data):
        self.label = label
        self.level = level
        self.data = data
        self.children = []

    def add(self, structure):
        self.children.append(structure)

    def addAll(self, structures):
        for structure in structures:
            self.add(structure)

    def copy(self):
        return Structure(self.label, self.level, self.data)

    def accept(self, visitor):
        return visitor.visit(self)

    def passDown(self, visitor):
        if self.accept(visitor):
            for child in self.children:
                child.passDown(visitor)

    def select(self, visitor):
        if visitor.level == self.level:
            groups = self.scan(visitor)
            results = []
            for group in groups:
                copySelf = self.copy()
                copySelf.addAll(group)
                results.append(copySelf)
            return results
        else:
            selections = []
            for child in self.children:
                results = child.select(visitor)
                for result in results:
                    copySelf = self.copy()
                    copySelf.add(result)
                    selections.append(copySelf)
            return selections

    def scan(self, visitor):
        if len(visitor.children) == 0:
            return []
        else:
            groups = []
            while visitor.index <= len(self.children) - len(visitor.children):
                group = self.findNextGroup(visitor)
                if group is not None:
                    groups.append(group)
            visitor.index = 0
            return groups

    def findNextGroup(self, visitor):
        group = []
        for childVisitor in visitor.children:
            child = self.children[visitor.index]
            visitor.index += 1
            if child.accept(childVisitor):
                group.append(child.copy())
            else:
                return
        return group

    def __repr__(self):
        return self.label + " " + self.data


p = Structure("Protein", PROTEIN, "AminoAcid")

cA = Structure("ChainA", CHAIN, "A")
cB = Structure("ChainB", CHAIN, "B")

r1 = Structure("Residue1", RESIDUE, "TYR")
r2 = Structure("Residue2", RESIDUE, "GLY")
r3 = Structure("Residue3", RESIDUE, "GLY")
r4 = Structure("Residue4", RESIDUE, "SER")

a1 = Structure("Atom1", ATOM, "N")
a2 = Structure("Atom2", ATOM, "C")
a3 = Structure("Atom3", ATOM, "N")
a4 = Structure("Atom4", ATOM, "C")
a5 = Structure("Atom5", ATOM, "N")
a6 = Structure("Atom6", ATOM, "C")
a7 = Structure("Atom7", ATOM, "N")
a8 = Structure("Atom8", ATOM, "C")

p.add(cA)

cA.add(r1)
r1.add(a1)
r1.add(a2)

cA.add(r2)
r2.add(a3)
r2.add(a4)

p.add(cB)

cB.add(r3)
r3.add(a5)
r3.add(a6)

cB.add(r4)
r4.add(a6)
r4.add(a7)

def printAction(visitor, structure):
    print structure
    return True

def captureAction(visitor, structure):
    visitor.store = structure
    return True

def levelEqual(visitor, structure):
    return visitor.level == structure.level

class Visitor(object):

    def __init__(self, level, action, data=None):
        self.level = level 
        self.action = action
        self.data = data
        self.children = []
        self.index = 0

    def add(self, visitor):
        self.children.append(visitor)

    def visit(self, structure):
        if self.data == structure.data:
            return self.action(self, structure)
        else:
            return False

    def __repr__(self):
        return "%s %s" % (self.level, self.index)

printV = Visitor(ANY, printAction)

compositeV = Visitor(CHAIN, levelEqual)
compositeV.add(Visitor(RESIDUE, levelEqual, "GLY"))
selections = p.select(compositeV)
for selection in selections:
    selection.passDown(printV)
    print selection
    print "-" * 10
