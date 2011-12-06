package server;

import java.util.List;
import java.util.TimerTask;

import library.DatabaseConnection;

/**
 * This TimerTask simply turns off the lights for all Characters.
 * 
 * @author Nicholas Eddy, Kyohei Mizokami, Mike Novak, Chris Panzero
 * 
 */
public class LightChanger extends TimerTask {

	@Override
	public void run() {

		List<Integer> characterIDs = DatabaseConnection.getCharacters();

		for (int characterID : characterIDs) {
			DatabaseConnection.turnLightsOff(characterID);
		}

	}

}