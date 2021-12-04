package sjsu.cs157a.bankingsystem;

import java.sql.*;
import java.util.List;

public class Bank {
	public static List<String> getAllBanks(Connection conn) {
		return Database.getAllBanks(conn);
	}
}
