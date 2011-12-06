package library;

public class Item extends DatabaseObject {

	private Effect effect;
	private ItemType type;

	public Item(int id) {
		super(id);
		params.put("id", null);
		params.put("name", null);
		params.put("description", null);
		params.put("type", null);
		params.put("usable", null);
		params.put("equippable", null);
		params.put("requireditem", null);
		params.put("requiredquantity", null);
		params.put("requiredlevel", null);
		params.put("effect", null);
		params.put("effectamount", null);
		params.put("effectdescription", null);
		this.setTableName("items");
		this.load();
		if (this.getID() > -1) {
			this.effect = Effect.valueOf(this.get("effect"));
			this.type = ItemType.valueOf(this.get("type"));
		}
	}

	/**
	 * Does the given effect of this item.
	 */
	public void doEffect(int characterID) {
		effect.doEffect(characterID, Integer.parseInt(this.get("effectamount")));
	}

	public ItemType getType() {
		return type;
	}
}
