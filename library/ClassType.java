package library;

public enum ClassType {

	NINJA(50, 10, 10, 5, 5, 3, 2, 5, 2, 15, 5, 1, 1, .5, .1, .1, .4, .2) // Based
																			// on
																			// Strength
																			// and
																			// Endurance
	{

		public double getDamage(int level, boolean isApAttack) {
			if (isApAttack)
				return (int)(getStats(level)[6] + getStats(level)[8] * Math.random());// intellegence
																	// + luck
			return (int)(getStats(level)[2] + getStats(level)[8] * Math.random()); // strength +
																	// luck
		}

		public double getHitChance(int level, int monsterLevel) {
			return (int)((getStats(level - monsterLevel)[7]) + +getStats(level)[8]
					* Math.random()); // agility +luck
		}

		@Override
		public String toString() {
			return "Ninja";
		}
	},
	CYBORG(40, 30, 2, 3, 5, 6, 10, 2, 6, 10, 20, 5, .5, .5, .5, 1.0, .1, .5) // Based
																				// on
																				// Intellegence
																				// and
																				// Perception
	{

		public double getDamage(int level, boolean isApAttack) {
			if (isApAttack)
				return (int)(getStats(level)[6] + getStats(level)[8] * Math.random());// intellegence
																	// + luck
			return (int)(getStats(level)[2] + getStats(level)[8] * Math.random()); // strength +
																	// luck
		}

		public double getHitChance(int level, int monsterLevel) {
			return (int)(getStats(level - monsterLevel)[7]) + +getStats(level)[8]
					* Math.random(); // agility + +luck
		}

		@Override
		public String toString() {
			return "Cyborg";
		}
	},

	CHILD(5, 0, 1, 5, 1, 15, 2, 2, 15, 15, 5, 1, 1, 1, 1, 1, 1, 2) // Based on
																	// Luck and
																	// Charisma,
																	// grows
																	// FAST
	{

		public double getDamage(int level, boolean isMagic) {
			return (int)(getStats(level)[2] + getStats(level)[8] * Math.random()); // strength +
																	// luck
		}

		public double getHitChance(int level, int monsterLevel) {
			return (int)(.8 + getStats(level)[8] * Math.random()); // all luck
		}

		@Override
		public String toString() {
			return "Child";
		}
	},
	GUNSLINGER(45, 20, 3, 3, 5, 10, 5, 10, 8, 8, 15, .5, .5, .5, .5, .5, .5, .8) // Based
																					// on
																					// Agility,
																					// Charisma
																					// and
																					// Perception
	{

		public double getDamage(int level, boolean isApAttack) {
			if (isApAttack)
				return (int)(getStats(level)[6] + getStats(level)[8] * Math.random());// intellegence
																	// + luck
			return (int)(getStats(level)[2] + getStats(level)[8] * Math.random()); // strength +
																	// luck
		}

		public double getHitChance(int level, int monsterLevel) {
			return (int)((getStats(level - monsterLevel)[7]) + +getStats(level)[8]
					* Math.random()); // agility +luck
		}

		@Override
		public String toString() {
			return "Gunslinger";
		}
	};

	private static double baseHP;
	private double baseAP, baseStrength, basePerception, baseEndurance,
			baseCharisma, baseIntelligence, baseAgility, baseLuck, hpPerLevel,
			apPerLevel, strengthPerLevel, endurancePerLevel,
			perceptionPerLevel, charismaPerLevel, intelligencePerLevel,
			agilityPerLevel, luckPerLevel;

