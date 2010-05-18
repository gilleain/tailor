# read in a result list and cutoff based on the value of a particular column

import sys

filename = sys.argv[1]
column = int(sys.argv[2])
cutoff = float(sys.argv[3])
isMax = bool(int(sys.argv[4]))

if filename == "-":
    fp = sys.stdin
else:
    fp = open(filename)

for line in fp.readlines()[1:]:
    data = float(line.split("\t")[column])
    if (isMax and data < cutoff) or (not isMax and data > cutoff):
        sys.stdout.write(line)
fp.close()
