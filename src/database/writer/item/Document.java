package database.writer.item;





public class Document extends Item {
	
	private String path;
	private Integer weight;
	
	protected Document(String path) {
		this.path = path;
		this.weight = 0;
	}
	
	protected Document(int id, String path, int weight) {
		this.setId(id);
		this.path = path;
		this.weight = weight;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	protected void link(int weight) {
		this.weight += weight;
	}

	protected String insert() {
		return "INSERT INTO DOCUMENTS (Id, Path, Weight) " + "VALUES ('" + this.getId() + "', '" + this.path + "', '" + this.weight + "');";
	}

	@Override
	protected String update() {
		return "UPDATE DOCUMENTS set Weight='" + this.weight + "' where Id='" + this.getId() + "';";
	}
	
}
