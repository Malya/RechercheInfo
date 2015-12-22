package database.item;

public interface Correlation<X extends Unique, Y extends Unique> {

	public X getX();
	
	public Y getY();
	
}
