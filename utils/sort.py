# stupid fucking gnu sort doesn't work

import sys

SINGLE_COLUMN = 1
DUAL_COLUMN = 2

mode = SINGLE_COLUMN

filename = sys.argv[1]
columnA = int(sys.argv[2])
columnB = -1

if len(sys.argv) > 3:
    columnB = int(sys.argv[3])
    mode = DUAL_COLUMN

if filename == "-":
    fp = sys.stdin
else:
    fp = open(filename, 'r')
lines = fp.readlines()
fp.close()

splitlines = [l.split("\t") for l in lines[1:]]

def single_cmp(x, y):
    return cmp(float(x[columnA]), float(y[columnA]))

def dual_cmp(x, y):
    xx = float(x[columnA]) + float(x[columnB]) / 2.0
    yy = float(y[columnA]) + float(y[columnB]) / 2.0
    return cmp(xx, yy)
    
if mode == SINGLE_COLUMN:
    splitlines = sorted(splitlines, cmp=single_cmp)
else:
    splitlines = sorted(splitlines, cmp=dual_cmp)

for sl in splitlines:
    sys.stdout.write("\t".join(sl))
