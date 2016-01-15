import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import query.Matcher;
import version.Version;
import database.reader.Document;

public class Evaluator {

	private final static boolean VERBOSE = false;

	private final static NumberFormat decimal = new DecimalFormat("#0.00"); 
	
	private String query;
	private Map<String, Float> pertinents;
	private Map<Version, List<Entry<Document, Double>>> cache;
	
	public Evaluator(String query, String qrelDoc) throws IOException {
		this.query = query;
		this.pertinents = new HashMap<String, Float>();
		BufferedReader reader = new BufferedReader(new FileReader(qrelDoc));
		String line;
		while ((line = reader.readLine()) != null) {
			String path = line.split("\\s")[0];
			float pertinence = Float.valueOf(line.split("\\s")[1].replace(",", "."));
			if (pertinence > 0) {
				this.pertinents.put(path, pertinence);
			}
		}
		reader.close();
		this.cache = new HashMap<Version, List<Entry<Document, Double>>>();
	}

	private static String degres(float value) {
		if (value < 0.25) {
			return "--";
		} else if (value < 0.5) {
			return "-+";
		} else if (value < 0.75) {
			return "+-";
		} else return "++";
	}
	
	public void evaluate(int nbResultsStart, int nbResultsEnd, Version...versions) {
		System.out.println("Requetes a evaluer: " + query);
		
		for(Version v : versions) {
			List<Entry<Document, Double>> results = this.cache.get(v);
			if (results == null) {
				Matcher matcher = v.matcher;
				results = new ArrayList<Entry<Document, Double>>(matcher.match(query));
				this.cache.put(v, results);
			}
			
			
			Entry<Integer, Double> entry = getNbPertinentInRaking(results, nbResultsStart, nbResultsEnd);
			// Pour le score
			double rank = entry.getValue();
			// Pour la précision
			double nbPertinentRaking = entry.getKey();
			// Pour le rappel global
			//double nbPertinentTotal = getNbPertinentInRaking(results, 0, results.size());
	
			double precision = nbPertinentRaking / (nbResultsEnd-nbResultsStart);
			double rappelRaking = nbPertinentRaking / this.pertinents.size();
			//double rappelGlobal = nbPertinentTotal / this.pertinents.size();
			
			System.out.println(v + " -> S@" + nbResultsEnd + ": " + decimal.format(rank) + ", P@" + nbResultsEnd + ": " + decimal.format(precision) + ", R@" + nbResultsEnd + ": " + decimal.format(rappelRaking)); // +", Global R:" + rappelGlobal);
		}

	}

	private Entry<Integer, Double> getNbPertinentInRaking(List<Entry<Document, Double>> results, int nbResultsStart, int nbResultsEnd) {
		Iterator<Entry<Document, Double>> it = results.iterator();
		Entry<Document,Double> entry;
		int i = nbResultsStart;
		int nbPertinent = 0;
		double rank = 0.0;
		double perfect = 0.0;
		while (it.hasNext() && i < nbResultsEnd) {
			entry = it.next();
			String doc = entry.getKey().getPath();
			Float pertinence = this.pertinents.get(doc);
			if (pertinence == null) {
				pertinence = 0.0f;
			}
			rank += (nbResultsEnd - i) * pertinence;
			perfect += nbResultsEnd - i;
			if (pertinence > 0.0f) {
				nbPertinent += 1;
			}
			if (VERBOSE) {
				System.out.println(degres(pertinence) + doc);
			}
			i += 1;
		}
		return new SimpleEntry<Integer, Double>(nbPertinent, rank/perfect);
	}

}
