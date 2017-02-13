# count up the number of structures with a compression better than half

import sys

count = 0
for line in sys.stdin:
    compression, rest = line.split('\t', 1)
    if float(compression) > 0.5:
        count += 1
print count
