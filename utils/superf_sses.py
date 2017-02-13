import sys, re

for line in open(sys.argv[1]):
    if "superf" in line:
        bits = line.split()
        id, typ, start, end = bits[0], bits[1], bits[4], bits[7]
        try:
            len = int(end) - int(start)
            print len, id, typ, start, end
        except Exception:
            pass
