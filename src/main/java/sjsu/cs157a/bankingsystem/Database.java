package sjsu.cs157a.bankingsystem;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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
	
	/**
	 * @param conn The MySql connection
	 * @param bankName The name of the bank
	 * @param accType The type of account
	 * @param balance The starting balance of the account
	 * @param userID The users ID
	 * @return Returns true if the account is created
	 */
	public static boolean createBankAccount(Connection conn, String bankName, String accType, float balance, int userID) {
		try {
			String sql = "CALL CreateBankAccount(?, ?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setString(2, accType);
			pstmt.setFloat(3, balance);
			pstmt.setInt(4, userID);
			pstmt.executeUpdate();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * @param conn The MySql connection
	 * @return Returns all banks in the database
	 */
	public static List<Bank> getAllBanks(Connection conn) {
		ResultSet rset = null;
		List<Bank> banks = new ArrayList<Bank>();
		try {
			String sql = "CALL GetAllBanks()";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				banks.add(new Bank(rset.getString(1), rset.getFloat(2)));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return banks;
	}

	/**
	 * @param conn The MySql connection
	 * @param userID The users ID
	 * @return Returns the account type and balance of all accounts at a given bank
	 */
	public static List<Account> getAllUserBankAccountsAtBank(Connection conn, String bankName, int userID) {
		ResultSet rset = null;
		List<Account> userBankAccounts = new ArrayList<Account>();
		try {
			String sql = "CALL GetAllUserBankAccountsAtBank(?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setInt(2, userID);
			rset = pstmt.executeQuery();
			
			while(rset.next()) {
				userBankAccounts.add(new Account(rset.getString(1), rset.getFloat(2)));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return userBankAccounts;
	}

	/**
	 * @param conn The MySql connection
	 * @param bankName The name of the bank
	 * @param accType The type of account
	 * @param userID The users ID
	 * @return
	 */
	public static boolean deleteBankAccount(Connection conn, String bankName, String accType, int userID) {
		try {
			String sql = "CALL DeleteBankAccount(?, ?, ?);";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setString(2, accType);
			pstmt.setInt(3, userID);
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
	 * @param conn The MySql connection
	 * @param bankName The name of the bank
	 * @param accType The type of account
	 * @param userID The users ID
	 * @return Returns the balance of a users account
	 */
	public static float getBankAccountBalance(Connection conn, String bankName, String accType, int userID) {
		ResultSet rset = null;
		float balance = -1;
		try {
			String sql = "CALL GetBankAccountBalance(?, ?, ?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bankName);
			pstmt.setString(2, accType);
			pstmt.setInt(3, userID);
			rset = pstmt.executeQuery();
			rset.next();
			
			balance = rset.getFloat(1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return balance;
	}

	/**
	 * @param conn The MySql connection
	 * @param userID The users ID
	 * @return Return a users net worth
	 */
	public static float calculateNetWorth(Connection conn, int userID) {
		ResultSet rset = null;
		float netWorth = -1;
		try {
			String sql = "CALL CalculateNetWorth(?)";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userID);
			rset = pstmt.executeQuery();
			rset.next();
			
			netWorth = rset.getFloat(1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return netWorth;
	}
}
