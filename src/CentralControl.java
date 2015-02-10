import java.io.IOException;
import java.util.ArrayList;


public class CentralControl {

	// Configurable values
	public static final int SIZE = 2000;
	public static final int TOPFLOOR = 5;
	public static final int NUM_OF_ELEVATORS = 2;
	private static final String FILENAME =
			"S" + SIZE + "F" + TOPFLOOR + "E" + NUM_OF_ELEVATORS;

	// weights in pounds
	public static final int MIN_WEIGHT = 100;
	public static final int MAX_WEIGHT = 230;
	public static final int MAX_ELEVATOR_WEIGHT = 2400;

	public static double waitTime = 0;
	public static double totalWaitTime = 0;

	static class Person {
        int arrivalTime;
        int startFloor;
        int endFloor;
        int weight;
        boolean pickedUp;

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
    }

	static class Elevator {
		int position;
		boolean up;
		boolean down;
		int currentElevatorWeight;

		public Elevator
		(final int pos, final boolean up,
				final boolean down, final int weight) {
			this.position = pos;
			this.up = up;
			this.down = down;
			this.currentElevatorWeight = weight;
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
		public void addWeight(final int weight) {
			this.currentElevatorWeight += weight;
		}
		public void removeWeight(final int weight) {
			this.currentElevatorWeight -= weight;
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
			final ArrayList<CentralControl.Elevator> e) {
		for (int i = 0; i < CentralControl.NUM_OF_ELEVATORS; i++) {
			// TODO: work on different configurations
			if (i % 2 == 0) {
				CentralControl.Elevator elevator =
						new CentralControl.Elevator(
								0, true, false, 0);
				e.add(elevator);
			} else {
				CentralControl.Elevator elevator =
						new CentralControl.Elevator(
								CentralControl.TOPFLOOR, false, true, 0);
		        e.add(elevator);
			}
		}
	}

	public static void main(final String[] args) throws IOException {

		int n = 10;
		
		System.out.println("Nr of floors: " + CentralControl.TOPFLOOR);
		System.out.println("Nr of people: " + SIZE);
		System.out.println("Nr of elevators: " + NUM_OF_ELEVATORS);
		System.out.println();
		
		for (int i = 0; i < n; i++) {
			// create sequence
			final ArrayList<Person> sequence = new ArrayList<>();
			sequenceGenerator.createNewSequence(sequence);
			sequenceGenerator.sortSequence(sequence);
			sequenceGenerator.saveToTxt(sequence, FILENAME);
	
			// create baseline elevator(s)
			final ArrayList<Elevator> e = new ArrayList<>();
			createBaselineElevators(e);
	
	        // divides sequence array into; up/down queues
	        final ArrayList<Person> upQueue = new ArrayList<>();
	        final ArrayList<Person> downQueue = new ArrayList<>();
	        createQueues(sequence, upQueue, downQueue);
	
	        BaselineElevator.runBaseline(e, upQueue, downQueue);
	
	        double awt = waitTime / sequence.size();
			System.out.println("avarage waiting time: " + awt * 15 / 60 + " min / person");
			System.out.println();
			totalWaitTime += waitTime;
			waitTime = 0;
		}
		double atwt = totalWaitTime / (SIZE * n);
		System.out.println("avarage waiting time: " + atwt * 15 / 60 + " min / person");
	}
}
