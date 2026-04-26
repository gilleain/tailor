package tailor.engine.operator;

import tailor.api.Operator;
import tailor.api.ResultsPrinter;
import tailor.description.ChainDescription;

public class ResultsPrinterAdapter implements Operator {
	
	private ResultsPrinter resultsPrinter;
	
	private ResultPipe source;
	
	private ChainDescription chainDescription;
	
	public ResultsPrinterAdapter(ResultsPrinter resultsPrinter, ResultPipe source, ChainDescription chainDescription) {
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
	public String description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}
