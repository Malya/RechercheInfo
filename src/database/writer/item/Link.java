package database.writer.item;

import database.writer.item.Document;
import database.writer.item.Term;



public class Link {
	
	private Term term;
	private Document doc;
	private Integer tf;
	
	protected Link(Term term, Document doc, int tf) {
		this.term = term;
		this.doc = doc;
		this.tf = tf;
		this.term.links(this.tf);
	}
	
	protected String insert() {
		return "INSERT INTO LINKS (TermId, DocID, TF) " + "VALUES ('" + this.term.getId() + "', '" + this.doc.getId() + "', '" + this.tf + "');";
	}
	
}
