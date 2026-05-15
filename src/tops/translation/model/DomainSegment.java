package tops.translation.model;

import java.util.ArrayList;
import java.util.List;


public class DomainSegment {
    private int start;
    private int end;

    public DomainSegment(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(Segment backboneSegment) {
        //return backboneSegment.containedInPDBNumberRange(this.start, this.end);
        return backboneSegment.overlapsPDBNumberRange(this.start, this.end);
    }

    public List<Segment> filter(List<Segment> backboneSegments) {
        List<Segment> subList = new ArrayList<>();

        for (Segment backboneSegment : backboneSegments) {
            if (this.contains(backboneSegment)) {
                subList.add(backboneSegment);
            }
        }
        return subList;
    }

    public String toString() {
        return this.start + ":" + this.end;
    }

}
