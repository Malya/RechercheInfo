package database.reader;

import database.reader.item.Items;

public class Document {
	
	public static Items<Document> collection() {
		return new Items<Document>() {
			@Override
			protected Document item(String name) {
				return new Document(name);
			}
		};
	}
	
	private String path;
	
	protected Document(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public String toString() {
		return this.path;
	}
}
