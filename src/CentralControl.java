import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class CentralControl {
	
	// Configurable values
	public static final int SIZE = 1000;
	public static final int TOPFLOOR = 25;
	public static final int NUM_OF_ELEVATORS = 2;
	private static final int NUM_OF_RUNS = 1;
	private static final long SEED = 12345;

	// weights in pounds
	public static final int MIN_WEIGHT = 100;
	public static final int MAX_WEIGHT = 230;
	public static final int MAX_ELEVATOR_WEIGHT = 2400;

	public static double waitTime = 0;
	private static double baselineTotalWaitTime = 0;
	private static double mainTotalWaitTime = 0;

	static class Person {
        int arrivalTime;
        int startFloor;
        int endFloor;
        int weight;
        int id;
        boolean pickedUp;
        int elevator;

        public Person
        (final int time, final int start, final int end,
        		final int weight, final int id,
        		final boolean pickedUp) {
            this.arrivalTime = time;
            this.startFloor = start;
            this.endFloor = end;
            this.weight = weight;
            this.id = id;
            this.pickedUp = pickedUp;
        }

		public void isPickedUp(final boolean pickUp) {
			this.pickedUp = pickUp;
		}
		public void setElevatorNr(final int nr) {
			this.elevator = nr;
		}
    }

	static class Elevator {
		int position;
		boolean up;
		boolean down;
		int currentElevatorWeight;
		String type;
		int people;
		int peoplePickedUp;
		int peopleDropedOff;
		String status;
		int internalClock;
		boolean[] pushedButtons = new boolean[TOPFLOOR];

		public Elevator
		(final int pos, final boolean up,
				final boolean down, final int weight,
				final String type, final int per) {
			this.position = pos;
			this.up = up;
			this.down = down;
			this.currentElevatorWeight = weight;
			this.type = type;
			this.people = per;
		}
		public Elevator
		(final int pos, final boolean up,
				final boolean down, final int weight,
				final String type, final int per, String stat, final int time, final boolean[] buttons) {
			this.position = pos;
			this.up = up;
			this.down = down;
			this.currentElevatorWeight = weight;
			this.type = type;
			this.people = per;
			this.status = stat;
			this.internalClock = time;
		}
		
		public void increment() {
			this.position += 1;
		}
		public void decrement() {
			this.position -= 1;
		}
		public void setDirUp(final boolean dir) {
			this.up = dir;
		}
		public void setDirDown(final boolean dir) {
			this.down = dir;
		}
		public void addPerson(Person per) {
			this.people += 1;
			this.currentElevatorWeight += per.weight;
			
			this.peoplePickedUp += 1;
		}
		public void removePerson(Person per) {
			this.people -= 1;
			this.currentElevatorWeight -= per.weight;
			
			this.peopleDropedOff += 1;
		}
		public void PickedUp() {
			this.peoplePickedUp += 1;
		}
		public void DropedOff() {
			this.peopleDropedOff += 1;
		}
		public void resetCount() {
			this.peoplePickedUp = 0;
			this.peopleDropedOff = 0;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void addTimeUnit() {
			this.internalClock += 1;
		}
		public void pushButton(int floor) {
			this.pushedButtons[floor] = true;
		}
		public void clearButton(int floor) {
			this.pushedButtons[floor] = false;
		}
	}

	public static void createQueues(final ArrayList<Person> sequence,
			final ArrayList<Person> upQueue,
			final ArrayList<Person> downQueue) {
		for (int j = 0; j < sequence.size(); j++) {
        	if (sequence.get(j).endFloor > sequence.get(j).startFloor) {
        		upQueue.add(sequence.get(j));
        	}
        	else if (sequence.get(j).endFloor < sequence.get(j).startFloor) {
        		downQueue.add(sequence.get(j));
        	}
        	else {
        		sequence.remove(j);
        	}
        }
	}

	public static void createBaselineElevators(
			final ArrayList<Elevator> e) {
		for (int i = 0; i < CentralControl.NUM_OF_ELEVATORS; i++) {
			// TODO: work on different configurations
			if (i % 2 == 0) {
				Elevator elevator =
						new Elevator(1, true, false, 0, "Baseline", 0);
				e.add(elevator);
			} else {
				Elevator elevator =
						new Elevator(TOPFLOOR, false, true, 0, "Baseline", 0);
		        e.add(elevator);
			}
		}
	}
	
	public static void createMainElevators(
			final ArrayList<Elevator> e) {
		boolean[] buttons = new boolean[TOPFLOOR];

		for (int i = 0; i < CentralControl.NUM_OF_ELEVATORS; i++) {
			// TODO: work on different configurations
			if (i % 2 == 0) {
				Elevator elevator =
						new Elevator(1, true, false, 0, "Main", 0, "idle", 0, buttons);
				e.add(elevator);
			} else {
				Elevator elevator =
						new Elevator(TOPFLOOR, false, true, 0, "Main", 0, "idle", 0, buttons);
		        e.add(elevator);
			}
		}
	}
	
	public static void simulation1 (String type) throws FileNotFoundException, UnsupportedEncodingException {
		
		@SuppressWarnings("unused")
		final ProgressBar bar = ProgressBar.createBar();
		
		System.out.println("Starting simulation1:");
		System.out.println();
		
		int n = NUM_OF_RUNS;
		
		//TODO: print to text file instead of console
		
		System.out.println("Nr of floors: " + TOPFLOOR);
		System.out.println("Nr of people: " + SIZE);
		System.out.println("Nr of elevators: " + NUM_OF_ELEVATORS);
		System.out.println("Nr of runs: " + NUM_OF_RUNS);
		System.out.println("Seed: " + SEED);
		System.out.println("Type: " + type);
		System.out.println();
		
		for (int i = 1; i <= n; i++) {
			// create new sequence
			final ArrayList<Person> sequence = new ArrayList<>();
			sequenceGenerator.createNewBaselineSequence(sequence, SIZE, TOPFLOOR, i * SEED);
			sequenceGenerator.sortSequence(sequence);
			final ArrayList<Person> mainSequence =
					new ArrayList<>(sequenceGenerator.convertToMainSequene(sequence));
			
			sequenceGenerator.saveToTxt(sequence, "baseline_sequence", "output");
			sequenceGenerator.saveToTxt(mainSequence, "main_sequence", "output");
	
	        
			if (type  == "baseline") {
				// divides sequence array into; up/down queues
		        final ArrayList<Person> upQueue = new ArrayList<>();
		        final ArrayList<Person> downQueue = new ArrayList<>();
		        createQueues(sequence, upQueue, downQueue);
		        // create baseline elevator(s)
	 			final ArrayList<Elevator> e = new ArrayList<>();
	 			createBaselineElevators(e);
	 			// run baseline
		        BaselineElevator.runBaseline(e, upQueue, downQueue);
		        
		        baselineTotalWaitTime += waitTime;
				waitTime = 0;
				
				double atwt = baselineTotalWaitTime / (SIZE * n);
				System.out.println("awt: " + atwt * 15 / 60 + " min / person");
		    	} 
			else if (type == "main") {
				// divides sequence array into; up/down queues
				final ArrayList<Elevator> e = new ArrayList<>();
				createMainElevators(e);
				// run elevator
				MainElevator.runElevator(e, mainSequence);
				
				mainTotalWaitTime += waitTime;
				waitTime = 0;
				
				double atwt = mainTotalWaitTime / (SIZE * n);
				System.out.println("awt: " + atwt * 5 / 60 + " min / person");
		        }
			
		int percent = (int) Math.round((double) i /(double) n * 100);
		ProgressBar.updateBar(percent);
		}
		
		
		System.out.println();
		System.out.println("simulation1 done!");
	}
	

	public static void main(final String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		
		simulation1("baseline");
		simulation1("main");
		
	}
}
