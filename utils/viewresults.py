# an attempt at a jython program!

from tops.view.tops2D.app import LinearViewPanel
from javax.swing import JFrame, JPanel, JScrollPane, ScrollPaneConstants
from java.awt import Dimension, GridLayout
import sys

class Record(object):
    def __init__(self, compression, oldstring, newstring):
        self.compression = compression
        self.oldstring = oldstring
        self.newstring = newstring
    def __cmp__(self, other):
        return cmp(self.compression, other.compression)

records = []
for line in open(sys.argv[1]):
    if "pattern" not in line:
        continue

    compression = float(line[0:line.index('\t')])
    stringpart = line[line.index("for") + 3:].strip()

    oldstring, newstring = stringpart.split(" <=> ")
    record = Record(compression, oldstring, newstring)
    records.append(record)

numberOfStringPairs = len(records)

frame = JFrame("test")
frame.setSize(600, 400)

panel = JPanel(GridLayout(numberOfStringPairs, 2))
size = Dimension(600, 200 * numberOfStringPairs)

panel.setSize(size)
panel.setPreferredSize(size)

records.sort()
for record in records:
    try:
        oldname, oldVertices, oldEdges = record.oldstring.split(' ')
        newname, newVertices, newEdges = record.newstring.split(' ')

        oldPanel = LinearViewPanel(300, 200)
        oldPanel.renderVertices(oldVertices)
        oldPanel.renderEdges(oldEdges)
        oldPanel.setToolTipText(oldname)
        panel.add(oldPanel)

        newPanel = LinearViewPanel(300, 200)
        newPanel.renderVertices(newVertices)
        newPanel.renderEdges(newEdges)
        newPanel.setToolTipText(newname)
        panel.add(newPanel)
    except ValueError:
        pass
        #print oldstring, newstring

scrollPane = JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)
scrollPane.setSize(800,400)
frame.getContentPane().add(scrollPane)
frame.setVisible(1)
