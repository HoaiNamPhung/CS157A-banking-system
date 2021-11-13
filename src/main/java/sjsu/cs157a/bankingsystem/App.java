package sjsu.cs157a.bankingsystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
	public static void main( String[] args ) throws Exception
    {
    	// Prepare MySQL connection.
        Connection conn = SQLConnector.getInstance().getConnection();
        if (conn == null) {
            System.out.println( "No connection to local MySQL server. Please try again later." );
            throw new SQLException();
        }
        else {
        	System.out.println( "Connection to MySQL ready." );	
        }
        
        // User state.   
        int userID = -1;
        String firstName = null;
        String lastName = null;
        String email = null;
        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.now();
    	boolean loggedIn = false;
        
        // Let user log in.
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to login(1), register(2), or quit(3)? (Please input 1~3).");
        boolean inputValid = false;
        while (!inputValid) {
        	switch (scanner.nextLine()) {
        	case "1":
        		inputValid = true;
            	while (!loggedIn) {
                    String pw = null;
                	System.out.println("Please input your email.");
                	email = scanner.nextLine();
                	System.out.println("Please input your password.");
                	pw = scanner.nextLine();
                	userID = User.login(conn, email, pw);
                	if (userID == -1) {
                		System.out.println("The given email and password do not match for any user within the banking system. Please try again.");
                	}
                	else {
                		loggedIn = true;
                	}
            	}
        		break;
        	case "2":
        		inputValid = true;
            	while (!loggedIn) {
                    String pw = null;
                	System.out.println("Please input your first name.");
                	firstName = scanner.nextLine();
                	System.out.println("Please input your lastName.");
                	lastName = scanner.nextLine();
                	System.out.println("Please input your email.");
                	email = scanner.nextLine();
                	System.out.println("Please input your password.");
                	pw = scanner.nextLine();
                	userID = User.register(conn, firstName, lastName, email, pw);
                	if (userID == -2) {
                		System.out.println("Given email is already in use.");
                	}
                	if (userID < 0) {
                		System.out.println("Registration failed. Please try again.");
                	}
                	else {
                		loggedIn = true;
                	}
            	}  
        		break;
        	case "3":
        		inputValid = true;
        		System.out.println("Quitting.");
        		break;
        	default:
        		System.out.println("Invalid input. Please try again.");
        		break;
        	}
        }    
        
        // Logged in message.
        if (loggedIn) {
        	System.out.println("Welcome to your personal banking tracker, " + email + ". Today is " + date + ". It is currently " + time + ".");
        	System.out.println("Please input a number from 1~4 show all actions related to the corresponding category.");
        }
        
        // Banking system usage loop.
        while (loggedIn) {
            System.out.println("Accounts (1) | Transactions (2) | Loans (3) | Banks (4) | Delete User (8) | Sign Out (0)");
            switch (scanner.nextLine()) {
            case "1":
            	System.out.println("Not yet implemented.");
            	break; 
	        case "2":
	        	System.out.println("Not yet implemented.");
	        	break;
		    case "3":
		    	System.out.println("Not yet implemented.");
		    	break;
		    case "4":
		    	System.out.println("Not yet implemented.");
		    	break;
		    case "8":
		    	System.out.println("Are you sure? This will delete the user permanently, making all assets inaccessible. (Y/N)");
		    	String confirmation = scanner.nextLine();
		    	if (confirmation.toUpperCase().equals("Y")) {
		    		System.out.println("User '" + email + "' has been deleted.");
		    		User.deleteUser(conn, userID);
		    		loggedIn = false;
		    	}
		    	else {
		    		System.out.println("Deletion canceled.");
		    	}
		    	break;
		    case "0":
		    	System.out.println("Signing out.");
		    	loggedIn = false;
            }
            System.out.println();
        }
        
        // Close scanner on end.
        scanner.close();
	}
}
