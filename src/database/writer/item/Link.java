package database.writer.item;

import database.item.Correlation;
import database.writer.item.Document;
import database.writer.item.Term;
import format.Tag;



public class Link implements Correlation<Term, Document> {
	
	private Term term;
	private Document doc;
	private Tag tag;
	private int pos;
	
	protected Link(Term term, Document doc, Tag tag, Integer pos) {
		this.term = term;
		this.doc = doc;
		this.tag = tag;
		this.pos = pos;
		this.term.links();
		this.doc.link();
	}
	
	public Tag getTag() {
		return this.tag;
	}
	
	public int getPos() {
		return this.pos;
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
