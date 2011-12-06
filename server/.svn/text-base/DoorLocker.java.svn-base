package server;

import java.util.TimerTask;

import library.DatabaseConnection;

/**
 * This TimerTask class simply re-locks the doors in rooms.
 * 
 * @author Nicholas Eddy, Kyohei Mizokami, Mike Novak, Chris Panzero
 * 
 */
public class DoorLocker extends TimerTask {

	@Override
	public void run() {
		int roomNums = DatabaseConnection.getNumberOfRooms();
		for (int i = 1; i <= roomNums; i++) {

			String lockedDoor = DatabaseConnection.getLockedDoor(i);

			if (lockedDoor.equals("north") || lockedDoor.equals("east")
					|| lockedDoor.equals("south") || lockedDoor.equals("west")) {
				DatabaseConnection.setDoorLocked(i, true);
			}
		}
	}
}