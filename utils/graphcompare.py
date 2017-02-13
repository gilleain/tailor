import sys, re
def flip(vstr):
    swap = []
    for c in vstr:
        if c in ["N", "C"]:
            swap.append(c)
        else:
            swap.append(c.swapcase())
    return "".join(swap)

import re
epatt = re.compile("(\d+):(\d+)(.)")
def doedges(oldstr, newstr):
    olds = epatt.findall(oldstr)
    news = epatt.findall(newstr)
    missing = []
    for oldedge in olds:
        if oldedge in news:
            continue
        else:
            missing.append(oldedge)
    return missing

def dovertices(oldvstr, vstr):
    # exactly correct
    if oldvstr == vstr or flip(oldvstr) == vstr:
        return []
    else:
        if len(oldvstr) == len(vstr) and oldvstr.lower() == vstr.lower():
            incorrect = []
            for i, c in enumerate(oldvstr):
                if c != vstr[i]:
                    incorrect.append((i, c))
            return incorrect
        else:
            return ["tricky"] 

def compare(id, oldvstr, vstr, oldestr, estr):
    missing_verts = dovertices(oldvstr, vstr)
    missing_edges = doedges(oldestr, estr)
    verdicts = []
    if missing_verts == []:
        verdicts.append("nomissingverts")
    else:
        verdicts.append("missingverts")
    if missing_edges == []:
        verdicts.append("nomissingedges")
    else:
        verdicts.append("missingedges")

    print verdicts, id, oldvstr, vstr, oldestr, estr, missing_verts, missing_edges

linepatt = re.compile("(.+)\s(.+)\s+pattern:\s+(.+)\s+(.*)\s+for\s.+\s(.+)\s(.+)\s\<\=\>\s.+\s(.+)\s(.+)")
for line in open(sys.argv[1]):
    #if "pattern" not in line: continue
    m = linepatt.match(line)
    if m:
        #print m.groups()
        score, id, pvstr, evstr, oldvstr, oldestr, newvstr, newestr = m.groups()
        #print score, newvstr, newestr
        compare(id, oldvstr, newvstr, oldestr, newestr)
