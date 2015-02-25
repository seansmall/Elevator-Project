import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;

public class sequenceGenerator {
	
	private static final Random RNG =
            new Random (Long.getLong ("seed", System.nanoTime()));
	
	
	static int hours = 8;
	static int day = 240 * hours; //60 sec * 60 min * 8 / 15 = 1920
	static int morning = 240 * 1;
	static int night = 240 * 1;
	
	
	public static ArrayList<CentralControl.Person> createNewBaselineSequence
	(ArrayList<CentralControl.Person> sequence, int size, int topFloor, long seed) {
		RNG.setSeed(seed);
		
		//TODO; convert to real time
		for (int i = 0; i < size; i++) {
			int at;
			if (i < size*0.4) {
				at = 1 + RNG.nextInt(morning);
			} else if (i >= size*0.4 && i <= size*0.6) {
				at = 1 + RNG.nextInt(day);
			} else {
				at = 1 + day/10*6 + RNG.nextInt(night);
			}
			
			int sf = 1 + RNG.nextInt(topFloor);
			int ef = 1 + RNG.nextInt(topFloor);
			if (sf == ef) {
				sf = 1 + RNG.nextInt(topFloor);
				ef = 1 + RNG.nextInt(topFloor);
			}
			
			int w = CentralControl.MIN_WEIGHT + RNG.nextInt(CentralControl.MAX_WEIGHT - CentralControl.MIN_WEIGHT);
			CentralControl.Person person = new CentralControl.Person(at, sf, ef, w, false);
            sequence.add(person);
		}
		return sequence;
	}
	
	public static ArrayList<CentralControl.Person> sortSequence(ArrayList<CentralControl.Person> sequence) {
		
		// sorts the sequence
	      for (int i = 0; i < sequence.size(); i++) {
	            int min = i;
	            for (int j = i + 1; j < sequence.size(); j++) {
	                if (sequence.get(j).arrivalTime < sequence.get(min).arrivalTime) {
	                    min = j;
	                }
	            }
	            if (min != i) {
	            	final CentralControl.Person temp = sequence.get(i);
	                sequence.set(i, sequence.get(min));
	                sequence.set(min, temp);
	            }
	        }
		return sequence;
	}
	public static ArrayList<CentralControl.Person> convertToMainSequene(ArrayList<CentralControl.Person> sequence) {
			
		final ArrayList<CentralControl.Person> newSequence = new ArrayList<>();
		
		for (int i = 0; i < sequence.size(); i++) {
			int at = sequence.get(i).arrivalTime * 3;
			int sf = sequence.get(i).startFloor;
			int ef = sequence.get(i).endFloor;
			int w = sequence.get(i).weight;
		
			CentralControl.Person person = new CentralControl.Person(at, sf, ef, w, false);
	        newSequence.add(person);
		}
			return newSequence;
		}
	
	
	public static void saveToTxt(
			ArrayList<CentralControl.Person> sequence,
			String fileName, String dirName) throws FileNotFoundException,
			UnsupportedEncodingException {
		
		// checks if filename already exists
		if (!new File(dirName).exists()) {
			new File(dirName).mkdir();
		}
		  
	      File f = new File(dirName + File.separator + fileName + ".txt");

	      if(!f.exists() && !f.isDirectory()) {
	    	// saves file to directory
		      PrintWriter writer = new PrintWriter(f, "UTF-8");
				writer.println("ArrivalTime,StartFloor,EndFloor,Weight");
				for (int i = 0; i < sequence.size(); i++) {
					int at = sequence.get(i).arrivalTime;
					int sf = sequence.get(i).startFloor;
					int ef = sequence.get(i).endFloor;
					int w = sequence.get(i).weight;
					writer.println(at + "," + sf + "," + ef + "," + w + ",");
				}
				writer.close();
				// prints to console
				System.out.println("Filename: " + f);
	      }
	}
	
	public static void main (String[] args) throws FileNotFoundException, UnsupportedEncodingException {

	}
}
