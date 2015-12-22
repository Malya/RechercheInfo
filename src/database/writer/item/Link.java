package database.writer.item;

import database.item.Correlation;
import database.writer.item.Document;
import database.writer.item.Term;



public class Link implements Correlation<Term, Document> {
	
	private Term term;
	private Document doc;
	private Integer tf;
	
	protected Link(Term term, Document doc, int tf) {
		this.term = term;
		this.doc = doc;
		this.tf = tf;
		this.term.links(this.tf);
		this.doc.link(tf);
	}
	
	public int getTF() {
		return this.tf;
	}

	@Override
	public Term getX() {
		return this.term;
	}

	@Override
	public Document getY() {
		return this.doc;
	}
	
}
