package tops.translation.model;

import static tops.translation.model.Segment.Orientation.DOWN;
import static tops.translation.model.Segment.Orientation.NONE;
import static tops.translation.model.Segment.Orientation.UP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import tops.translation.model.Segment.Orientation;
import translation.Axis;
import translation.Geometer;

public class Sheet {

    private int number;

    // The strand map has as the keys all the strands in the map;
    // the values are the other strands in the sheet that the key is attached to.
    // In theory, a nice pure sheet would only have one value per key. In theory...
    private TreeMap<Segment, List<Segment>> strandMap;
    
    private Axis axis;

    public Sheet(int number) {
        this.number = number;
        this.strandMap = new TreeMap<>();
        this.axis = null;
    }

    public Sheet(int number, Segment first, Segment second) {
        this(number);
        this.addPair(first, second);
    }

    public void addPair(Segment first, Segment second) {
        if (first.compareTo(second) < 0) {
            this.map(first, second);
        } else {
            this.map(second, first);
        }
    }

    public void map(Segment keyStrand, Segment partner) {
        List<Segment> values;
        if (this.strandMap.containsKey(keyStrand)) {
            values = this.strandMap.get(keyStrand);
        } else {
            values = new ArrayList<>();
            this.strandMap.put(keyStrand, values);
        }
        values.add(partner);
    }

    public List<Segment> getPartners(Segment key) {
        return this.strandMap.get(key);
    }

    public Iterator<Segment> getPartnerIterator(Segment key) {
        return this.getPartners(key).iterator();
    }

    public int getNumber() {
        return this.number;
    }

    public void setAxis(Axis axis) {
        this.axis = axis;
    }

    public void setAxis(Vector3d axisVector) {
        this.setAxis(new Axis(this.calculateCentroid(), axisVector));
    }

    public Axis getAxis() {
        return this.axis;
    }

    public Point3d calculateCentroid() {
        List<Point3d> centers = new ArrayList<>();
        for (Segment strand : this.strandMap.keySet()) {
            centers.add(strand.getAxis().getCentroid());
        }
        return Geometer.averagePoints(centers);
    }

    public int size() {
        int size = 0;
        for (Segment strand : this.strandMap.keySet()) {
            size++;
            size += this.strandMap.get(strand).size();
        }
        return size;
    }
    
    public void extend(Sheet other) {
        Iterator<Segment> keyIterator = other.iterator();
        while (keyIterator.hasNext()) {
            Segment key = keyIterator.next();
            List<Segment> otherValues = other.getPartners(key);

            if (this.strandMap.containsKey(key)) {
                List<Segment> thisValues = this.getPartners(key);
                thisValues.addAll(otherValues);
            } else {
                this.strandMap.put(key, otherValues);
            }
        }
    }

    public Iterator<Segment> iterator() {
        return this.strandMap.keySet().iterator();
    }

    public Iterator<Segment> chainOrderIterator() {
        List<Segment> chainOrder = new ArrayList<>();
        chainOrder.addAll(this.strandMap.keySet());
        Iterator<Segment> iterator = this.iterator();
        while (iterator.hasNext()) {
            List<Segment> partners = this.strandMap.get(iterator.next());
            for (int i = 0; i < partners.size(); i++) {
                Segment partner = partners.get(i);
                if (!chainOrder.contains(partner)) {
                    chainOrder.add(partner);
                }
            }
        }
        Collections.sort(chainOrder);
        return chainOrder.iterator();
    }

