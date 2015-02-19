import java.util.ArrayList;


public class CentralControl {
	
	// Configurable values
	public static final int SIZE = 2000;
	public static final int TOPFLOOR = 5;
	public static final int NUM_OF_ELEVATORS = 2;
	private static final int NUM_OF_RUNS = 1;
	private static final long SEED = 123456;

	// weights in pounds
	public static final int MIN_WEIGHT = 100;
	public static final int MAX_WEIGHT = 230;
	public static final int MAX_ELEVATOR_WEIGHT = 2400;

	public static double waitTime = 0;
	private static double totalWaitTime = 0;

	static class Person {
        int arrivalTime;
        int startFloor;
        int endFloor;
        int weight;
        boolean pickedUp;
        int elevator;

        public Person
        (final int time, final int start, final int end,
        		final int weight, final boolean pickedUp) {
            this.arrivalTime = time;
            this.startFloor = start;
            this.endFloor = end;
            this.weight = weight;
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
		public void increment(final int pos) {
			this.position = pos + 1;
		}
		public void decrement(final int pos) {
			this.position = pos -1;
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
	}

	public static void createQueues(final ArrayList<Person> sequence,
			final ArrayList<Person> upQueue,
			final ArrayList<Person> downQueue) {
		for (int j = 0; j < sequence.size(); j++) {
        	if (sequence.get(j).endFloor > sequence.get(j).startFloor) {
        		upQueue.add(sequence.get(j));
        	}
        	if (sequence.get(j).endFloor < sequence.get(j).startFloor) {
        		downQueue.add(sequence.get(j));
        	}
        }
	}

	public static void createBaselineElevators(
			final ArrayList<Elevator> e) {
		for (int i = 0; i < CentralControl.NUM_OF_ELEVATORS; i++) {
			// TODO: work on different configurations
			if (i % 2 == 0) {
				Elevator elevator =
						new Elevator(0, true, false, 0, "Baseline", 0);
				e.add(elevator);
			} else {
				Elevator elevator =
						new Elevator(TOPFLOOR, false, true, 0, "Baseline", 0);
		        e.add(elevator);
			}
		}
	}
	
	public static void simulation1 (String type) {
		
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
			sequenceGenerator.createNewSequence(sequence, SIZE, TOPFLOOR, i * SEED);
			sequenceGenerator.sortSequence(sequence);
//			sequenceGenerator.saveToTxt(sequence, fileName, "test");
	
	        // divides sequence array into; up/down queues
	        final ArrayList<Person> upQueue = new ArrayList<>();
	        final ArrayList<Person> downQueue = new ArrayList<>();
	        createQueues(sequence, upQueue, downQueue);
	        
			if (type  == "baseline") {
		        // create baseline elevator(s)
	 			final ArrayList<Elevator> e = new ArrayList<>();
	 			createBaselineElevators(e);
	 			// run baseline
		        BaselineElevator.runBaseline(e, upQueue, downQueue);
		    	} 
			else if (type == "") {
		        	
		        }
			
			totalWaitTime += waitTime;
			waitTime = 0;
			int percent = (int) Math.round((double) i /(double) n * 100);
			ProgressBar.updateBar(percent);
		}
		double atwt = totalWaitTime / (SIZE * n);
		System.out.println("awt: " + atwt * 15 / 60 + " min / person");
		
		System.out.println();
		System.out.println("simulation1 done!");
	}
	

	public static void main(final String[] args){
		
		simulation1("baseline");
		
	}
}
