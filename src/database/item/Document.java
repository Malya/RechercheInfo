package database.item;

public class Document {
	
	private Integer id;
	private String path;
	
	protected Document(int id, String path) {
		this.id = id;
		this.path = path;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getPath() {
		return this.path;
	}

	public String insert() {
		return "INSERT INTO DOCUMENTS (Id, Path) " + "VALUES ('" + this.id + "', '" + this.path + "');";
	}

	public String unlinks() {
		return "DELETE FROM LINKS WHERE DocID='" + this.id + "';";
	}
	
}
