package database.item;

public class Item<U extends Unique> implements Unique {

	private Integer id;
	private U unique;
	
	protected Item(U unique) {
		this.id = null;
		this.unique = unique;
	}
	
	protected void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public U getUnique() {
		return this.unique;
	}

	@Override
	public String getName() {
		return this.unique.getName();
	}
	
}
