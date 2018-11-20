# read in two tailor results and find intersection

import sys

def readResults(filepath):
    fp = open(filepath)
    motifMap = {}
    for line in fp.readlines()[1:]:
        bits = line.rstrip().split("\t")
        pdbid, motif = bits[0:2]
        angles = bits[2:]
        motif = [int(x) for x in motif.split(' ')[1].split("-")]
        if pdbid in motifMap:
            motifMap[pdbid].append(motif)
        else:
            motifMap[pdbid] = [motif]
    fp.close()
    return motifMap

def match(motif, motifList):
    s1, e1 = motif
    for other in motifList:
        s2, e2 = other
        if s1 == s2 and e1 + 1 == e2:
            return True
    return False

file1 = sys.argv[1]
file2 = sys.argv[2]

m1 = readResults(file1)
m2 = readResults(file2)

for pdbid in m1:
    if pdbid in m2:
        for motif in m1[pdbid]:
            if match(motif, m2[pdbid]):
                continue
            else:
                print(pdbid, motif, "not in", file2)
    else:
        print pdbid, "not in", file2
        continue
