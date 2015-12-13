package database.writer.item;

public abstract class Item {

	private Integer id;
	
	protected Item() {
		this.id = null;
	}
	
	protected void setId(int id) {
		this.id = id;
	}
	
	protected int getId() {
		return this.id;
	}
	
	protected abstract String insert();
	
	protected abstract String update();
	
}
