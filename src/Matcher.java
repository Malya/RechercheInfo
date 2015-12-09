import java.util.ArrayList;


public class Matcher {
	
	private ArrayList<String> queryWords; 
	private Database db;
	
	public Matcher(String query) {
		queryWords = new ArrayList<>();
		Indexator index = new Indexator();
		//db = index.getDatabase();
		
		for(String word : query.split("[\\s\\p{Punct}]+")) {
			word = index.cleanWord(word);
			if(word.length() > 1) {
				this.queryWords.add(word);
			}
		}
		
		for(String word : queryWords) {
			
		}
	}
	
	

}
