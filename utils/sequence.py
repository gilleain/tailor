# simple util for analyzing sequence columns in results

import sys
from operator import itemgetter

columns = eval(sys.argv[2])

tlc = ["ALA", "ARG", "ASN", "ASP", "CYS",\
       "GLU", "GLN", "GLY", "HIS", "ILE",\
       "LEU", "LYS", "MET", "PHE", "PRO",\
       "SER", "THR", "TRP", "TYR", "VAL"]
freq_maps = [dict([(res, 0) for res in tlc]) for i in range(len(columns))]

for line in open(sys.argv[1]):
    bits = line.rstrip("\n").split("\t")
    for index, column in enumerate(columns):
        residue_name = bits[column]
        freq_maps[index][residue_name] += 1

for position, map in enumerate(freq_maps):
    print(position, " ".join(["%s %3s" % (k, v) for k, v  in sorted(map.items(), key=itemgetter(1), reverse=True)]))
