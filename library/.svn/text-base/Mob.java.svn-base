package library;

public class Mob extends DatabaseObject {

	private MobClass mobClass;

	public Mob(int id) {
		super(id);
		params.put("id", null);
		params.put("name", null);
		params.put("mobclass", null);
		params.put("homeroom", null);
		params.put("roomrange", null);
		params.put("level", null);
		params.put("hostile", null);
		params.put("expvalue", null);
		params.put("attackable", null);
		params.put("hp", null);
		this.setTableName("mobs");
		this.load();
		this.mobClass = MobClass.valueOf(this.get("mobclass"));
	}

	public MobClass getMobClass() {
		return this.mobClass;
	}

	public int getDamage(int level, int difficultyMultiplier) {
		return (int) (level * difficultyMultiplier * 1.25);
	}

}
