# manage the overlap between two lists of tailor results

# temporary classes! for bound catmats
class BoundMotif(object):

    def __init__(self, line):
        bits = line.rstrip("\n").split("\t")
        self.pdbid, self.chain = bits[0].split(".")
        self.start, self.end = [int(s) for s in bits[1].split("-")]
        self.angles = bits[2:-1]
        self.ligands = eval(bits[-1])

    def contains(self, other):
        return self.start == other.start and self.end == other.end + 1

    def overlap(self, other):
        return (self.end >= other.start and self.end <= other.end) or (other.end >= self.start and self.start >= other.start)

    def __repr__(self):
        return "%s.%s\t%s-%s\t%s\t%s" % (self.pdbid, self.chain, self.start, self.end, "\t".join(self.angles), self.ligands)

class BoundMotifResultList(object):

    def __init__(self, path):
        self.results = {}
        fp = open(path)
        for line in fp.readlines()[1:]:
            result = BoundMotif(line)
            if result.pdbid in self.results:
                self.results[result.pdbid].append(result)
            else:
                self.results[result.pdbid] = [result]
        fp.close()

    def overlap(self, other):
        intersection = []
        selfMinusOther = []

        for pdbid in self.results:
            if pdbid in other.results:
                for selfResult in self.results[pdbid]:
                    overlap = findOverlap(selfResult, other.results[pdbid])
                    if overlap is None:
                        selfMinusOther.append(selfResult) 
                    else:
                        intersection.append(overlap)
            else:
                for result in self.results[pdbid]:
                    selfMinusOther.append(result)
        return intersection, selfMinusOther

class Result(object):

    def __init__(self, line):
        bits = line.rstrip("\n").split("\t")
        self.pdbid = bits[0]
        chainType, self.motifname, resrange = bits[1].split(" ")
        self.start, self.end = [int(s) for s in resrange.split("-")]
        self.angles = bits[2:]

    def overlap(self, other):
        return (self.end >= other.start and self.end <= other.end) or (other.end >= self.start and self.start >= other.start)

    def contains(self, other):
        return self.start <= other.start and self.end >= other.end

    def duplicate(self, other):
        return self.start == other.start and self.end == other.end

    def __repr__(self):
        return "%s\t%s %s-%s\t%s" % (self.pdbid, self.motifname, self.start, self.end, "\t".join(self.angles))

def findOverlap(result, otherResults):
    for otherResult in otherResults:
        """
        if result.contains(otherResult):
            return otherResult
        elif otherResult.contains(result):
            return otherResult
        """
        if otherResult.overlap(result):
            return otherResult
    return None

class ResultList(object):

    def __init__(self, path):
        self.results = {}
        fp = open(path)
        for line in fp.readlines()[1:]:
            result = Result(line)
            if result.pdbid in self.results:
                self.results[result.pdbid].append(result)
            else:
                self.results[result.pdbid] = [result]
        fp.close()

    def overlap(self, other):
        intersection = []
        selfMinusOther = []

        for pdbid in self.results:
            if pdbid in other.results:
                for selfResult in self.results[pdbid]:
                    overlap = findOverlap(selfResult, other.results[pdbid])
                    if overlap is None:
                        selfMinusOther.append(selfResult) 
                    else:
                        intersection.append(overlap)
            else:
                for result in self.results[pdbid]:
                    selfMinusOther.append(result)
        return intersection, selfMinusOther

import sys

# input
pathA = sys.argv[1]
pathB = sys.argv[2]

# output
intersectionPath = sys.argv[3]
aMinusBPath = sys.argv[4]
bMinusAPath = sys.argv[5]

#resultsA = ResultList(pathA)
#resultsB = ResultList(pathB)

resultsA = BoundMotifResultList(pathA)
resultsB = ResultList(pathB)

intersection, aMinusB = resultsA.overlap(resultsB)
intersection, bMinusA = resultsB.overlap(resultsA)
print len(intersection), len(aMinusB), len(bMinusA)

intersectionFP = open(intersectionPath, 'w')
for result in intersection:
    intersectionFP.write(str(result) + "\n")

aMinusBFP = open(aMinusBPath, 'w')
for result in aMinusB:
    aMinusBFP.write(str(result) + "\n")

bMinusAFP = open(bMinusAPath, 'w')
for result in bMinusA:
    bMinusAFP.write(str(result) + "\n")
