package database.reader;

import database.item.Unique;


public class Document implements Unique {
	
	private Integer weight;
	
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

	@Override
	public String getName() {
		return this.path;
	}
	
}