    public boolean contains(Segment strand) {
        if (this.strandMap.containsKey(strand)) {
            return true;
        } else {
            Iterator<Segment> keyIterator = this.iterator();
            while (keyIterator.hasNext()) {
                if (this.strandMap.get(keyIterator.next()).contains(strand)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<List<Segment>> getSheetPaths() {
        List<List<Segment>> paths = new ArrayList<>();
        Iterator<Segment> iterator = this.iterator();
        while (iterator.hasNext()) {
            Segment key = (Segment) iterator.next();
            paths.add(this.traverseSheetPath(key, new ArrayList<>()));
        }
        return paths;
    }

    public List<Segment> traverseSheetPath(Segment currentStrand, List<Segment> path) {
        List<Segment> partners = this.strandMap.get(currentStrand);
        for (int i = 0; i < partners.size(); i++) {
            Segment partner = partners.get(i);
            path.add(partner);
            if (this.strandMap.containsKey(partner)) {
                this.traverseSheetPath(partner, path);            
            } else {
                return path;
            }
        } 
        return path;
    }

    public void assignOrientationsToStrands() {

        // while we're at it, we might as well calculate the sheet axis
        Vector3d sheetVector = new Vector3d();

        // reset the iterator
        Iterator<Segment> iterator = this.iterator();
        while (iterator.hasNext()) {
            Segment strand = iterator.next();
            if (strand.getOrientation() == Orientation.NONE) {
                strand.setOrientation(Orientation.UP);
            }

            Iterator<Segment> partnerIterator = this.getPartnerIterator(strand);
            while (partnerIterator.hasNext()) {
                Segment partner = partnerIterator.next();
                this.assignOrientations(strand, partner);

                // add to the average vector, or subtract if DOWN
                if (partner.getOrientation() == Orientation.UP) {
                    sheetVector.add(partner.getAxis().getAxisVector());
                } else {
                    sheetVector.sub(partner.getAxis().getAxisVector());
                }
                Logger.getLogger("translation.FoldAnalyser").info("orientation " + strand + " -> " + partner);
            }
        }

        sheetVector.normalize();
        this.setAxis(sheetVector);
    }

    private void assignOrientations(Segment strand, Segment partner) {
        char relativeOrientation = strand.getRelativeOrientation(partner);
        if (relativeOrientation == 'P') {
            Orientation strandOrientation = strand.getOrientation();
            if (strandOrientation == NONE) {
                Orientation partnerOrientation = partner.getOrientation();
                if (partnerOrientation == NONE) {
                    Logger.getLogger("translation.FoldAnalyser").info("No orientation known for " + strand + " and " + partner);
                } else {
                    Logger.getLogger("translation.FoldAnalyser").info("Assigning orientation : " + partnerOrientation + " to " + strand);
                    strand.setOrientation(partnerOrientation);
                }
            } else {
                Logger.getLogger("translation.FoldAnalyser").info("Assigning orientation : " + strandOrientation + " to " + partner);
                partner.setOrientation(strandOrientation);
            }
        } else {
        	Orientation strandOrientation = strand.getOrientation();
            if (strandOrientation == NONE) {
            	Orientation partnerOrientation = partner.getOrientation();
                if (partnerOrientation == NONE) {
                    Logger.getLogger("translation.FoldAnalyser").info("No orientation known for " + strand + " and " + partner);
                } else {
                    Logger.getLogger("translation.FoldAnalyser").info("Assigning orientation : " + partnerOrientation + " to " + strand);
                    strand.setOrientation(partnerOrientation);
                }
            } else if (strandOrientation == UP) {
                Logger.getLogger("translation.FoldAnalyser").info("Assigning orientation : UP to " + partner);
                partner.setOrientation(DOWN);
            } else if (strandOrientation== DOWN) {
                Logger.getLogger("translation.FoldAnalyser").info("Assigning orientation : DOWN to " + partner);
                partner.setOrientation(UP);
            }
        }
    }

    public List<Edge> toTopsEdges(Domain domain) {
        List<Edge> edges = new ArrayList<>();

        for (Segment strand : this.strandMap.keySet()) {
            if (!domain.contains(strand)) {
                continue;
            }

            Iterator<Segment> partnerIterator = this.getPartnerIterator(strand);
            while (partnerIterator.hasNext()) {
                Segment partner = (Segment) partnerIterator.next();
                if (!domain.contains(partner)) {
                    continue;
                }

                char relativeOrientation = strand.getRelativeOrientation(partner); 
                Edge edge;
                if (strand.compareTo(partner) < 0) {
                    edge = new Edge(strand, partner, relativeOrientation);
                } else {
                    edge = new Edge(partner, strand, relativeOrientation);
                }
                //System.err.println("Made edge : " + edge);

                // since the mapping is symmetric, we have to discard half the edges we make!
                if (!edges.contains(edge)) {
                    edges.add(edge);
                }
            }
        } 

        return edges;
    }

    @Override
    public String toString() {
        StringBuffer returnValue = new StringBuffer();
        returnValue.append("Sheet (" + this.number + ") [");

        Iterator<Segment> iterator = this.strandMap.keySet().iterator();

        while (iterator.hasNext()) {
            Segment strand = iterator.next();
            returnValue.append(strand);

            Iterator<Segment> partnerIterator = this.getPartnerIterator(strand);
            while (partnerIterator.hasNext()) {
                Segment partner = partnerIterator.next();
                returnValue.append(" -> ").append(partner); 
            }
            returnValue.append("\n");
        }
        returnValue.append("]");
        return returnValue.toString();
    }

}
