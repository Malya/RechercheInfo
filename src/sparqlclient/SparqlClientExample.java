package sparqlclient;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class SparqlClientExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SparqlClient sparqlClient = new SparqlClient("localhost:3030/space");

        String upQuery = "ASK WHERE { ?s ?p ?o }";
        boolean serverIsUp = sparqlClient.ask(upQuery);
        if (serverIsUp) {
            System.out.println("server is UP");

            String query = "lieu naissance;omar sy";
            HashSet<Map<String, String>> newQuery = new HashSet<Map<String, String>>();
            for(String term : query.split(";")) {
            	String sparqlQuery = createSynonymQuery(term);
            	System.out.println("Query: " + sparqlQuery);
            	newQuery.addAll(sparqlClient.select(sparqlQuery));
            }

            for(Map<String, String> res : newQuery) {
            	for(Entry<String, String> entry : res.entrySet()) {
            		System.out.println(entry.getKey() + " : " + entry.getValue());
            	}
            }
            
            
        } else {
            System.out.println("service is DOWN");
        }
    }
    
    public static String createSynonymQuery(String term) {
    	return "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
        		"SELECT ?label WHERE { \n" +
        		 "?res rdfs:label ?labels. \n" +
        		 "FILTER (regex(?labels, '^" + term +"$')). \n" +
        		 "?res rdfs:label ?label. \n" +
    			"} \n"	+
    			"LIMIT 20";
    }
    
    private static void nbPersonnesParPiece(SparqlClient sparqlClient) {
        String query = "PREFIX : <http://www.lamaisondumeurtre.fr#>\n"
                    + "SELECT ?piece (COUNT(?personne) AS ?nbPers) WHERE\n"
                    + "{\n"
                    + "    ?personne :personneDansPiece ?piece.\n"
                    + "}\n"
                    + "GROUP BY ?piece\n";
            Iterable<Map<String, String>> results = sparqlClient.select(query);
            System.out.println("nombre de personnes par pièce:");
            for (Map<String, String> result : results) {
                System.out.println(result.get("piece") + " : " + result.get("nbPers"));
            }
    }    
}
