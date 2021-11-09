package sjsu.cs157a.bankingsystem;

import java.sql.Connection;
import java.sql.Statement;

public class Database {
	
	/**
	 * Creates a new database if it doesn't exist.
	 * @param conn The MySQL connection.
	 * @param dbName The new database name.
	 * @return Returns true if given database exists, whether or not it needed to be added.
	 */
	public static boolean createDatabase(Connection conn, String dbName) {
		try {
			String sql = "CREATE DATABASE IF NOT EXISTS " + dbName;
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Creates a new table if it doesn't exist. 
	 * @param conn The MySQL connection.
	 * @param tableName The new table name.
	 * @param dropTable Flag indicating whether or existing tables of the same name should be dropped beforehand.
	 * @return Returns true if given table exists, whether or not it needed to be added.
	 */
	public static boolean createTable(Connection conn, String tableName, boolean dropTable) {
		try {
			String dropSql = "DROP TABLE IF EXISTS " + tableName;
			String createSql = "CREATE TABLE IF NOT EXISTS " + tableName +
					"(uid INT PRIMARY KEY AUTO_INCREMENT, " +
					"first VARCHAR(255), " +
					"last VARCHAR(255), " +
					"routingNumber INT)";
			Statement stmt = conn.createStatement();
			if (dropTable) {
				stmt.execute(dropSql);
			}
			stmt.execute(createSql);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
