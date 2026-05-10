package tailor.engine.operator;

import tailor.api.Operator;
import tailor.api.ResultsPrinter;
import tailor.description.ChainDescription;

public class ResultsPrinterAdapter implements Operator {
	
	private ResultsPrinter resultsPrinter;
	
	private Pipe source;
	
	private ChainDescription chainDescription;
	
	private String id;
	
	public ResultsPrinterAdapter(ResultsPrinter resultsPrinter, Pipe source, ChainDescription chainDescription) {
		super();
		this.resultsPrinter = resultsPrinter;
		this.source = source;
		this.chainDescription = chainDescription;
	}

	@Override
	public void run() {
		resultsPrinter.printHeader(chainDescription.getAtomListMeasures());
		while (source.hasNext()) {
			resultsPrinter.printResult(source.getNext());
		}
	}

	@Override
	public Pipe getInput() {
		return this.source;
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void clear() {
		// No action
	}

	@Override
	public void setOutput(Pipe output) {
		// No action
	}

	@Override
	public void setInput(Pipe input) {
		// No action
	}

	@Override
	public Pipe getOutput() {
		// No action
		return null;
	}

}
