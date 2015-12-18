package database.reader;

import java.util.ArrayList;
import java.util.Collection;

public class Term {
	
	private String word;
	private Integer idf;
	private Collection<Link> binds;
	
	protected Term(String word) {
		this.word = word;
		this.binds = new ArrayList<Link>();
	}
	
	public String getWord() {
		return this.word;
	}
	
	protected void setIDF(int idf) {
		this.idf = idf;
	}
	
	public int getIDF() {
		return this.idf;
	}
	
	protected void links(Document doc, int tf) {
		this.binds.add(new Link(doc, tf));
	}
	
	public Collection<Link> getBinds() {
		return this.binds;
	}
	
}
