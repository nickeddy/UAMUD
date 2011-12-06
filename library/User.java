package library;

public class User extends DatabaseObject {

	public User(String username) {
		super(DatabaseConnection.getIDFromUsername(username));
		params.put("id", null);
		params.put("username", null);
		params.put("password", null);
		params.put("name", null);
		params.put("createdate", null);
		params.put("admin", null);
		params.put("banned", null);
		this.setTableName("users");
		this.load(this.getID());
	}

	public boolean validatePassword(String password) {
		return password.equals(this.get("password"));
	}
}
