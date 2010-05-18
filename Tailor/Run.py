import os, sys
from DataSource import PDBFileList
from Engine import Matcher
from Exception import DescriptionException

class Run(object):

    def __init__(self, pipes, structurePath, structureList=[]):
        self.pipes = pipes
        self.structurePath = structurePath
        self.structureList = structureList

    def run(self, err=sys.stderr):
        try:
            # start out with this, to try and catch IOerrors before writing anything
            structures = PDBFileList(self.structurePath, self.structureList)

            for pipe in self.pipes:
                pipe.writeHeader()

            # now, do the actual work 
            for structure in structures:
                err.write("Processing : %s" % structure)

                try:
                    for pipe in self.pipes:
                        err.write(" %s" % pipe.description.name)
                        pipe.run(structure)
                except DescriptionException, d:
                    err.writelines(["", "Description problem " + str(d)])
                err.write(os.linesep)

        except IOError, ioe:
            err.writelines([str(ioe)])
            return

class Pipe(object):

    def __init__(self, description, measures, out=sys.stdout):
        self.description = description
        self.matcher = Matcher(description)
        self.measures = measures
        self.out = out

        self.columnSeparator = "\t"
        self.lineSeparator   = os.linesep

        self.numberOfColumns = 0
        for measure in measures:
            self.numberOfColumns += measure.getNumberOfColumns()

        formatStrings = []
        for measure in measures:
            formatStringValue = measure.getFormatStrings()
            if type(formatStringValue) == list:
                formatStrings.extend(formatStringValue)
            else:
                formatStrings.append(formatStringValue)
        self.resultTemplate = self.columnSeparator.join(formatStrings)

    def writeHeader(self):
        headerLine = self.getHeader()
        self.out.write(headerLine)

    def getHeader(self):
        initialHeader  = "pdbid%smotif" % self.columnSeparator
        measureHeaderList = []
        for measure in self.measures:
            columnHeaders = measure.getColumnHeaders()
            if type(columnHeaders) == tuple:
                measureHeaderList.extend(columnHeaders)
            else:
                measureHeaderList.append(columnHeaders)
        measureHeader = self.columnSeparator.join(measureHeaderList)
        return initialHeader + self.columnSeparator + measureHeader + self.lineSeparator

    def formatMotif(self, structure, motif):
        # TODO : handle multiple models
        try:
            motifStr = " ".join(["%s %s" % (c.chainID, c.residueRange) for c in motif[0]])
            return "%s%s%s" % (structure, self.columnSeparator, motifStr)
        except Exception, e:
            return str(motif)

    def formatResults(self, structure, motif, results):
        signature = self.formatMotif(structure, motif)
        try:
            resultStr = self.resultTemplate % tuple(results)
        except Exception, e:
            resultStr = " ".join([str(r) for r in results])
        return signature + self.columnSeparator + resultStr + self.lineSeparator

    def run(self, structure):
        for fragment in self.matcher.findAll(structure):
            results = []
            for measure in self.measures:
                result = measure.measure(fragment)
                if type(result) == tuple:
                    results.extend(result)
                else:
                    results.append(result)
            self.out.write(self.formatResults(structure, fragment, results)) 
