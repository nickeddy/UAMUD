package library;

public enum Effect {

	HEAL_HP {
		@Override
		void doEffect(int characterID, int effectAmount) {
			DatabaseConnection.healCharacterHP(characterID, effectAmount);
		}
	},
	HEAL_AP {
		@Override
		void doEffect(int characterID, int effectAmount) {
			DatabaseConnection.healCharacterAP(characterID, effectAmount);
		}
	},
	ADD_ITEM {
		@Override
		void doEffect(int characterID, int itemID) {
			DatabaseConnection.addItemToCharacter(characterID, itemID);
		}
	},
	LIGHTS_ON {
		@Override
		void doEffect(int characterID, int effectAmount) {
			DatabaseConnection.turnLightsOn(characterID);
		}
	},
	LIGHTS_OFF {
		@Override
		void doEffect(int characterID, int effectAmount) {
			DatabaseConnection.turnLightsOff(characterID);
		}
	},
	NONE {
		@Override
		void doEffect(int characterID, int effectAmount) {
			// Don't do anything.
		}
	},
	NUKA_COLA {
		@Override
		void doEffect(int characterID, int effectAmount) {
			// Heals HP and AP by effectAmount, drops a bottlecap
			DatabaseConnection.healCharacterHP(characterID, effectAmount);
			// DatabaseConnection.healCharacterAP(characterID, effectAmount);
			DatabaseConnection.addItemToCharacter(characterID, 21);
		}
	};

	abstract void doEffect(int characterID, int effectAmount);
}
