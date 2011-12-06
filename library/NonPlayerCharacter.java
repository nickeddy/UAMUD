package library;

public class NonPlayerCharacter extends DatabaseObject {

	private NonPlayerCharacterClass npcClass;

	public NonPlayerCharacter(int id) {
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
		params.put("location", null);
		this.setTableName("npcs");
		this.load();
		this.npcClass = NonPlayerCharacterClass.valueOf(this.get("mobclass"));
	}

	public NonPlayerCharacterClass getNPCClass() {
		return this.npcClass;
	}

}
