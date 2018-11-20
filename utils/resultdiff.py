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
            motifMap[pdbid].append((motif, angles))
        else:
            motifMap[pdbid] = [(motif, angles)]
    fp.close()
    return motifMap

file1 = sys.argv[1]
file2 = sys.argv[2]

m1 = readResults(file1)
m2 = readResults(file2)

overlaps = {}
for pdbid in m1:
    if pdbid in m2:
        motifs1 = m1[pdbid]
        motifs2 = m2[pdbid]
        for motif1, angles1 in motifs1:
            s1, e1 = motif1
            for motif2, angles2 in motifs2:
                s2, e2 = motif2
                if s2 < s1 < e2 or s1 < s2 < e1 or s2 < e1 < e2 or s1 < e2 < e2:
                    astr = "\t".join(angles1)
                    astr2 = "\t".join(angles2)
                    mstr = "-".join([str(x) for x in motif1])
                    mstr2 = "-".join([str(x) for x in motif2])
                    #print "%s\th %s\t%s" % (pdbid, mstr, astr)
                    #print "%s\th %s\t%s" % (pdbid, mstr2, astr2)
                    overlap = s2 - s1
                    print(overlap, pdbid, mstr, mstr2)
                    if overlap in overlaps:
                        overlaps[overlap] += 1
                    else:
                        overlaps[overlap] = 1
    else:
        continue
#for overlap in overlaps:
    #print overlap, overlaps[overlap]
