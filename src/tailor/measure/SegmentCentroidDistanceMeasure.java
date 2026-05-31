package tailor.measure;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import tailor.api.Measurement;
import tailor.api.SegmentListMeasure;
import tailor.description.ChainDescription;
import tailor.description.segment.SegmentDescription;
import tailor.description.segment.SegmentDescriptionPath;
import tailor.geometry.Axis;
import tailor.geometry.Geometer;
import tailor.measurement.DoubleMeasurement;
import tailor.partition.LabelPartition;
import tailor.partition.SegmentMatcher;
import tailor.partition.SegmentPartition;
import tailor.partition.LabelPartition.LabelledPart;
import tailor.partition.LabelPartition.Part;
import tailor.partition.SegmentMatcher.Match;
import tailor.structure.Segment;

public class SegmentCentroidDistanceMeasure implements SegmentListMeasure {
	
	private final SegmentDescriptionPath pathA;
	private final SegmentDescriptionPath pathB;
	private final SegmentMatcher segmentMatcher;

	public SegmentCentroidDistanceMeasure(SegmentDescriptionPath pathA, SegmentDescriptionPath pathB) {
		this.pathA = pathA;
		this.pathB = pathB;
		this.segmentMatcher = new SegmentMatcher(fromDescriptionPaths(pathA, pathB));
	}
	
	private LabelPartition fromDescriptionPaths(SegmentDescriptionPath... descriptionPaths) {
		Map<ChainDescription, List<String>> partition = new LinkedHashMap<>();
		for (SegmentDescriptionPath descriptionPath : descriptionPaths) {
			ChainDescription chainDescription = descriptionPath.getChainDescription();
			List<String> part = partition.computeIfAbsent(chainDescription, $ -> new ArrayList<>());
			part.add(descriptionPath.getSegmentDescription().getType().getTypeString());
		}
		
		List<Part> parts = new ArrayList<>();
		for (ChainDescription chainDescription : partition.keySet()) {
			parts.add(new LabelledPart(chainDescription.getIndex(), partition.get(chainDescription)));
		}

		return new LabelPartition(parts);
	}

	@Override
	public Measurement<Double> measure(SegmentPartition segmentPartition) {
		Optional<Match> match = segmentMatcher.containedIn(segmentPartition);
		if (match.isPresent()) {
			return measure(match.get().getSegments());
		} else {
			return new DoubleMeasurement();
		}
	}

	private Measurement<Double> measure(List<Segment> segments) {
		Segment segmentA = segments.get(0);
		Segment segmentB = segments.get(1);
		Axis aA = segmentA.getAxis();
		Axis aB = segmentB.getAxis();
		
		double d = Geometer.distance(aA.getCentroid(), aB.getCentroid());
		System.err.println("Distance : (" + segmentA.toCompactString() + ", " + segmentB.toCompactString() + ") = " + d);
		return new DoubleMeasurement( d );
	}

	@Override
	public List<SegmentDescription> getSegmentDescriptions() {
		return List.of(pathA.getSegmentDescription(), pathB.getSegmentDescription());
	}

	@Override
	public String getName() {
		return "Segment centroid distance";
	}

}
