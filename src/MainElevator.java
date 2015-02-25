import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainElevator {
	
	public static void runElevator(final ArrayList<CentralControl.Elevator> e,
			final ArrayList<CentralControl.Person> upQueue,
			final ArrayList<CentralControl.Person> downQueue,
			final ArrayList<CentralControl.Person> sequence) throws FileNotFoundException, UnsupportedEncodingException {
		
		
		File f = new File("output" + File.separator + "main_console" + ".txt");
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		
		System.out.println("Starting..");
		
		int day = 5760;
        // loop simulates a day. [1 unit = 5 sec]
		for (int time = 0; time <= day;) {
			
			elevatorLoop: for (int i = 0; i < e.size(); i++) {
				
				
				if (e.get(i).status.equals("idle")) {
					int nextCall = getNextCall(sequence, time);
					
					if (nextCall == -1) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(false);
						e.get(i).addTimeUnit();
						++time;
						continue elevatorLoop;
					}
					else if (e.get(i).position < nextCall) {
						e.get(i).setDirUp(true);
						e.get(i).setDirDown(false);
						e.get(i).setStatus("going up");
					}
					else if (e.get(i).position > nextCall) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(true);
						e.get(i).setStatus("going down");
					}
				}
				
				// elevator responding to up call
				if (e.get(i).up) {
					
//					System.out.println("elevator" + i + " responding to up call");
					
					sequenceLoop: for (int j = 0; j < upQueue.size(); j++) {
						
						// open doors
						if (upQueue.get(j).pickedUp == false
								&& e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT
								&& time >= upQueue.get(j).arrivalTime
								&& e.get(i).position == upQueue.get(j).startFloor
								&& e.get(i).status.equals("going up")) {
							
							e.get(i).setStatus("doors open");
							e.get(i).addTimeUnit();
							time++;
							System.out.println(e.get(i).status);
							continue elevatorLoop;
						}
						// pick up people
						if (upQueue.get(j).pickedUp == false
								&& e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT
								&& time >= upQueue.get(j).arrivalTime
								&& e.get(i).position == upQueue.get(j).startFloor
								&& e.get(i).status.equals("doors open")) {
							
							//add passenger to elevator
							e.get(i).addPerson(upQueue.get(j));
							// assigns the elevator # to the passenger
							upQueue.get(j).setElevatorNr(i);
							upQueue.get(j).isPickedUp(true);
							for (int k = 0; k < sequence.size(); k++) {
								if (sequence.get(k).equals(upQueue.get(j))) {
									sequence.remove(k);
								}
							}
							System.out.println("person added");
							continue sequenceLoop;
						}
						// drop of people
						if ((upQueue.get(j).elevator == i) && (upQueue.get(j).pickedUp == true)
							&& (e.get(i).position == upQueue.get(j).endFloor)) {
								
							CentralControl.waitTime += time - upQueue.get(j).arrivalTime;
							// remove person from elevator
							e.get(i).removePerson(upQueue.get(j));
							// remove person from queue
							upQueue.remove(j);
							System.out.println("person dropped");
							continue sequenceLoop;
						}
						
						// close doors
						if (j == upQueue.size()-1
							&& e.get(i).status.equals("doors open")) {
							
							e.get(i).setStatus("doors closed");
							e.get(i).addTimeUnit();
							time++;
							System.out.println(e.get(i).status);
							continue elevatorLoop;
						}
					}
					
					if (e.get(i).people == 0) {
						e.get(i).setStatus("idle");
					}
				
					if (e.get(i).position == CentralControl.TOPFLOOR) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(true);
						e.get(i).setStatus("going down");
					} else if (e.get(i).position == 0) {
						e.get(i).setDirUp(true);
						e.get(i).setDirDown(false);
						e.get(i).setStatus("going up");
					}
					
					// moves elevator one step
					if (e.get(i).down) {
						e.get(i).decrement();
						e.get(i).setStatus("going down");
						e.get(i).addTimeUnit();
						time++;
						continue elevatorLoop;
					} 
					else if (e.get(i).up) {
						e.get(i).increment();
						e.get(i).setStatus("going up");
						e.get(i).addTimeUnit();
						time++;
						continue elevatorLoop;
					}
				}
				
				// elevator responding to down call
				if (e.get(i).down) {
					
//					System.out.println("elevator" + i + " responding to down call");
					
					sequenceLoop: for (int j = 0; j < downQueue.size(); j++) {
						// stop elevator/open doors
						if (downQueue.get(j).pickedUp == false
								&& e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT
								&& time >= downQueue.get(j).arrivalTime
								&& e.get(i).position == downQueue.get(j).startFloor
								&& e.get(i).status.equals("going down")) {
							
							e.get(i).setStatus("doors open");
							e.get(i).addTimeUnit();
							time++;
							System.out.println(e.get(i).status);
							continue elevatorLoop;
						}
						// pick up people
						if (downQueue.get(j).pickedUp == false
								&& e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT
								&& time >= downQueue.get(j).arrivalTime
								&& e.get(i).position == downQueue.get(j).startFloor
								&& e.get(i).status.equals("doors open")) {
							
							//add passenger to elevator
							e.get(i).addPerson(downQueue.get(j));
							// assigns the elevator # to the passenger
							downQueue.get(j).setElevatorNr(i);
							downQueue.get(j).isPickedUp(true);
							for (int k = 0; k < sequence.size(); k++) {
								if (sequence.get(k).equals(downQueue.get(j))) {
									sequence.remove(k);
								}
							}
							System.out.println("person added");
							continue sequenceLoop;
						}
						// drop of people
						if ((downQueue.get(j).elevator == i) && (downQueue.get(j).pickedUp == true)
							&& (e.get(i).position == downQueue.get(j).endFloor)) {
							
							CentralControl.waitTime += time - downQueue.get(j).arrivalTime;
							// remove person from elevator
							e.get(i).removePerson(downQueue.get(j));
							// remove person from queue
							downQueue.remove(j);
							System.out.println("person droped");
							continue sequenceLoop;
						}
						
						// close doors
						if (j == downQueue.size()-1
							&& e.get(i).status.equals("doors open")) {
							
							e.get(i).setStatus("doors closed");
							e.get(i).addTimeUnit();
							time++;
							System.out.println(e.get(i).status);
							continue elevatorLoop;
						}
					}
				
					if (e.get(i).position == CentralControl.TOPFLOOR) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(true);
						e.get(i).setStatus("going down");
					} else if (e.get(i).position == 0) {
						e.get(i).setDirUp(true);
						e.get(i).setDirDown(false);
						e.get(i).setStatus("going up");
					}
					
					// moves elevator one step
					if (e.get(i).down) {
						e.get(i).decrement();
						e.get(i).setStatus("going down");
						e.get(i).addTimeUnit();
						time++;
						continue elevatorLoop;
					} 
					else if (e.get(i).up) {
						e.get(i).increment();
						e.get(i).setStatus("going up");
						e.get(i).addTimeUnit();
						time++;
						continue elevatorLoop;
					}
				}
			}
			printToFile(e, writer, time);
			printToConsole(e, time);
		}
		writer.close();
 	}

	public static void printToConsole(
			final ArrayList<CentralControl.Elevator> e, int time) {
		for (int i = 0; i < e.size(); i++) {
			System.out.printf("%d \t\t", time);
			System.out.printf("%d \t", e.get(i).position);
			System.out.printf(" %d \t %d \t", e.get(i).currentElevatorWeight, e.get(i).people);
			System.out.printf(" %d \t %d \t\t", e.get(i).peoplePickedUp, e.get(i).peopleDropedOff);
			e.get(i).resetCount();
		}
		System.out.println();
	}

	public static void printToFile(final ArrayList<CentralControl.Elevator> e,
			PrintWriter writer, int time) {
		// checks if filename already exists
		if (!new File("output").exists()) {
			new File("output").mkdir();
		}

				for (int i = 0; i < e.size(); i++) {
					writer.printf("%d \t\t", time);
					writer.printf("%d \t", e.get(i).position);
					writer.printf(" %d \t %d \t", e.get(i).currentElevatorWeight, e.get(i).people);
					writer.printf(" %d \t %d \t\t", e.get(i).peoplePickedUp, e.get(i).peopleDropedOff);
				}
				writer.println();
	}

	public static int getNextCall(
			final ArrayList<CentralControl.Person> sequence, double time) {
		
		for (int i = 0; i < sequence.size(); i++) {
			if (time >= sequence.get(i).arrivalTime) {
				ArrayList<CentralControl.Person> temp = new ArrayList<>(sequence);
				sequence.remove(i);
				return temp.get(i).startFloor;
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
