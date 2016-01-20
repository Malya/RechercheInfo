package version;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import query.Matcher;
import query.matcher.Basic;
import query.matcher.Jaccard;
import query.matcher.Normalized;
import query.matcher.Semantic;
import database.reader.Link;
import database.reader.Term;

public enum Version {

	V1("01.01.16", "TF", new Basic() {
		@Override
		protected double match(Term term, Link link) {
			return link.getTF();
		}
	}),
	V2("01.01.16", "Similarity Cosinus with TF", new Normalized() {
		@Override
		protected double match(Term term, Link link) {
			return link.getTF();
		}
	}),
	V3("01.01.16", "TF-IDF", new Basic() {
		@Override
		protected double match(Term term, Link link) {
			return link.getTF() * term.getIDF();
		}
	}),
	V4("01.01.16", "TF-IDF weighted by Document length", new Basic() {
		@Override
		protected double match(Term term, Link link) {
			return link.getTF() * term.getIDF() / link.getDoc().getWeight();
		}
	}),
	V5("01.01.16", "Jaccard distance with TF", new Jaccard()),
	V6("01.01.16", "Similarity Cosinus with TF-IDF", new Normalized() {
		@Override
		protected double match(Term term, Link link) {
			return link.getTF() * term.getIDF();
		}
	}),
	V7("01.01.16", "Similarity Cosinus with TF-IDF weighted by Document length", new Normalized() {
		@Override
		protected double match(Term term, Link link) {
			return link.getTF() * term.getIDF() / link.getDoc().getWeight();
		}
	}),
	V8("15.01.16", "TF-GDF", new Basic() {
		@Override
		protected double match(Term term, Link link) {
			return link.getTF() * term.getGDF();
		}
	}),
	V9(9, "17.01.16", "Semantic", new Semantic(new Basic() {
		@Override
		protected double match(Term term, Link link) {
			return link.getTF() * term.getGDF();
		}})
	);
	
	private Date date;
	private final String description;
	public final Matcher matcher;
	
	Version(String date, String description, Matcher matcher) {
		try {
			this.date = new SimpleDateFormat("dd.MM.yy").parse(date);
		} catch (ParseException e) {}
		this.description = description;
		this.matcher = matcher;
	}
	
	public String info() {
		return this.toString() + "(" + new SimpleDateFormat("dd.MM.yy").format(this.date) + ")::" + this.description; 
	}
	
}
