package database.writer.item;

import database.item.Unique;




public class Term implements Unique {
	
	private String word;
	private Integer idf;
	
	protected Term(String word) {
		this.word = word;
		this.idf = 0;
	}
	
	protected Term(String word, int idf) {
		this.word = word;
		this.idf = idf;
	}
	
	public String getWord() {
		return this.word;
	}
	
	public int getIDF() {
		return this.idf;
	}
	
	protected void links(int tf) {
		this.idf += tf;
	}
	
	protected void unlinks(int tf) {
		this.idf -= tf;
	}

	@Override
	public String getName() {
		return this.word;
	}


}
