package database.item;

public class Term {
	
	private Integer id;
	private String word;
	private Integer idf;
	
	protected Term(int id, String word) {
		this.id = id;
		this.word = word;
		this.idf = 0;
	}
	
	protected Term(int id, String word, int idf) {
		this.id = id;
		this.word = word;
		this.idf = idf;
	}
	
	public int getId() {
		return this.id;
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

	public String insert() {
		return "INSERT INTO TERMS (Id, Term, IDF) " + "VALUES ('" + this.id + "', '" + this.word + "', '" + this.idf + "');";
	}
	
	public String update() {
		return "UPDATE TERMS set IDF='" + this.idf + "' where Id='" + this.id + "';";
	}
}
