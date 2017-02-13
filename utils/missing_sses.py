import sys, re

for line in open(sys.argv[1]):
    if "no match" in line:
        bits = line.split()
        id, typ, start, end = bits[0], bits[1], bits[2], bits[4]
        try:
            len = int(end) - int(start)
            print len, id, typ, start, end
        except Exception:
            pass
