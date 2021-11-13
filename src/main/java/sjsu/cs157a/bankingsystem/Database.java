package sjsu.cs157a.bankingsystem;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;

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
	 * Creates a new table if it doesn't exist. This is a template; the SQL string should be modified to create a specific kind of table.
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
					"sampleText VARCHAR(255)";
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
	
	/**
	 * Creates a new user.
	 * @param conn The MySQL connection.
	 * @param firstName The user's first name.
	 * @param lastName The user's last name.
	 * @param email The user's email. Must be unique.
	 * @param pw The user's password.
	 * @return Returns the new users unique userID. If unique key 'email' is already in use, returns -2. If creation fails for any other reason, returns -1.
	 */
	public static int createUser(Connection conn, String firstName, String lastName, String email, byte[] pw) {
		try {
			String sql = "CALL CreateUser(?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, firstName);
			pstmt.setString(2, lastName);
			pstmt.setString(3, email);
			pstmt.setBytes(4, pw);
			// Success check.
			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated == 0) {
				throw new SQLException("No rows affected: user was not created.");
			}
			// Return new userID.
			ResultSet rs = pstmt.executeQuery("SELECT LAST_INSERT_ID()");
	        if (rs.next()) {
	        	return rs.getInt(1);
            }
            else {
                throw new SQLException("No rows affected: user was not created.");
            }
		}
		catch (SQLIntegrityConstraintViolationException emailException) {
			return -2;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Deletes a user with a given userID.
	 * @param conn The MySQL connection.
	 * @param userID The user's unique user id.
	 * @return Returns true on successful deletion.
	 */
	public static boolean deleteUser(Connection conn, int userID) {
		try {
			String sql = "CALL DeleteUser(?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			int rowsUpdated = pstmt.executeUpdate();
			// Success check.	
			if (rowsUpdated == 0) {
				throw new SQLException("No rows affected: user was not deleted.");
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Retrieves a given user's userID.
	 * @param conn The MySQL connection.
	 * @param email The user's email.
	 * @param pw The user's password.
	 * @return Returns the given user's userID. If query fails or user DNE, returns -1.
	 */
	public static int getUserID(Connection conn, String email, byte[] pw) {
		try {
			String sql = "CALL GetUserID(?, ?, ?);";
			CallableStatement cstmt = conn.prepareCall(sql);
			cstmt.setString(1, email);
			cstmt.setBytes(2, pw);
			cstmt.registerOutParameter(3, Types.INTEGER);
			cstmt.executeUpdate();	
			int userID = cstmt.getInt(3);
			if (userID == 0) {
				throw new SQLException("UserID does not exist for given email and password.");
			}
			return userID;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
