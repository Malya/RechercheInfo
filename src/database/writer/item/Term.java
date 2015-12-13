package database.writer.item;




public class Term extends Item {
	
	private String word;
	private Integer idf;
	
	protected Term(String word) {
		this.word = word;
		this.idf = 0;
	}
	
	protected Term(int id, String word, int idf) {
		this.setId(id);
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

	protected String insert() {
		return "INSERT INTO TERMS (Id, Term, IDF) " + "VALUES ('" + this.getId() + "', '" + this.word + "', '" + this.idf + "');";
	}
	
	protected String update() {
		return "UPDATE TERMS set IDF='" + this.idf + "' where Id='" + this.getId() + "';";
	}
}