	ClassType(int baseHP, int baseAP, int baseStrength, int basePerception,
			int baseEndurance, int baseCharisma, int baseIntelligence,
			int baseAgility, int baseLuck, double hpPerLevel,
			double apPerLevel, double strengthPerLevel,
			double perceptionPerLevel, double endurancePerLevel,
			double charismaPerLevel, double intelligencePerLevel,
			double agilityPerLevel, double luckPerLevel) {

		this.baseStrength = baseStrength;
		this.basePerception = basePerception;
		this.baseEndurance = baseEndurance;
		this.baseCharisma = baseCharisma;
		this.baseIntelligence = baseIntelligence;
		this.baseAgility = baseAgility;
		this.baseLuck = baseLuck;
		this.hpPerLevel = hpPerLevel;
		this.apPerLevel = apPerLevel;
		this.strengthPerLevel = strengthPerLevel;
		this.perceptionPerLevel = perceptionPerLevel;
		this.endurancePerLevel = endurancePerLevel;
		this.charismaPerLevel = intelligencePerLevel;
		this.agilityPerLevel = agilityPerLevel;
		this.luckPerLevel = luckPerLevel;
	}

	/**
	 * Returns a Character's statistics of this ClassType.
	 * 
	 * @param level
	 *            The level of the Character.
	 * @return Character's statistics of this ClassType.
	 */
	public int[] getStats(int level) {
		// Array should return in a standard order:
		// int[0] = HP (max), int[1] = AP (max), int[2] = strength,
		// int[3] = perception, int[4] = endurance, int[5] = charisma,
		// int[6] = intelligence, int[7] = agility, int[8] = luck

		int[] stats = new int[9];

		stats[0] = (int) (this.getBaseHP() + level * this.getHpPerLevel());
		stats[1] = (int) (this.getBaseAP() + level * this.getApPerLevel());
		stats[2] = (int) (this.getBaseStrength() + level
				* this.getStrengthPerLevel());
		stats[3] = (int) (this.getBasePerception() + level
				* this.getPerceptionPerLevel());
		stats[4] = (int) (this.getBaseEndurance() + level
				* this.getEndurancePerLevel());
		stats[5] = (int) (this.getBaseCharisma() + level
				* this.getCharismaPerLevel());
		stats[6] = (int) (this.getBaseIntelligence() + level
				* this.getIntelligencePerLevel());
		stats[7] = (int) (this.getBaseAgility() + level
				* this.getAgilityPerLevel());
		stats[8] = (int) (this.getBaseLuck() + level * this.getLuckPerLevel());

		return stats;
	}

	abstract double getDamage(int level, boolean isApAttack);

	abstract double getHitChance(int level, int monsterLevel);

	public double getDefense(int level) {
		return getStats(level)[4] + getStats(level)[8] * .5; // endurance + luck
	}

	public abstract String toString();

	public double getBaseHP() {
		return baseHP;
	}

	public double getBaseAP() {
		return baseAP;
	}

	public int getMaxHP(int level) {
		return this.getStats(level)[0];
	}

	public int getMaxAP(int level) {
		return this.getStats(level)[1];
	}

	public double getBaseIntelligence() {
		return baseIntelligence;
	}

	public double getPerceptionPerLevel() {
		return perceptionPerLevel;
	}

	public double getCharismaPerLevel() {
		return charismaPerLevel;
	}

	public double getHpPerLevel() {
		return hpPerLevel;
	}

	public double getIntelligencePerLevel() {
		return intelligencePerLevel;
	}

	public double getStrengthPerLevel() {
		return strengthPerLevel;
	}

	public double getBaseLuck() {
		return baseLuck;
	}

	public double getBaseEndurance() {
		return baseEndurance;
	}

	public double getLuckPerLevel() {
		return luckPerLevel;
	}

	public double getAgilityPerLevel() {
		return agilityPerLevel;
	}

	public double getBaseStrength() {
		return baseStrength;
	}

	public double getBaseAgility() {
		return baseAgility;
	}

	public double getApPerLevel() {
		return apPerLevel;
	}

	public double getBaseCharisma() {
		return baseCharisma;
	}

	public double getBasePerception() {
		return basePerception;
	}

	public double getEndurancePerLevel() {
		return endurancePerLevel;
	}
}
