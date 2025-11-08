package aigen.datasource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StructureGenerator implements Iterable<Structure> {
    private List<AngleRange> angleRanges;
    private double stepSize;
    private int numberOfModels;
    private List<Double> angles;
    private int changingAngleIndex;
    private int sweepIndex;
    
    public StructureGenerator(List<AngleRange> angleRanges, double stepSize) {
        this.angleRanges = angleRanges;
        this.stepSize = stepSize;
        
        this.numberOfModels = 1;
        for (AngleRange angleRange : angleRanges) {
            this.numberOfModels *= Math.abs(angleRange.stop - angleRange.start) / stepSize;
        }
        
        this.angles = new ArrayList<>();
        for (AngleRange angleRange : angleRanges) {
            this.angles.add(angleRange.start);
        }
        
        this.changingAngleIndex = 0;
        this.sweepIndex = 1;
    }
    
    public void updateIndices() {
        double currentlyChangingAngle = this.angles.get(this.changingAngleIndex);
        double nextAngleValue = this.angleRanges.get(this.changingAngleIndex).start + this.stepSize;
        
        if (currentlyChangingAngle > nextAngleValue) {
            this.changingAngleIndex += 1;
        } else {
            this.sweepIndex += 1;
        }
    }
    
    @Override
    public Iterator<Structure> iterator() {
        return new Iterator<Structure>() {
            private int currentModel = 0;
            
            @Override
            public boolean hasNext() {
                return currentModel < numberOfModels;
            }
            
            @Override
            public Structure next() {
                List<AngleRange> anglePairs = new ArrayList<>();
                for (int i = 0; i < angles.size(); i += 2) {
                    anglePairs.add(new AngleRange(angles.get(i), angles.get(i + 1)));
                }
                
                Structure structure = Generate.makeFragment(anglePairs);
                updateIndices();
                currentModel++;
                
                return structure;
            }
        };
    }
}
