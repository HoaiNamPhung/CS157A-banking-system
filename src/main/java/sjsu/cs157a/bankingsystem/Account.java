package sjsu.cs157a.bankingsystem;

import java.sql.Connection;
import java.util.List;

public class Account {
	String accType;
	float balance;
	
	public Account(String accType, float balance) {
		this.accType = accType;
		this.balance = balance;
	}
	
	public static boolean createBankAccount(Connection conn, String bankName, String accType, int balance, int userID) {
		return Database.createBankAccount(conn, bankName, accType, balance, userID);
	}
	
	public static List<Account> getAllUserBankAccountsAtBank(Connection conn, String bankName, int userID) {
		return Database.getAllUserBankAccountsAtBank(conn, bankName, userID);
	}
	
	public static float getBankAccountBalance(Connection conn, String bankName, String accType, int userID) {
		return Database.getBankAccountBalance(conn, bankName, accType, userID);
	}

	public static boolean deleteBankAccount(Connection conn, String bankName, String accType, int userID) {
		return Database.deleteBankAccount(conn, bankName, accType, userID);
	}

	public static float calculateNetWorth(Connection conn, int userID) {
		return Database.calcualteNetWorth(conn, userID);
	}
}
