package tailor.category.filter;

public class SquareRegion implements PairFilter {
	
	private final Filter filterX;	// filter on one axis
	private final Filter filterY;	// filter on other axis
	
	public SquareRegion(Filter filterX, Filter filterY) {
		this.filterX = filterX;
		this.filterY = filterY;
	}

	@Override
	public boolean accept(double firstValue, double secondValue) {
		return filterX.accept(firstValue) && filterY.accept(secondValue);
	}

}
