package database.writer.item;

import database.item.Unique;




public class Term implements Unique {
	
	private String word;
	private Integer gtf;
	
	protected Term(String word) {
		this.word = word;
		this.gtf = 0;
	}
	
	protected Term(String word, int gtf) {
		this.word = word;
		this.gtf = gtf;
	}
	
	public String getWord() {
		return this.word;
	}
	
	public int getGTF() {
		return this.gtf;
	}
	
	protected void links(int tf) {
		this.gtf += tf;
	}
	
	protected void unlinks(int tf) {
		this.gtf -= tf;
	}

	@Override
	public String getName() {
		return this.word;
	}


}
