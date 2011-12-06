package library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a superclass for every object loaded from the database.
 * 
 * @author Nicholas Eddy, Mike Novak, Kyohei Mizokami, Chris Panzero
 * 
 */
public class DatabaseObject {

	protected Map<String, String> params;
	protected int id;
	protected String table;

	public DatabaseObject(int id) {
		params = new HashMap<String, String>();
		this.id = id;
	}

	/**
	 * Gets this DatabaseObject's ID.
	 * 
	 * @return This DatabaseObject's ID.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Gets this DatabaseObject's parameters.
	 * 
	 * @return This DatabaseObject's parameters.
	 */
	public Map<String, String> getParameters() {
		return this.params;
	}

	/**
	 * Sets this DatabaseObject's parameters to given params.
	 * 
	 * @param params
	 *            The parameters to set to.
	 */
	public void setParameters(Map<String, String> params) {
		this.params = params;
	}

	/**
	 * Sets this DatabaseObject's table to given table.
	 * 
	 * @param table
	 *            The table to set this DatabaseObject to.
	 */
	public void setTableName(String table) {
		this.table = table;
	}

	/**
	 * Loads from the database to this DatabaseObject.
	 */
	public void load() {
		load(this.id);
	}

	/**
	 * Loads data from the database from the given ID.
	 * 
	 * @param id
	 *            The ID to load from.
	 */
	public void load(int id) {
		if (id < 1) {
			return;
		}
		DatabaseConnection db = DatabaseConnection.getInstance();
		ResultSet result = db.executeWithResult("select * from " + table
				+ " where id = '" + this.id + "';");
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				entry.setValue(result.getString(entry.getKey()));
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves this current DatabaseObject to the SQLite database. If it is not
	 * ready to save, it will not save and throw a DatabaseErrorException.
	 * 
	 * @throws DatabaseErrorException
	 * @throws SQLException
	 */
	public void save() throws SQLException {
		DatabaseConnection db = DatabaseConnection.getInstance();
		String sql;

		if (this.id < 1) {
			// This branch means the object will be created in the DB
			sql = DatabaseConnection.createInsertQuery(params, table);
		} else {
			// This branch means the object will be updated in the DB
			sql = DatabaseConnection.createUpdateQuery(params, table, this.id);
		}
		db.execute(sql);
	}

	/**
	 * Gets the value of given parameter.
	 * 
	 * @param parameter
	 *            The parameter to get the value for.
	 * @return The value of the given parameter.
	 */
	public String get(String parameter) {
		return params.get(parameter);
	}

	/**
	 * Sets this DatabaseObject's parameters to the given Map<String,String> of
	 * params.
	 * 
	 * @param params
	 *            The parameters to set this DatabaseObject to.
	 */
	public void setAll(Map<String, String> params) {
		for (Map.Entry<String, String> entry : params.entrySet()) {
			this.params.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Sets the parameter p of this DatabaseObject to the given value v.
	 * 
	 * @param p
	 *            The parameter to change.
	 * @param v
	 *            The value to change to.
	 */
	public void set(String p, String v) {
		this.params.put(p, v);
	}

	/**
	 * Determines if this DatabaseObject is ready to save to the database. If
	 * any parameter has a null value, it is not ready to save.
	 * 
	 * @return Whether or not the DatabaseObject is ready to save.
	 */
	public boolean readyToSave() {
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getValue() == null)
				return false;
		}
		return true;
	}
}
