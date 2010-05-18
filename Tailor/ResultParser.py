"""
A module to convert text lists of results into Feature objects.

Use like:

    def parse(line):
        pdbid, chain, start, stop = line.split()
        return ExampleDescription(pdbid, chain, start, stop)

    for e in generateExamples(filename, pdbdir, parse): print e

"""
import sys
from copy import copy
from Feature import Structure, Model, Chain, Residue, Atom
from DataSource import PDBFileList

class ExampleStream(object):

    def __init__(self, pdbdirectory):
        self.pdbFileList = PDBFileList(pdbdirectory)

    def examples(self, pdbid, exampleDescriptions):
        structure = self.pdbFileList[pdbid]

        try:
            for exampleDescription in exampleDescriptions:
                concreteExample = exampleDescription.findIn(structure)
                yield concreteExample
        except Exception, e:
            sys.stderr.write("%s\n" % str(e))

def generateExamples(filename, pdbdir, parse_func):
    # read in the data from a file and convert to an example description
    examples = {}
    for line in open(filename):
        try:
            exampleDescription = parse_func(line)
            examples.setdefault(exampleDescription.pdbid, []).append(exampleDescription)
        except Exception, e: # TODO FIXME
            sys.stderr.write(str(e) + "\n")

    # read in the structures and produce Structure Trees
    exampleStream = ExampleStream(pdbdir)
    for pdbid in examples:
        sys.stderr.write("%s\n" % pdbid)
        for example in exampleStream.examples(pdbid, examples[pdbid]):
            yield example

# XXX have to make this more general and less hacky
class ExampleDescription(object):

    def __init__(self, pdbid, chain, residueStart, residueEnd, ligand_num):
        self.pdbid = pdbid
        self.chain = chain
        self.residueStart = int(residueStart)
        self.residueEnd = int(residueEnd)
        self.ligand_num = int(ligand_num)

    #TODO : would make more sense to use the Description class with very restrictive contraints?
    def findIn(self, structure):
        example = Structure(self.pdbid)

        # TODO : implement model stuff :(
        model = structure[0]
        modelFeature = Model(0)
        example.add(modelFeature)

        for chain in model:
            if chain.chainID == self.chain or self.chain == " ":
                #print "[%s]" % chain.chainID
                chainFeature = Chain(chain.chainID, chainType="Protein")

                # XXX obviously better to access by index...
                for residue in chain:
                    if self.residueStart <= int(residue.number) <= self.residueEnd:

                        # XXX better to use copy()?
                        residueFeature = Residue(residue.number, residue.resname)
                        for atom in residue:
                            residueFeature.add(copy(atom))
                        chainFeature.add(residueFeature)
    
                modelFeature.add(chainFeature)
            else:
                continue
        # now add the ligand (as of this moment, a water molecule) XXX ugh!
        waterChain = Chain("Water", "Water")
        waterResidue = Residue(self.ligand_num, "HOH")
        #print "getting", self.ligand_num
        waterchains = structure.chainsOfType("Water")
        #print "chains", waterchains
        existingWater = waterchains[0].getResidueNumber(self.ligand_num)
        waterResidue.add(copy(existingWater.getAtom("O")))
        waterChain.add(waterResidue)
        modelFeature.add(waterChain)

        return example

    def __repr__(self):
        return "%s.%s %s-%s %s" % (self.pdbid, self.chain, self.residueStart, self.residueEnd, self.ligand_num)

if __name__ == "__main__":
    import sys

    filename = sys.argv[1]
    pdbdir = sys.argv[2]

    def parse(line):
        pdbid, chain, start, stop = line.split(".")
        return ExampleDescription(pdbid, chain, start, stop)

    def recurse(tree):
        print tree.__class__.__name__, tree
        for child in tree: recurse(child)

    for e in generateExamples(filename, pdbdir, parse):
        recurse(e)
