import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainElevator {
	
	public static void runElevator(final ArrayList<CentralControl.Elevator> e,
			final ArrayList<CentralControl.Person> sequence) throws FileNotFoundException, UnsupportedEncodingException {
		
		
		File f = new File("output" + File.separator + "main_console" + ".txt");
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		
		System.out.println("Starting..");
		
		int day = 5760;
        // loop simulates a day. [1 unit = 5 sec]
		for (int time = 0; time <= day;) {
			
			
			elevatorLoop: for (int i = 0; i < e.size(); i++) {
				time = e.get(i).internalClock;
				
				if (e.get(i).status.equals("idle")) {
					
					int nextCall = getNextCall(sequence, e.get(i).internalClock);
					
					if (nextCall == -1) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(false);

						e.get(i).addTimeUnit();
						continue elevatorLoop;
					}
					else if (e.get(i).position <= nextCall) {
						e.get(i).setDirUp(true);
						e.get(i).setDirDown(false);
						e.get(i).setStatus("moving");
					}
					else if (e.get(i).position > nextCall) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(true);
						e.get(i).setStatus("moving");
					}
				}
				
				// elevator responding to call
				if (e.get(i).up || e.get(i).down) {
					
					sequenceLoop: for (int j = 0; j < sequence.size(); j++) {
						
						// open doors
						if (sequence.get(j).pickedUp == false
								&& e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT
								&& e.get(i).internalClock >= sequence.get(j).arrivalTime
								&& e.get(i).position == sequence.get(j).startFloor
								&& e.get(i).status.equals("moving")) {
							
							e.get(i).setStatus("doors open");
							e.get(i).addTimeUnit();
							continue elevatorLoop;
						}
						// pick up people
						if (sequence.get(j).pickedUp == false
								&& e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT
								&& e.get(i).internalClock >= sequence.get(j).arrivalTime
								&& e.get(i).position == sequence.get(j).startFloor
								&& e.get(i).status.equals("doors open")) {
							
							//add passenger to elevator
							e.get(i).addPerson(sequence.get(j));
							// assigns the elevator # to the passenger
							sequence.get(j).setElevatorNr(i);
							sequence.get(j).isPickedUp(true);
							
							if (sequence.get(j).startFloor < sequence.get(j).endFloor) {
								e.get(i).setDirUp(true);
								e.get(i).setDirDown(false);
							} else if (sequence.get(j).startFloor > sequence.get(j).endFloor) {
								e.get(i).setDirUp(false);
								e.get(i).setDirDown(true);
							}
							
							continue sequenceLoop;
						}
						// drop of people
						if ((sequence.get(j).elevator == i) && (sequence.get(j).pickedUp == true)
							&& (e.get(i).position == sequence.get(j).endFloor)) {
								
							CentralControl.waitTime += e.get(i).internalClock - sequence.get(j).arrivalTime;
							// remove person from elevator
							e.get(i).removePerson(sequence.get(j));
							// remove person from queue
							sequence.remove(j);
							continue sequenceLoop;
						}
						
						// close doors
						if (j == sequence.size()-1
							&& e.get(i).status.equals("doors open")) {
							
							e.get(i).setStatus("doors closed");
							e.get(i).addTimeUnit();
							continue elevatorLoop;
						}
					}
					
					if (e.get(i).people == 0 && getNextCall(sequence, e.get(i).internalClock) == -1) {
						e.get(i).setStatus("idle");
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(false);
						continue elevatorLoop;
					}
				
					if (e.get(i).position == CentralControl.TOPFLOOR) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(true);
					} else if (e.get(i).position == 1) {
						e.get(i).setDirUp(true);
						e.get(i).setDirDown(false);
					}
					
					// moves elevator one step
					if (e.get(i).down) {
						e.get(i).decrement();
						e.get(i).setStatus("moving");
						e.get(i).addTimeUnit();
						continue elevatorLoop;
					} 
					else if (e.get(i).up) {
						e.get(i).increment();
						e.get(i).setStatus("moving");
						e.get(i).addTimeUnit();
						continue elevatorLoop;
					}
				}
			}
		printToFile(e, writer);
//		printToConsole(e);
			
		}
		writer.close();
		for (int i = 0; i < sequence.size(); i++) {
			System.out.println(sequence.get(i).id);
		}
 	}

	public static void printToConsole(
			final ArrayList<CentralControl.Elevator> e) {
		for (int i = 0; i < e.size(); i++) {
			System.out.printf("%d \t\t", e.get(i).internalClock);
			System.out.printf("%d \t", e.get(i).position);
			System.out.printf(" %d \t %d \t", e.get(i).currentElevatorWeight, e.get(i).people);
			System.out.printf(" %d \t %d \t\t", e.get(i).peoplePickedUp, e.get(i).peopleDropedOff);
			e.get(i).resetCount();
		}
		System.out.println();
	}

	public static void printToFile(final ArrayList<CentralControl.Elevator> e,
			PrintWriter writer) {
		// checks if filename already exists
		if (!new File("output").exists()) {
			new File("output").mkdir();
		}

				for (int i = 0; i < e.size(); i++) {
					writer.printf("%d \t\t", e.get(i).internalClock);
					writer.printf("%d \t", e.get(i).position);
					writer.printf(" %d \t %d \t", e.get(i).currentElevatorWeight, e.get(i).people);
					writer.printf(" %d \t %d \t\t", e.get(i).peoplePickedUp, e.get(i).peopleDropedOff);
					e.get(i).resetCount();
				}
				writer.println();
	}

	public static int getNextCall(
			final ArrayList<CentralControl.Person> sequence, double time) {
		
		for (int i = 0; i < sequence.size(); i++) {
			if (time >= sequence.get(i).arrivalTime) {
				return sequence.get(i).startFloor;
			}
		}
		if (sequence.size() == 0){
			return -2;
		}

		return -1;
	}
	
	public static void main (String[] args){
		
	}
}
