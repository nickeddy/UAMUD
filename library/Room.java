package library;

public class Room extends DatabaseObject {

	public Room(int id) {
		super(id);
		params.put("id", null);
		params.put("north", null);
		params.put("south", null);
		params.put("east", null);
		params.put("west", null);
		params.put("description", null);
		params.put("name", null);
		params.put("locked", null);
		params.put("door", null);
		params.put("requireditem", null);
		this.setTableName("rooms");
		this.load();
	}
}
