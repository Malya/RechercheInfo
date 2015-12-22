package database.item;

public class Bind<X extends Unique, Y extends Unique> implements Correlation<Item<X>, Item<Y>> {

	private Item<X> x;
	private Item<Y> y;
	private Correlation<X, Y> correlation;
	
	protected Bind(Item<X> x, Item<Y> y, Correlation<X, Y> correlation) {
		this.x = x;
		this.y = y;
		this.correlation = correlation;
	}
	
	public Correlation<X, Y> getCorrelation() {
		return this.correlation;
	}
	
	@Override
	public Item<X> getX() {
		return this.x;
	}
	
	@Override
	public Item<Y> getY() {
		return this.y;
	}
	
}
