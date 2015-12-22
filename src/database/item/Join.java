package database.item;

public class Join<X extends Unique, Y extends Unique> extends Item<X> implements Correlation<Item<X>, Item<Y>> {

	private Item<Y> y;
	
	protected Join(X x) {
		super(x);
		this.y = null;
	}

	public Item<X> getX() {
		return this;
	}
	
	public Item<Y> getY() {
		return y;
	}
	
	protected void attach(Item<Y> y) {
		this.y = y;
	}
	
}
