package tests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import library.DatabaseConnection;

import org.junit.Test;

public class DatabaseSpeedTest {

	private DatabaseConnection dbc;

	@Test
	public void DumpEntireDatabase() {

		long startTime = Calendar.getInstance().getTimeInMillis();
		// characterequip
		// characterkills
		// characters
		// inventory
		// items
		// npcs
		// roomitems
		// rooms
		// usercharacters
		// users
		System.out.println("----- table : characterequip -----\n");
		Dump1();
		System.out.println("----- table : characterkills -----\n");
		Dump2();
		System.out.println("----- table : characters -----\n");
		Dump3();
		System.out.println("----- table : inventory -----\n");
		Dump4();
		System.out.println("----- table : npcs -----\n");
		Dump5();
		System.out.println("----- table : roomitems -----\n");
		Dump6();
		System.out.println("----- table : rooms -----\n");
		Dump7();
		System.out.println("----- table : usercharacters -----\n");
		Dump8();
		System.out.println("----- table : users -----\n");
		Dump9();
		System.out.println("----- table : items -----\n");
		Dump10();
		long endTime = Calendar.getInstance().getTimeInMillis();

		System.out.println("\n");
		System.out.println("Time it took to dump the database: "
				+ (endTime - startTime) + " ms");
	}

	private void Dump1() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM characterequip;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump2() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM characterkills;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump3() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM characters;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump4() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM inventory;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump5() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM npcs;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump6() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM roomitems;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump7() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM rooms;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump8() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM usercharacters;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump9() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM users;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void Dump10() {
		dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT * FROM items;");

		try {
			while (rs.next()) {
				for (int i = 1; i < 20; i++) {
					try {
						System.out.print(rs.getString(i) + " ");
					} catch (SQLException e) {
						System.out.println("\n");
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
