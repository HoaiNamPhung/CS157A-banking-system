package sjsu.cs157a.bankingsystem;

import java.sql.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Connection conn = SQLConnector.getInstance().getConnection();
        if (conn == null) {
            System.out.println( "No connection." );
        }
        else {
        	System.out.println( "Connection to MySQL ready." );
        	Database.createTable(conn, "TestTable", false);
        	Database.createTable(conn, "TestTable", true);
        }
    }
}
