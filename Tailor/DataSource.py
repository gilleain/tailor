"""
Classes that provide data :
    - PDBFileList : read from flat PDB files
    - StructureGenerator : generate structures from angles
"""
import os

from Parse import PDBParser, StructureBuilder
from Generate import makeFragment

def structureFromFile(filepath):
    parser = PDBParser(StructureBuilder())
    return parser.getStructure(filepath)

class StructureGenerator(object):

    def __init__(self, angleRanges, stepSize):
        self.angleRanges = angleRanges
        self.stepSize = stepSize

        self.numberOfModels = 1
        for angleRange in angleRanges:
            start, stop = angleRange
            self.numberOfModels *= math.fabs(stop - start) / stepSize

        self.angles = [start for start, stop in angleRanges]
        self.changing_angle_index = 0
        self.sweep_index = 1

    def updateIndices(self):
        currentlyChangingAngle = self.angles[self.changing_angle_index]
        nextAngleValue = self.angleRanges[self.changing_angle_index][0] + stepSize
        if currentlyChangingAngle > nextAngleValue:
            self.currently_changing_index += 1 
        else:
            self.sweep_index += 1

    def __iter__(self):
        for m in range(self.numberOfModels):
            angles = [(self.angles[i], self.angles[i + 1]) for i in range(0, len(self.angles), 2)]
            yield makeFragment(angles)
            self.updateIndices()

class PDBFileList(object):

    def __init__(self, path, structureList=[]):
        self.path = path
        self.pathMap = {}
        self.structure_paths = []

        if os.path.isdir(path):
            if structureList == []:
                for filename in os.listdir(path):
                    if filename[0] == ".": continue # avoid dotfiles (eg .svn dirs)
                    fullpath = os.path.join(path, filename)
                    self.mapFilename(filename, fullpath)
                    self.structure_paths.append(fullpath)
            else:
                print(structureList)
                filenamesInDir = os.listdir(path)
                if filenamesInDir == []:
                    raise IOError, "WARNING : No files in directory %s" % path    
                for filename in structureList:
                    if filename in filenamesInDir:
                        fullpath = os.path.join(path, filename)
                        self.mapFilename(filename, fullpath)
                        self.structure_paths.append(fullpath)
                    else:
                        raise IOError, "WARNING : File %s is not in directory %s" % (filename, path)
        elif os.path.isfile(path):
            self.structure_paths = [path]
        else:
            raise IOError, "WARNING : The path %s is not a directory or a file - it may not exist!" % path

        self.parser = PDBParser(StructureBuilder())

    def mapFilename(self, filename, fullpath):
        # XXX take the first four letters of a filename
        id = filename[0:4]
        self.pathMap[id] = fullpath 

    def __iter__(self):
        try:
            for i, path in enumerate(self.structure_paths):
                yield self.parser.getStructure(path)
        except IOError, ioe:
            raise IOError, "I/O problem with path [%s] %s" % (path, ioe)

    def __getitem__(self, key):
        path = self.pathMap[key]
        return self.parser.getStructure(path)

    def __repr__(self):
        return self.path

