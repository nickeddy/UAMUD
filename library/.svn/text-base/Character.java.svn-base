package library;

public class Character extends DatabaseObject {

	private ClassType characterClass;

	public Character(int id) {
		super(id);
		params.put("id", null);
		params.put("name", null);
		params.put("level", null);
		params.put("hp", null);
		params.put("ap", null);
		params.put("location", null);
		params.put("classid", null);
		params.put("isonline", null);
		params.put("experience", null);
		params.put("lights", null);
		params.put("maxhp", null);
		params.put("maxap", null);
		this.setTableName("characters");
		this.load();
		if (this.getID() > -1)
			this.characterClass = ClassType.valueOf(this.get("classid"));

	}

	public ClassType getClassType() {
		return this.characterClass;
	}

	public int[] getStats() {
		return this.characterClass
				.getStats(Integer.parseInt(this.get("level")));
	}

	public int getDamage() {
		return (int) (DatabaseConnection.getDamageBonusForCharacter(this
				.getID()) + characterClass.getDamage(
				Integer.parseInt(this.get("level")), false));
	}

	public int getDefense() {
		return (int) characterClass.getDefense(Integer.parseInt(this
				.get("level")));
	}

	public int getMaxHP(int level) {
		return characterClass.getMaxHP(level);
	}

	public int getMaxAP(int level) {
		return characterClass.getMaxAP(level);
	}

}
