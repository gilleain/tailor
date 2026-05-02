package tailor.editor;

import tailor.view.symbol.Symbol;


public class SymbolSelectionEvent {
	
	private Symbol symbol;
	
	public SymbolSelectionEvent(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public Symbol getSymbol() {
		return this.symbol;
	}

}
