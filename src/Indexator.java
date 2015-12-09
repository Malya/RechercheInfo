import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;


public class Indexator {
	
	private HashMap<String, Integer> index = new HashMap<>();
	
	public void indexAllFiles(File folder) {
	    for (File fileEntry : folder.listFiles()) {
	    	index.clear();
	    	
	        indexOneFile(fileEntry);
	        
	        String fileName = fileEntry.getName();
			for(String value : index.keySet()) {
				// db.links(value, fileName, index.get(value));
			}
	        
	    }
	}
	
	/*
	 * Create the index of words associated with his hit number in one document
	 * Input : File input = one document 
	 */
	public void indexOneFile(File input) {
		Document doc;
		
		try {
			doc = Jsoup.parse(input, "UTF-8");
			
			// Get all the words of the document (removing the words with less than two characters) 
			String result = getTextNodes(doc.children());
			String fWord;
			Integer occurence;
			for(String word : result.split("[\\s\\p{Punct}]+")) {
				fWord = cleanWord(word);
				
				if(fWord.length() > 1) {
					if(index.containsKey(fWord)) { // Word already present in the index
						occurence = index.get(fWord);
						index.put(fWord, occurence +1);
						
					} else {
						index.put(fWord, 1);
					}
				}
			}
			
			// Remove the stop words
			removeStopWords();
			
			for(String value : index.keySet()) {
				System.out.println(value);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Clean a word :
	 * 		- remove all numbers / accents / non visible characters
	 * 		- set to lower case
	 * 		- truncate the word to 7 characters
	 *  
	 * Input : String word = The word to clean
	 */
	public String cleanWord(String word) {
		word = word.replaceAll("\\p{Digit}", "");
		word = word.toLowerCase();
		word = Normalizer.normalize(word, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
		word = word.replaceAll("\\P{Graph}", "");
		
		if(word.length() > 7) {
			word = word.substring(0, 7);
		}

		return word;
	}
	
	public void removeStopWords() {
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("stopliste.txt"));
            String line;
            while ((line = reader.readLine()) != null)
            {
                if(line.length() > 1) {
                	index.remove(line);
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
	
	public String getTextNodes(Elements elems) {
		String result = "";
		for (Element elem : elems) {
			for(TextNode node : elem.textNodes())
			  result += " " + node.text();
			
			result += getTextNodes(elem.children());
		}
		return result;
	}

	public static void main(String[] args) {
		File input = new File("CORPUS");
		Indexator indexator = new Indexator();
		indexator.indexAllFiles(input);
				
	}
	


}
