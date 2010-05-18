package tailor.msdmotif;


public class MSDMotifGuiResultsPrinter implements MSDMotifResultsPrinter {
	
	private MSDMotifResultTableModel model;
	
	public MSDMotifGuiResultsPrinter(MSDMotifResultTableModel model) {
		this.model = model;
	}

	public void printResult(MSDMotifResult result) {
		this.model.addResult(result);
	}

}
