import java.util.ArrayList;


public class BaselineElevator {
	
	public static void runBaseline(final ArrayList<CentralControl.Elevator> e,
			final ArrayList<CentralControl.Person> upQueue,
			final ArrayList<CentralControl.Person> downQueue) {
		
		int time = 1;
        // loop simulates a day. [1 unit = 15 sec]
		while ((upQueue.isEmpty() != true) && (downQueue.isEmpty() != true)) {
			// runs for every elevator
			for (int i = 0; i < e.size(); i++) {
				// direction change @ top/bottom floor
				if (e.get(i).position == -1) {
					e.get(i).setDirUp(true);
					e.get(i).setDirDown(false);
				}
				if (e.get(i).position == (CentralControl.TOPFLOOR+1)) {
					e.get(i).setDirUp(false);
					e.get(i).setDirDown(true);
				}
				
				// checks if any available passengers are going up
				if (e.get(i).up == true) {
					for (int j = 0; j < upQueue.size(); j++) {
						// picks up passenger
						if ((upQueue.get(j).pickedUp == false) 
							&& (e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT) 
							&& (time >= upQueue.get(j).arrivalTime)
							&& (e.get(i).position == upQueue.get(j).startFloor)) {
								// add passenger weight to elevator
								e.get(i).addWeight(upQueue.get(j).weight);
								// calculate waiting time
								CentralControl.totalWaitTime += (time - upQueue.get(j).arrivalTime)
									+ (upQueue.get(j).endFloor - upQueue.get(j).startFloor);
								upQueue.get(j).isPickedUp(true);
						}
						// drops off passenger
						if ((upQueue.get(j).pickedUp == true)
							&& (e.get(i).position == upQueue.get(j).endFloor)) {
							// remove passenger weight from elevator
							e.get(i).removeWeight(upQueue.get(j).weight);
							// remove person from queue
							upQueue.remove(j);
						}
					}
					// moves elevator one step up
					e.get(i).increment(e.get(i).position);
				}
				// checks if any available passengers are going down
				if (e.get(i).down == true) {
					for (int j = 0; j < downQueue.size(); j++) {
						// picks up passenger
						if ((downQueue.get(j).pickedUp == false)
							&& (e.get(i).currentElevatorWeight < CentralControl.MAX_ELEVATOR_WEIGHT)
							&& (time >= downQueue.get(j).arrivalTime) 
							&& (e.get(i).position == downQueue.get(j).startFloor)) {
								// add passenger weight to elevator
								e.get(i).addWeight(downQueue.get(j).weight);
								// calculates waiting time
								CentralControl.totalWaitTime += (time - downQueue.get(j).arrivalTime)
									+ (downQueue.get(j).startFloor - downQueue.get(j).endFloor);
								downQueue.get(j).isPickedUp(true);
						}
						// drops off passenger
						if ((downQueue.get(j).pickedUp == true)
								&& (e.get(i).position == downQueue.get(j).endFloor)) {
								// remove passenger weight from elevator
								e.get(i).removeWeight(downQueue.get(j).weight);
								// remove person from queue
								downQueue.remove(j);
							}
					}
					// moves elevator one step down
					e.get(i).decrement(e.get(i).position);
				}
			}
			time++;
		}
	}
	public static void main (String[] args){
		
	}
	
}
