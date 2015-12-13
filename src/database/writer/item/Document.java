package database.writer.item;





public class Document extends Item {
	
	private String path;
	
	protected Document(String path) {
		this.path = path;
	}
	
	protected Document(int id, String path) {
		this.setId(id);
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}

	protected String insert() {
		return "INSERT INTO DOCUMENTS (Id, Path) " + "VALUES ('" + this.getId() + "', '" + this.path + "');";
	}

	@Override
	protected String update() {
		return "";
	}
	
}
