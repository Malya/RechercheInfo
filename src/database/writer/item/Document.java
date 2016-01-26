package database.writer.item;

import database.item.Unique;

public class Document implements Unique {

	private String path;
	private Integer weight;
	
	protected Document(String path) {
		this.path = path;
		this.weight = 0;
	}
	
	protected Document(String path, int weight) {
		this.path = path;
		this.weight = weight;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public int getWeight() {
		return this.weight;
	}
	
	protected void link() {
		this.weight += 1;
	}
	
	@Override
	public String getName() {
		return this.path;
	}

}