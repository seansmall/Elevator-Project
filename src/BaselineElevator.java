import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class BaselineElevator {
	
	public static void runBaseline(final ArrayList<CentralControl.Elevator> e,
			final ArrayList<CentralControl.Person> upQueue,
			final ArrayList<CentralControl.Person> downQueue) throws FileNotFoundException, UnsupportedEncodingException {
		
		File f = new File("output" + File.separator + "baseline_console" + ".txt");
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		
		int day = 1920;

        // loop simulates a day. [1 unit = 15 sec]
		for (int time = 1; time <= day; time++) {
			
			if (time == day && !(downQueue.isEmpty() && upQueue.isEmpty())) {
				day++;
			}
			printToFile(e, writer, time);
			
			
			// runs for every elevator
			for (int i = 0; i < e.size(); i++) {
				
				// checks if any available passengers are going up
				if (e.get(i).up) {
					for (int j = 0; j < upQueue.size(); j++) {
						// picks up passenger
						if ((upQueue.get(j).pickedUp == false)
							&& (e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT)
							&& (time >= upQueue.get(j).arrivalTime)
							&& (e.get(i).position == upQueue.get(j).startFloor)) {
							//add passenger to elevator
							e.get(i).addPerson(upQueue.get(j));
							// assigns the elevator # to the passenger
							upQueue.get(j).setElevatorNr(i);
							upQueue.get(j).isPickedUp(true);
						}
						// drops off passenger
						else if ((upQueue.get(j).elevator == i) && (upQueue.get(j).pickedUp == true)
							&& (e.get(i).position == upQueue.get(j).endFloor)) {
							
							// calculate waiting time
							CentralControl.waitTime += time - upQueue.get(j).arrivalTime;
							// remove person from elevator
							e.get(i).removePerson(upQueue.get(j));
							// remove person from queue
							upQueue.remove(j);
						}
					}
					// moves elevator one step up
					if (e.get(i).position == (CentralControl.TOPFLOOR )) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(true);
					} else {
						e.get(i).increment();
					}
				}
				// checks if any available passengers are going down
				else if (e.get(i).down) {
					for (int j = 0; j < downQueue.size(); j++) {
						// picks up passenger
						if ((downQueue.get(j).pickedUp == false)
							&& (e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT)
							&& (time >= downQueue.get(j).arrivalTime) 
							&& (e.get(i).position == downQueue.get(j).startFloor)) {
							// add passenger to elevator
							e.get(i).addPerson(downQueue.get(j));
							// assigns the elevator # to the passenger
							downQueue.get(j).setElevatorNr(i);
							downQueue.get(j).isPickedUp(true);	
						}
						// drops off passenger
						else if ((i == downQueue.get(j).elevator) && (downQueue.get(j).pickedUp == true)
								&& (e.get(i).position == downQueue.get(j).endFloor)) {
							
								// calculates waiting time
								CentralControl.waitTime += time - downQueue.get(j).arrivalTime;
								// remove person from elevator
								e.get(i).removePerson(downQueue.get(j));
								// remove person from queue
								downQueue.remove(j);		
						}
					}
					// moves elevator one step down
					if (e.get(i).position == 1) {
						e.get(i).setDirUp(true);
						e.get(i).setDirDown(false);
					} else {
						e.get(i).decrement();
					}
				}
			}
			
				
//				try {
//				    Thread.sleep(2000);                 //1000 milliseconds is one second.
//				} catch(InterruptedException ex) {
//				    Thread.currentThread().interrupt();
//				}
		}
		writer.close();
		
		for (int i = 0; i < downQueue.size(); i++) {
			System.out.println(downQueue.get(i).id);
		}
	}


	public static void printToConsole(
			final ArrayList<CentralControl.Elevator> e, int time) {
		System.out.printf("%d \t\t", time);
		for (int i = 0; i < e.size(); i++) {
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
		writer.printf("%d \t\t", time);
		for (int i = 0; i < e.size(); i++) {
			writer.printf("%d \t", e.get(i).position);
			writer.printf(" %d \t %d \t", e.get(i).currentElevatorWeight, e.get(i).people);
			writer.printf(" %d \t %d \t\t", e.get(i).peoplePickedUp, e.get(i).peopleDropedOff);
			e.get(i).resetCount();
		}
		writer.println();
	}
	
	
	public static void main (String[] args){
		
	}
	
}
