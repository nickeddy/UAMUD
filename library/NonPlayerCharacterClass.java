package library;

import java.util.Random;

public enum NonPlayerCharacterClass {

	CARAVAN_MERCHANT {

		@Override
		public String talk() {
			// TODO Auto-generated method stub
			// Decided to check out the shop, eh? If you want any of this shit,
			// just holler.
			String[] toSay = { "Here's what I've got for sale, partner!",
					"Checking out the shop? Take a look." };
			Random gen = new Random();
			return toSay[gen.nextInt(toSay.length)];
		}

		@Override
		public String toString() {
			return "Caravan Merchant";
		}
	};

	public abstract String talk();

	public abstract String toString();

}
