package library;

import java.util.Random;

public enum MobClass {

	GHOUL {

		@Override
		public String talk() {
			String[] stuffToSay = { "Eerrrerrrrrr", "*groan*" };
			Random gen = new Random();
			return stuffToSay[gen.nextInt(stuffToSay.length)];
		}

		@Override
		public String toString() {
			return "Ghoul";
		}
	},
	RADROACH {

		@Override
		public String talk() {
			String[] stuffToSay = { "Hsssssssss!", "Ssssssss!" };
			Random gen = new Random();
			return stuffToSay[gen.nextInt(stuffToSay.length)];
		}

		@Override
		public String toString() {
			return "Radroach";
		}
	},
	GECKO {

		@Override
		public String talk() {
			String[] stuffToSay = {
					"I'm not really sure what geckos sound like...!",
					"So here's some easter eggs.", "Rawr!" };
			Random gen = new Random();
			return stuffToSay[gen.nextInt(stuffToSay.length)];
		}

		@Override
		public String toString() {
			return "Gecko";
		}
	},
	WILD_DOG {

		@Override
		public String talk() {
			String[] stuffToSay = { "Grrrrrr!", "Woof!", "*snarl*" };
			Random gen = new Random();
			return stuffToSay[gen.nextInt(stuffToSay.length)];
		}

		@Override
		public String toString() {
			return "Wild Dog";
		}
	};

	public abstract String talk();

	public abstract String toString();

}
