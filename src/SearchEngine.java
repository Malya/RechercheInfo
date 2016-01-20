import index.Indexator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import version.Version;


public class SearchEngine {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
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
				Evaluator evaluator = new Evaluator("personnes ; Intouchables", "QRELS/qrelQ1.txt");
				evaluator.evaluate(0, 5, Version.values());
				evaluator.evaluate(5, 10, Version.values());
				evaluator.evaluate(10, 25, Version.values());
				
				evaluator = new Evaluator("lieu naissance ; omar sy", "QRELS/qrelQ2.txt");
				evaluator.evaluate(0, 5, Version.values());
				evaluator.evaluate(5, 10, Version.values());
				evaluator.evaluate(10, 25, Version.values());
				
				evaluator = new Evaluator("acteurs ; joue avec ; omar sy", "QRELS/qrelQ9.txt");
				evaluator.evaluate(0, 5, Version.values());
				evaluator.evaluate(5, 10, Version.values());
				evaluator.evaluate(10, 25, Version.values());
			}
		}
	}

}
