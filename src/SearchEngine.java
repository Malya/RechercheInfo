import index.Indexator;

import java.io.File;
import java.util.Arrays;

import query.Matcher;


public class SearchEngine {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		boolean log = false;
		boolean print = false;
		long time = 0;
		for (String opt : args) {
			if (opt.equals("-l")) {
				log = true;
			} else if (opt.equals("-p")) { 
				print = true;
			} else if (opt.equals("-i")) {
				if (log) {
					time = -System.currentTimeMillis();
					System.out.println("Start process");
				}
				File input = new File("CORPUS");
				Indexator indexator = new Indexator();
				indexator.index(Arrays.asList(input.listFiles()));
				if (log) {
					time += System.currentTimeMillis();
					System.out.println("Time : " + time + "ms");
				}
				if (print) {
					indexator.export();
				}
			} else if (opt.equals("-e")) {
				Evaluator evaluator = new Evaluator(new Matcher());
				evaluator.evaluate("personnes Intouchables", "QRELS/qrelQ1.txt", 5);
				evaluator.evaluate("personnes Intouchables", "QRELS/qrelQ1.txt", 10);
				evaluator.evaluate("personnes Intouchables", "QRELS/qrelQ1.txt", 25);
				
				evaluator.evaluate("lieu naissance omar sy", "QRELS/qrelQ2.txt", 5);
				evaluator.evaluate("lieu naissance omar sy", "QRELS/qrelQ2.txt", 10);
				evaluator.evaluate("lieu naissance omar sy", "QRELS/qrelQ2.txt", 25);
				
				evaluator.evaluate("acteurs joue avec omar sy", "QRELS/qrelQ9.txt", 5);
				evaluator.evaluate("acteurs joue avec omar sy", "QRELS/qrelQ9.txt", 10);
				evaluator.evaluate("acteurs joue avec omar sy", "QRELS/qrelQ9.txt", 25);
			}
		}
	}

}