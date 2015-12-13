package database.reader;

import java.util.ArrayList;
import java.util.Collection;

import database.reader.item.Items;

public class Term {
	
	public static Items<Term> collection() {
		return new Items<Term>() {
			@Override
			protected Term item(String name) {
				return new Term(name);
			}
		};
	}
	
	private String word;
	private Collection<Link> binds;
	
	protected Term(String word) {
		this.word = word;
		this.binds = new ArrayList<Link>();
	}
	
	public String getWord() {
		return this.word;
	}
	
	protected void links(Document doc, int tf) {
		this.binds.add(new Link(doc, tf));
	}
	
	public Collection<Link> getBinds() {
		return this.binds;
	}
	
}
