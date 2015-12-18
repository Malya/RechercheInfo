import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import query.Matcher;
import database.reader.Document;

public class Evaluator {

	private final static boolean VERBOSE = false;

	private Matcher matcher;
	private ArrayList<String> pertinentDocs;

	public Evaluator(Matcher matcher) {
		this.matcher = matcher;
		pertinentDocs = new ArrayList<>();
	}

	public void evaluate(String query, String qrelDoc, int nbResults) {

		System.out.println("Requêtes à évaluer: " + query);
		
		for(int v = 1; v <= 2; v++) {
			matcher.setVersion(v);
			List<Entry<Document, Float>> results = new ArrayList<Entry<Document, Float>>(matcher.match(query));
	
			getPertinentDocs(qrelDoc);
	
			Iterator<Entry<Document, Float>> it = results.iterator();
			Entry<Document,Float> entry;
			int i = 0;
			int nbPertinent = 0;
			while (it.hasNext() && i < nbResults) {
				i++;
				entry = it.next();
				String doc = entry.getKey().getPath();
				if (pertinentDocs.contains(doc)) {
					nbPertinent++;
				} else if (VERBOSE) {
					System.out.println("Document non pertinent: " + doc);
				}
			}
	
			float precision = (float) nbPertinent / i;
			float rappel = (float) nbPertinent / pertinentDocs.size();
			/*System.out
					.println("Nombre de documents évalués: " + i
							+ ", Totalité de documents pertinents: "
							+ pertinentDocs.size());*/
			System.out.println("VERSION: " + v + " -> P@" + nbResults + ": " + precision + ", R@" + nbResults + ": " + rappel);
		}

	}

	private void getPertinentDocs(String qrelDoc) {

		pertinentDocs.clear();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(qrelDoc));
			String line;
			String path;
			float pertinence;
			while ((line = reader.readLine()) != null) {
				path = line.split("\\s")[0];
				pertinence = Float.valueOf(line.split("\\s")[1].replace(",",
						"."));

				if (pertinence > 0) {
					pertinentDocs.add(path);
				}

			}
			reader.close();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
