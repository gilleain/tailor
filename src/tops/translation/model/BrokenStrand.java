package tops.translation.model;

import java.util.Iterator;
import java.util.SortedSet;

import javax.vecmath.Vector3d;

public class BrokenStrand extends BackboneSegment {
	
    private SortedSet<BackboneSegment> strands;	 // XXX - why not use parent set?

    public void addStrand(BackboneSegment strand) {
        this.strands.add(strand);
    }

    public boolean containsContinuousStrands() {
        // if there is only a small gap between succesive strands
        return true;
    }

    // if the angle between the average axis and each of the strands is low
    // then they are probably all pointing in the same direction
    public boolean pointingInSameDirection() {
        // the trivial case! perhaps this should print a 'bug report' kind of error?
        if (this.strands.size() == 1) {
            return true;
        }
        // find the average vector
        BackboneSegment firstStrand = (BackboneSegment) this.strands.first();
        Vector3d averageVector = new Vector3d(firstStrand.getAxis().getAxisVector());
        Iterator<BackboneSegment> iterator = this.strands.iterator();
        while (iterator.hasNext()) {
            BackboneSegment strand = (BackboneSegment) iterator.next();
            averageVector.add(strand.getAxis().getAxisVector());
        }

        // check that the angle to this average is low ... whatever 'low' means
        iterator = this.strands.iterator();
        while (iterator.hasNext()) {
            BackboneSegment strand = (BackboneSegment) iterator.next();
            double angle = strand.getAxis().angle(averageVector);
            if (angle > 90.0) {
                return false;
            }
        }
        return true;
    }

    public String toFullString() {
        StringBuffer s = new StringBuffer();
        Iterator<BackboneSegment> strandIterator = this.strands.iterator();
        while (strandIterator.hasNext()) {
            BackboneSegment strand = (BackboneSegment) strandIterator.next();
            s.append(strand.toFullString()).append(", ");
        }
        return s.toString();
    }

    public char getTypeChar() {
        return 'E';
    }
}
