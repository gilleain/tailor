from reportlab.graphics.shapes import Drawing 
from reportlab.graphics.charts.barcharts import VerticalBarChart 

def frange(start, stop, step):
    while start < stop:
        yield start
        start += step

def assignToBin(value, hist):
    for key in hist:
        if key[0] <= value <= key[1]:
            hist[key].append(value)
            return
    raise ValueError, "unassigned %s" % value

def printBins(bins):
    keys = bins.keys()
    keys.sort()
    for k in keys:
        mid = (k[1] + k[0]) / 2.0
        print "%0.2f %3i" % (mid, len(bins[k]))

def chartBins(bins, filename):
    data = []
    labels = []

    keys = bins.keys()
    keys.sort()

    for k in keys:
        mid = (k[1] + k[0]) / 2.0
        labels.append(str(mid))
        data.append(len(bins[k]))

    maxval = max(data)
    data = [tuple(data)]  

    # now, make the chart
    bc = VerticalBarChart()
    bc.x = 50
    bc.y = 50
    bc.height = 500
    bc.width = 1100
    bc.data = data

    bc.valueAxis.valueMin = 0
    bc.valueAxis.valueStep = 10
    bc.valueAxis.valueMax = maxval + bc.valueAxis.valueStep

    bc.categoryAxis.labels.boxAnchor = 'n'
    #bc.categoryAxis.labels.dx = 8
    #bc.categoryAxis.labels.dy = -2
    #bc.categoryAxis.labels.angle = 30

    new_labels = []
    for i, l in enumerate(labels):
        if i % 5 == 0:
            new_labels.append(l)
        else:
            new_labels.append("")

    #bc.categoryAxis.categoryNames = labels 
    bc.categoryAxis.categoryNames = new_labels

    drawing = Drawing(1200, 600)
    drawing.add(bc)

    from reportlab.graphics import renderPM
    renderPM.drawToFile(drawing, output_filename)

# the main program

import sys

input_filename = sys.argv[1]

column = int(sys.argv[2])
start = float(sys.argv[3])
stop = float(sys.argv[4])
step = float(sys.argv[5])

if len(sys.argv) > 6:
    output_filename = sys.argv[6]
else:
    output_filename = None

bins = dict([((f, f + step), []) for f in frange(start, stop, step)])
for line in open(input_filename).readlines()[1:]:
    bits = line.rstrip("\n").split("\t")
    try:
        data = float(bits[column])
        assignToBin(data, bins)
    except ValueError, v:
        sys.stderr.write(str(v) + "\n")

if output_filename is None:
    printBins(bins)
else:
    chartBins(bins, output_filename)

