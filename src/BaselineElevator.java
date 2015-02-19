import java.util.ArrayList;


public class BaselineElevator {
	
	public static void runBaseline(final ArrayList<CentralControl.Elevator> e,
			final ArrayList<CentralControl.Person> upQueue,
			final ArrayList<CentralControl.Person> downQueue) {
		
		System.out.printf("Time \t\t");
		for (int i = 0; i < e.size(); i++) {
		System.out.printf("Floor \t");
		System.out.printf("Weight \t Pers \t");
		System.out.printf("Picks \t Drops \t\t");
		}
		System.out.println();
		
		int day = 1920;
        // loop simulates a day. [1 unit = 15 sec]
		for (int time = 0; time <= day; time++) {
			
			// runs for every elevator
			for (int i = 0; i < e.size(); i++) {
				if (time == day && !(e.get(i).people == 0)) {
					day++;
				}
				// checks if any available passengers are going up
				if (e.get(i).up == true) {
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
							// calculate waiting time
							CentralControl.waitTime += (time - upQueue.get(j).arrivalTime)
								+ (upQueue.get(j).endFloor - upQueue.get(j).startFloor);
								upQueue.get(j).isPickedUp(true);
						}
						// drops off passenger
						else if ((i == upQueue.get(j).elevator) && (upQueue.get(j).pickedUp == true)
							&& (e.get(i).position == upQueue.get(j).endFloor)) {
							// remove person from elevator
							e.get(i).removePerson(upQueue.get(j));
							// remove person from queue
							upQueue.remove(j);
							
						}
						
					}
					// moves elevator one step up
					if (e.get(i).position == (CentralControl.TOPFLOOR)) {
						e.get(i).setDirUp(false);
						e.get(i).setDirDown(true);
					} else {
					e.get(i).increment(e.get(i).position);
					}
				}
				// checks if any available passengers are going down
				else if (e.get(i).down == true) {
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
							// calculates waiting time
							CentralControl.waitTime += (time - downQueue.get(j).arrivalTime)
								+ (downQueue.get(j).startFloor - downQueue.get(j).endFloor);
								downQueue.get(j).isPickedUp(true);	
						}
						// drops off passenger
						else if ((i == downQueue.get(j).elevator) && (downQueue.get(j).pickedUp == true)
								&& (e.get(i).position == downQueue.get(j).endFloor)) {
								// remove person from elevator
								e.get(i).removePerson(downQueue.get(j));
								// remove person from queue
								downQueue.remove(j);
								
							}
					}
					// moves elevator one step down
					if (e.get(i).position == 0) {
						e.get(i).setDirUp(true);
						e.get(i).setDirDown(false);
					} else {
					e.get(i).decrement(e.get(i).position);
					}
				}
			}
			
				System.out.printf("%d \t\t", time);
				for (int i = 0; i < e.size(); i++) {
					System.out.printf("%d \t", e.get(i).position);
					System.out.printf(" %d \t %d \t", e.get(i).currentElevatorWeight, e.get(i).people);
					System.out.printf(" %d \t %d \t\t", e.get(i).peoplePickedUp, e.get(i).peopleDropedOff);
					e.get(i).resetCount();
				}
				System.out.println();
				
//				try {
//				    Thread.sleep(5000);                 //1000 milliseconds is one second.
//				} catch(InterruptedException ex) {
//				    Thread.currentThread().interrupt();
//				}
		}
	}
	
	
	public static void main (String[] args){
		
	}
	
}
