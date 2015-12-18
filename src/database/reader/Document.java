package database.reader;


public class Document {
	
	private Integer weight;
	
	public static Documents collection() {
		return new Documents();
	}
	
	private String path;
	
	protected Document(String path) {
		this.path = path;
		this.weight = null;
	}
	
	public String getPath() {
		return this.path;
	}
	
	protected void setWeight(int weight) {
		this.weight = weight;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	public String toString() {
		return this.path;
	}
	
}
