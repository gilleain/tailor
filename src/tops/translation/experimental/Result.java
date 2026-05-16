package tops.translation.experimental;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.vecmath.Point3d;

import tailor.condition.SegmentPartition;
import tops.translation.model.Segment;
import tops.translation.model.Chain;
import tops.translation.model.Group;

public class Result {
	
	private class CRef {
		public Chain chain;
		public List<RRef> residueRefs = new ArrayList<>();
		public List<SRef> segmentRefs = new ArrayList<>();
		public CRef(Chain chain) {
			this.chain = chain;
		}
	}
	
	private class SRef {
		public Segment segment;
		public List<RRef> residueRefs = new ArrayList<>();
		public SRef(Segment segment) {
			this.segment = segment;
		}
		public Segment getSegment() {
			return segment;
		}
	}
	
	private class RRef {
		public Group residue;
		public List<ARef> atomRefs = new ArrayList<>();
		public RRef(Group residue) {
			this.residue = residue;
		}
	}
	
	private class ARef {
		public String atomName;
		public Point3d point;
	}
	
	private CRef root;
	
	public Result(Chain chain) {
		this.root = new CRef(chain);
	}

	public Result(Chain chain, Group... residues) {
		this(chain);
		for (Group residue : residues) {
			RRef residueRef = new RRef(residue);
			this.root.residueRefs.add(residueRef);
			// TODO - atoms?
		}
	}
	
	public Result(Chain chain, Segment... segments) {
		this(chain);
		for (Segment segment : segments) {
			SRef segmentRef = new SRef(segment);
			this.root.segmentRefs.add(segmentRef);
			// TODO - residues and atoms?
		}
	}
	
	public List<Segment> getSegments() {
		List<Segment> segments = new ArrayList<>();
		for (SRef segmentRef : this.root.segmentRefs) {
			segments.add(segmentRef.segment);
		}
		return segments;
	}

	public SegmentPartition getSegmentPartition() {
		List<List<Segment>> parts = new ArrayList<>();
		parts.add(getSegments(root));
		return new SegmentPartition(parts);
	}
	
	private List<Segment> getSegments(CRef cRef) {
		return cRef.segmentRefs.stream().map(SRef::getSegment).toList();
	}
	

	public Result copy() {
		Result copy = new Result(this.root.chain);
		for (SRef sRef : this.root.segmentRefs) {
			copy.root.segmentRefs.add(new SRef(sRef.segment));
		}
		// TODO ?
		return copy;
	}
	
	public Result merge(Result other) {
		for (SRef otherS : other.root.segmentRefs) {
			SRef sCopy = new SRef(otherS.segment);
			this.root.segmentRefs.add(sCopy);
			// TODO
		}
		return this;
	}

	public boolean greaterThanOrEqual(Result other) {
		for (SRef sRef : this.root.segmentRefs) {
			for (SRef sRefOther : other.root.segmentRefs) {
				if (sRef.segment.getNumber() >= sRefOther.segment.getNumber()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String toString() {
		String segments = this.root.segmentRefs.stream().map(s -> s.segment.toCompactString()).collect(Collectors.joining("|"));
		return root.chain.getLabel() + "/" + segments;	// TODO
	}
}
