import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import query.Matcher;
import version.Version;
import database.reader.Document;

public class Evaluator {

	private final static boolean VERBOSE = false;

	private final static NumberFormat decimal = new DecimalFormat("#0.00"); 
	
	private Map<String, Float> pertinents;
	
	private String queries[] = {"personnes;Intouchables", "lieu naissance; omar sy", "personnes; r�compens�es; Intouchables",
			"palmar�s; Globes de Cristal; 2012", "membre;jury;Globes de Cristal;2012", "prix; omar sy; Globes de Cristal; 2012",
			"lieu; Globes Cristal; 2012", "prix; omar sy", "acteurs; jou� avec; omar sy"};
	
	
	public Evaluator() {
		this.pertinents = new HashMap<String, Float>();
	}
	
	public void getPertinents(String qrelDoc) throws IOException {
		pertinents.clear();
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
	
	public void evaluateAll() throws IOException {
		
		for(Version v : Version.values()) {
			float precision5 = 0, precision10 = 0, precision25 = 0;
			int nbQueries = 0;
			for(String query : queries) {
				nbQueries++;
				//System.out.println(query + " ; " + "QRELS/qrelQ" + nbQueries + ".txt");
				getPertinents("QRELS/qrelQ" + nbQueries + ".txt");
				precision5 += evaluate(query, 5, v);
				precision10 += evaluate(query, 10, v);
				precision25 += evaluate(query, 25, v);
			}
			System.out.println("Version �tudi� : " + v.getDescription());
			System.out.println("P5: " + decimal.format(precision5/nbQueries));
			System.out.println("P10: " + decimal.format(precision10/nbQueries));
			System.out.println("P25: " + decimal.format(precision25/nbQueries));
		}
		System.out.println("");

	}
	
	public void evaluateOne(String query, String qrelDoc, int nbResultsEnd, Version...versions) throws IOException {
		System.out.println("Requete a evaluer: " + query);
		
		for(Version v : versions) {
			getPertinents(qrelDoc);
			evaluate(query, nbResultsEnd, v);
		}
		System.out.println("");

	}
	
	public float evaluate(String query, int nbResultsEnd, Version v) {
		Matcher matcher = v.matcher;
		List<Entry<Document, Double>> results = new ArrayList<Entry<Document, Double>>(matcher.match(query));

		Entry<Integer, Double> entry = getNbPertinentInRaking(results, nbResultsEnd);
		// Pour le score
		//double rank = entry.getValue();
		// Pour la précision
		double nbPertinentRaking = entry.getKey();
		// Pour le rappel global
		//double nbPertinentTotal = getNbPertinentInRaking(results, 0, results.size());

		double precision = nbPertinentRaking / nbResultsEnd;
		//double rappelRaking = nbPertinentRaking / this.pertinents.size();
		//double rappelGlobal = nbPertinentTotal / this.pertinents.size();
		
		//System.out.println(v + " -> S@" + nbResultsEnd + ": " + decimal.format(rank) + 
		//		", P@" + nbResultsEnd + ": " + decimal.format(precision) +
		//		", R@" + nbResultsEnd + ": " + decimal.format(rappelRaking)); // +", Global R:" + rappelGlobal);
		
		return (float) precision;
	}

	private Entry<Integer, Double> getNbPertinentInRaking(List<Entry<Document, Double>> results, int nbResultsEnd) {
		Iterator<Entry<Document, Double>> it = results.iterator();
		Entry<Document,Double> entry;
		int i = 0;
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
