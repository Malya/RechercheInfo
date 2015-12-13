package database.reader;


public class Link {

	private Document doc;
	private Integer tf;
	
	protected Link(Document doc, int tf) {
		this.doc = doc;
		this.tf = tf;
	}
	
	public Document getDoc() {
		return this.doc;
	}
	
	public int getTF() {
		return this.tf;
	}
	
}
