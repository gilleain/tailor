import os
from Feature import Structure, Model, Chain, Residue, Atom
from Geometry import Vector

def getPDBIDFromPath(path):
    return os.path.splitext(os.path.basename(path))[0][0:4]

class StructureBuilder:

    def __init__(self):
        self.reset()

    def reset(self):
        self.structure  = None
        self.model      = None
        self.chain      = None
        self.residue    = None
        
        self.modelID    = None
        self.chainID    = None
        self.chainType  = None
        self.segID      = None
        self.residueID  = None
        self.resname    = None

    def getStructure(self):
        structure = self.structure
        self.reset()
        return structure

    def initStructure(self, structureID):
        self.structure = Structure(structureID)
        self.initModel()

    def initModel(self):
        if self.modelID is None:
            self.modelID = 0
        else:
            self.modelID += 1
        self.model = Model(self.modelID)
        self.structure.add(self.model)
        self.chainID = None
        self.chainType = None

    def initChain(self, chainID, chainType):
        self.chainID = chainID
        self.chainType = chainType
        self.chain = Chain(chainID, chainType)
        self.model.add(self.chain)

    def registerLine(self, residueID, resname, resseq, icode, segID, chainID, chainType):
        if self.chainID != chainID or chainType != self.chainType:
            self.initChain(chainID, chainType)
            self.initResidue(residueID, resname, resseq, icode)

        elif self.residueID != residueID or self.resname != resname:
            self.initResidue(residueID, resname, resseq, icode)

        if self.segID != segID:
            self.segID = segID

    def initResidue(self, residueID, resname, resnum, icode):
        self.residueID = residueID
        self.resname   = resname
        self.residue   = Residue(residueID, resname, self.segID)
        self.chain.add(self.residue)

    def initAtom(self, name, coord, b_factor, occupancy, altloc):
        atom = Atom(name, coord, b_factor, occupancy, altloc)
        self.residue.add(atom)

class PDBParser:

    def __init__(self, builder):
        self.builder = builder

    def getStructure(self, path):
        pdbID = getPDBIDFromPath(path)
        self.builder.initStructure(pdbID)

        openfile = file(path, 'r')
        for i, line in enumerate(openfile):
            record_type = line[0:6]

            if (record_type == 'ATOM  ' or record_type == 'HETATM'):
                name     = line[12:16]
                altloc   = line[16:17]
                resname  = line[17:20]
                chainID  = line[21:22]
                resseq   = int(line[22:26])      # sequence identifier   
                icode    = line[26:27]           # insertion code

                residueID = (resseq, icode)

                # atomic coordinates
                x = float(line[30:38]) 
                y = float(line[38:46]) 
                z = float(line[46:54])
                coord = (x, y, z)

                # occupancy & B factor
                occupancy = float(line[54:60])
                bfactor = float(line[60:66])

                segID = line[72:76]

                if record_type == 'HETATM':
                    if resname in ["HOH", "DOD"]:
                        chainID = "Water"
                        chainType = "Water"
                    else:
                        chainID = "Ligand"
                        chainType = "Ligand"
                else:
                    chainType = "Protein"

                self.builder.registerLine(residueID, resname, resseq, icode, segID, chainID, chainType)

                # init atom
                self.builder.initAtom(name, coord, bfactor, occupancy, altloc)

            elif(record_type == 'ENDMDL'):
                self.builder.initModel()

            elif(record_type == 'END   ' or record_type == 'CONECT'):
                break 

        openfile.close()
        return self.builder.getStructure()

if __name__ == "__main__":

    import sys

    p = PDBParser(StructureBuilder())
    s = p.getStructure(sys.argv[1])
    print(s, "has", len(s), "models")
    for model in s:
        print(model, "has", len(model), "chains")
        for chain in model:
            print(chain, "has", len(chain), "residues")
            for residue in chain:
                print(residue, "has", len(residue), "atoms" )
