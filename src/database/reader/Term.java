package database.reader;

import java.util.ArrayList;
import java.util.Collection;

import database.item.Unique;

public class Term implements Unique {
	
	private static int documents;
	private static int terms;
	
	protected static void setDocuments(int docs) {
		documents = docs;
	}
	
	protected static void setTerms(int ts) {
		terms = ts;
	}
	
	private String word;
	private Integer gtf;
	private Collection<Link> binds;
	
	protected Term(String word) {
		this.word = word;
		this.binds = new ArrayList<Link>();
	}
	
	public String getWord() {
		return this.word;
	}
	
	protected void setGTF(int gtf) {
		this.gtf = gtf;
	}
	
	public double getIDF() {
		return Math.log10((double) documents/this.binds.size());
	}
	
	public double getGDF() {
		return Math.log10((double) terms/this.gtf);
	}
	
	protected void links(Document doc, int tf) {
		this.binds.add(new Link(doc, tf));
	}
	
	public Collection<Link> getBinds() {
		return this.binds;
	}

	@Override
	public String getName() {
		return this.word;
	}
	
}
