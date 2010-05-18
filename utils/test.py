from rama import RamachandranPlot
from reportlab.graphics import renderPDF
from reportlab.graphics.shapes import Drawing

def columnDataToPointSet(lines, numberOfPointSets, s, e):
    data = [[] for i in range(numberOfPointSets)]
    for line in lines:
        angles = [float(a) for a in line.split("\t")[s:e + 1]]
        for i in range(numberOfPointSets):
            idx = i * 2
            data[i].append((angles[idx], angles[idx+1]))
    return data

import sys

columnStart = int(sys.argv[1])
columnEnd   = int(sys.argv[2])

n = ((columnEnd - columnStart) + 1) / 2

d = Drawing(800, 800)

points = [(30, 430), (430, 430), (30, 30), (430, 30)]

for i, filename in enumerate(sys.argv[3:7]):
    r = RamachandranPlot()
    x, y = points[i]
    r.x = x
    r.y = y
    r.visibleGrid = 0
    r.gridSpacing = 90
    lines = open(filename).readlines()[1:]
    r.data = columnDataToPointSet(lines, n, columnStart, columnEnd)
    
    d.add(r)

renderPDF.drawToFile(d, "test.pdf", "")
