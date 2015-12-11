import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import database.Database;


public class Evaluator {
	
	private final static int VERBOSE = 0;
	
	private Matcher matcher;
	private Database db;
	private ArrayList<String> pertinentDocs;
	
	public Evaluator(Matcher matcher) {
		this.matcher = matcher;
		this.db = matcher.getDatabase();
		pertinentDocs = new ArrayList<>();
	}
	
	public void evaluate(String query, String qrelDoc, int nbResults) {
		
		System.out.println("Requêtes à évaluer: " + query);
		
		List<Entry<Integer, Integer>> results = matcher.match(query);
		
		getPertinentDocs(qrelDoc);
		
		Iterator<Entry<Integer, Integer>> it = results.iterator();
		Entry<Integer, Integer> entry;
		int i = 0;
		int nbPertinent = 0;
		while(it.hasNext() && i < nbResults) {
			i++;
			entry = it.next();
			if(pertinentDocs.contains(db.getDocPath(entry.getKey()))) {
				nbPertinent++;
			} else if(VERBOSE == 1) {
				System.out.println("Document non pertinent: " + db.getDocPath(entry.getKey()));
			}
		}
		
		
		float precision = (float) nbPertinent / i;
		float rappel = (float) nbPertinent / pertinentDocs.size();
		System.out.println("Nombre de documents évalués: " + i + ", Totalité de documents pertinents: " + pertinentDocs.size());
		System.out.println("Précision: " + precision + ", Rappel: " + rappel);
		
	}
	
	private void getPertinentDocs(String qrelDoc) {
		
		pertinentDocs.clear();
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(qrelDoc));
            String line;
            String path;
            float pertinence;
            while ((line = reader.readLine()) != null)
            {
            	path = line.split("\\s")[0];
            	pertinence = Float.valueOf(line.split("\\s")[1].replace(",", "."));
            	
            	if(pertinence > 0) {
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
