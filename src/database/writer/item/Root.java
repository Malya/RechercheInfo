package database.writer.item;

import database.item.Unique;

public class Root implements Unique {

	private String root;
	
	protected Root(String root) {
		this.root = root;
	}
	
	public String getRoot() {
		return this.root;
	}
	
	public String getName() {
		return this.root;
	}
	
}
