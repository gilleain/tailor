perfect_helix = 0
perfect_strand = 0
sorta_helix = 0
sorta_strand = 0
missing_helix = 0
missing_strand = 0
superfluous_helix = 0
superfluous_strand = 0

import sys
for line in open(sys.argv[1]):
    if "perfectly" in line:
        if "Helix" in line:
            perfect_helix += 1
        elif "Strand" in line:
            perfect_strand += 1
    elif "sorta" in line:
        if "Helix" in line:
            sorta_helix += 1
        elif "Strand" in line:
            sorta_strand += 1
    elif "no match" in line:
        if "Helix" in line:
            missing_helix += 1
        elif "Strand" in line:
            missing_strand += 1
    elif "superfluous" in line:
        if "Helix" in line:
            superfluous_helix += 1
        elif "Strand" in line:
            superfluous_strand += 1

print "perfect", perfect_helix + perfect_strand, "helix", perfect_helix, "strand", perfect_strand
print "sorta", sorta_helix + sorta_strand, "helix", sorta_helix, "strand", sorta_strand
print "missing", missing_helix + missing_strand, "helix", missing_helix, "strand", missing_strand
print "superfluous", superfluous_helix + superfluous_strand, "helix", superfluous_helix, "strand", superfluous_strand
