package database.reader;

import java.util.HashMap;
import java.util.Map;

import database.item.Unique;
import format.Tag;

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
	private Map<Document, Link> binds;
	
	protected Term(String word) {
		this.word = word;
		this.binds = new HashMap<Document, Link>();
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
	
	protected void links(Document doc, Tag tag, int pos) {
		Link link = this.binds.get(doc);
		if (link == null) {
			link = new Link();
			this.binds.put(doc, link);
		}
		link.addTag(tag);
		link.addPos(pos);
	}
	
	public Map<Document, Link> getBinds() {
		return this.binds;
	}

	@Override
	public String getName() {
		return this.word;
	}
	
}
