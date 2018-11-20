"""
Merge the motifs in a list into non-overlapping examples.
"""

class Result(object):

    def __init__(self, line):
        bits = line.rstrip("\n").split("\t")
        self.pdbid = bits[0]
        resrange, ligtype, lignum = bits[1][2:].split(" ")
        self.start, self.end = [int(s) for s in resrange.split("-")]
        self.rest = bits[2:]

    def calculateOverlap(self, other):
        """
            Assumes that self is BEFORE other in the sequence!
        """
        return (self.end - other.start) + 1

    def overlaps(self, other, minOverlap=-1):
        if minOverlap == -1:
            return (self.end >= other.start and self.end <= other.end)\
                 or (other.end >= self.start and self.start >= other.start)
        else:
            overlap = self.calculateOverlap(other)
            return overlap >= minOverlap

    def contains(self, other):
        return self.start <= other.start and self.end >= other.end

    def duplicate(self, other):
        return self.start == other.start and self.end == other.end

    def __repr__(self):
        return "%s\t%s %s-%s\t%s" % (self.pdbid, self.motifname, self.start, self.end, "\t".join(self.rest))

class MergedResult(object):

    def __init__(self, id, columnResidueIndexMap):
        self.pdbid = id
        self.columnResidueIndexMap = columnResidueIndexMap
        self.start = 0
        self.end = 0
        self.results = []

    def append(self, result):
        if len(self.results) == 0:
            self.start = result.start
        self.end = result.end
        self.results.append(result)

    def overlaps(self, result, minOverlap=-1):
        """
            This is slightly counter-intuitive : the null MergedResult object
            overlaps with all Results.
        """
        if len(self.results) == 0:
            return True
        else:
            return self.results[-1].overlaps(result, minOverlap)

    def overlapMeasurements(self):
        measurements = []
        lastResult = self.results[0]
        measurements.extend(lastResult.rest[:])

        for result in self.results[1:]:
            offset = result.start - self.start
            for i, otherMeasurement in enumerate(result.rest):
                residueIndex = self.columnResidueIndexMap[i]
                shiftedIndex = residueIndex + offset
                if shiftedIndex > len(measurements):
                    measurements.append(otherMeasurement) 
            lastResult = result
        return measurements 

    def __len__(self):
        # these shenannigans are because the length can 
        # sometimes be negative (due to chain breaks)
        apparentLength = 1 + (self.end - self.start)
        return max(apparentLength, 0)

    def __repr__(self):
        return "%s\t%s-%s\t%s" % (self.pdbid, self.start, self.end,"\t".join(self.overlapMeasurements()))

import sys

# read in the results from the file
results = {}
fp = open(sys.argv[1])
for line in fp.readlines()[1:]:
    try:
        r = Result(line)
        results.setdefault(r.pdbid, []).append(r)
    except IndexError, ie:
        pass
    except ValueError, ie:
        pass
fp.close()    

# the column->residue mappings
columnResidueMap = eval(sys.argv[2])

# the minimum overlap
if len(sys.argv) > 3:
    minOverlap = int(sys.argv[3])
else:
    minOverlap = -1

# merge the list
merged_results = []
for id in results:
    current_merged_result = MergedResult(id, columnResidueMap)
    for result in results[id]:
        if current_merged_result.overlaps(result, minOverlap):
            current_merged_result.append(result)
        else:
            merged_results.append(current_merged_result)
            current_merged_result = MergedResult(id, columnResidueMap)
            current_merged_result.append(result)

# write out the merged results
for m in merged_results:
    print(len(m), m)
