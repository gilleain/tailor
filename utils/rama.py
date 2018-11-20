""" 
A module to make Ramachandran plots from lists of torsions.


"""

from reportlab.lib import colors
from reportlab.lib.validators import isNumber, isColor, isColorOrNone, isListOfStrings, \
                                    isListOfStringsOrNone, SequenceOf, isBoolean, NoneOr, \
                                    isListOfNumbersOrNone, isStringOrNone
from reportlab.lib.attrmap import *
from reportlab.graphics.shapes import Drawing, Group, Line, Rect
from reportlab.graphics.widgetbase import PropHolder, TypedPropertyCollection
from reportlab.graphics.charts.areas import PlotArea
from reportlab.graphics.charts.axes import XValueAxis, YValueAxis
from reportlab.graphics.charts.textlabels import Label
from reportlab.graphics.widgets.markers import isSymbol

class PointSetProperties(PropHolder):
    _attrMap = AttrMap(
        fillColor = AttrMapValue(isColorOrNone, desc='Point color.'),
        symbol = AttrMapValue(NoneOr(isSymbol), desc='Widget placed at point.'),
        name = AttrMapValue(isStringOrNone, desc='Name of the PointSet.')
    )

class RamachandranPlot(PlotArea):

    _attrMap = AttrMap(BASE=PlotArea,
        visibleGrid = AttrMapValue(isBoolean, desc='Flag for grid drawing.'),
        gridSpacing = AttrMapValue(isNumber, desc='One of [10,30,90,180].'),
        axesColor = AttrMapValue(isColorOrNone, desc='The color of the internal axes.'),
        xAxis = AttrMapValue(None, desc='Handle for the X axis.'),
        yAxis = AttrMapValue(None, desc='Handle for the Y axis.'),
        pointSets = AttrMapValue(None, desc='Handle for the point sets.'),
        data = AttrMapValue(None, desc='Data to be plotted, list of lists of phi, psi tuples.')
    )

    def __init__(self):
        PlotArea.__init__(self)

        self.xAxis = XValueAxis()
        self.xAxis.gridStrokeColor = colors.black
        self.yAxis = YValueAxis()
        self.yAxis.gridStrokeColor = colors.black

        self.pointSets = TypedPropertyCollection(PointSetProperties) 
        self.pointSets[0].fillColor = colors.blue
        self.pointSets[1].fillColor = colors.red
        self.pointSets[2].fillColor = colors.green
        self.pointSets[3].fillColor = colors.purple

    def draw(self):
        xA, yA = self.xAxis, self.yAxis

        xA.setPosition(self.x, self.y, 360)
        yA.setPosition(self.x, self.y, 360)

        xA.joinAxis = yA
        yA.joinAxis = xA

        xA.valueMin = -180
        xA.valueMax =  180
        xA.valueStep = self.gridSpacing

        yA.valueMin = -180
        yA.valueMax =  180
        yA.valueStep = self.gridSpacing

        xA.configure(self.data)
        yA.configure(self.data)

        g = Group()
        g.add(self.makeBackground())

        # axis labels
        phiLabX, phiLabY = self.x + 180, self.y - 20
        psiLabX, psiLabY = self.x - 20, self.y + 180
        g.add(Label(x=phiLabX, y=phiLabY, _text=u"\u03A6\u00B0"))
        g.add(Label(x=psiLabX, y=psiLabY, _text=u"\u03A8\u00B0"))

        g.add(xA)
        g.add(yA)

        # internal axes
        g.add(Line(self.x + 180, self.y, self.x + 180, self.y + 360))
        g.add(Line(self.x, self.y + 180, self.x + 360, self.y + 180))

        if self.visibleGrid:

            xA.visibleGrid = True
            yA.visibleGrid = True

            xA.gridStart = yA._x
            yA.gridStart = xA._y

            xA.gridEnd = yA._x + yA._length
            yA.gridEnd = xA._y + xA._length

            xA.makeGrid(g, parent=self)
            yA.makeGrid(g, parent=self)

        # the points
        for i, pointSet in enumerate(self.data):
            c = self.pointSets[i].fillColor
            for p in pointSet:
                phi, psi = p
                phiAsX = phi + 180 + self.x
                psiAsY = psi + 180 + self.y
                g.add(Rect(phiAsX, psiAsY, 1, 1, strokeColor=c))
        return g

    def demo(self):
        """"Make a Ramachandran plot with 2 pointsets."""
        d = Drawing(400, 400)

        points = [
                    [ (-45, -45), (-30, -37), (-60, -20) ], # pointset 1
                    [ (-45, 145), (-30, 137), (-60, 120) ], # pointset 2
                 ]

        r = RamachandranPlot()
        
        r.x = 30
        r.y = 30
        r.data = points
        r.visibleGrid = False
        r.gridSpacing = 30
        r.axesColor = colors.red
        
        d.add(r)
        return d

if __name__ == "__main__":
    import sys

    filename = sys.argv[1]
    columnStart = int(sys.argv[2]) 
    columnEnd = int(sys.argv[3]) 
    
    if filename == "-":
        fp = sys.stdin
        filename = "stdin"
    else:
        fp = open(filename, 'r')

    numberOfPointSets = ((columnEnd - columnStart) + 1) / 2
    
    data = [[] for i in range(numberOfPointSets)]
    for line in fp.readlines()[1:]:
        angles = [float(a) for a in line.split("\t")[columnStart:columnEnd+1]]
        for i in range(numberOfPointSets):
            idx = i * 2
            try:
                data[i].append((angles[idx], angles[idx+1]))
            except IndexError, ide:
                print("index error in", line.rstrip("\n"))
    r = RamachandranPlot()
    r.x = 30
    r.y = 30
    r.visibleGrid = 0
    r.gridSpacing = 30
    r.data = data

    d = Drawing(400, 400)
    d.add(r)

    from reportlab.graphics import renderPDF
    renderPDF.drawToFile(d, "%s.pdf" % filename, filename)

    #from reportlab.graphics import renderPS
    #renderPS.drawToFile(d, "%s.ps" % filename, "Test")
