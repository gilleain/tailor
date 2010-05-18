package tailor.engine;

import java.util.ArrayList;

import tailor.description.ProteinDescription;
import tailor.measure.Measure;

public interface Engine {
	
	public void run(ProteinDescription description, ArrayList<Measure> measures);
	
	public void run(Run run);

}
