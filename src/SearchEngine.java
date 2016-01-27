import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import index.Indexator;
import version.Version;


public class SearchEngine {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		boolean log = false;
		boolean print = false;
		long time = 0;
		int index = 0;
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
			} else if (opt.equals("-eAll")) {
				Evaluator evaluator = new Evaluator();
				evaluator.evaluateAll();
			} else if (opt.equals("-e")) {
				if(args.length < index + 3) {
					System.out.println("Usage: -e REQUETE QREL_PATH");
				} else {
					Evaluator evaluator = new Evaluator();
					evaluator.evaluateOne(args[index+1], args[index+2], 5, Version.V11);
					evaluator.evaluateOne(args[index+1], args[index+2], 10, Version.V11);
					evaluator.evaluateOne(args[index+1], args[index+2], 25, Version.V11);
				}
			}
			index++;
		}
	}

}
