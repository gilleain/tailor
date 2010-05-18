package tailor.editor;

import tailor.editor.symbol.Symbol;


public class SymbolSelectionEvent {
	
	private Symbol symbol;
	
	public SymbolSelectionEvent(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public Symbol getSymbol() {
		return this.symbol;
	}

}
