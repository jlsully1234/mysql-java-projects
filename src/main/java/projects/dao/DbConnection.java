package projects.dao;

// Utilities to connect to a database, including the driver and exception
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;



// Class used to manage a database connection
public class DbConnection {
	
	// Defines five private static variable for the database connection and schema
	private static final String SCHEMA = "projects";
	private static final String USER = "projects";
	private static final String PASSWORD = "projects";
	private static final String HOST = "localhost";
	private static final int PORT = 3306;

	// Code used to establish a connection with a MySQL database connection URL 
	public static Connection getConnection() {
		String url = 
			String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false",
			HOST, PORT, SCHEMA, USER, PASSWORD);
		
		// Prints the URL the connection will use to connect
		System.out.println("Connecting with url=" + url);
		
		// Method attempts to establish a connection to a database using JDBC API 
		try {
			Connection conn = DriverManager.getConnection(url);
			
			// Prints to console that the connection to the SCHEMA is successful  
			System.out.println("Connection to schema " + SCHEMA +" is successful");
			return conn;
			
		// If the connection is not established an exception will be thrown that is handles in a catch block
		} catch (SQLException e) {
		  
			// Prints to console if connection to the DB is not established
			System.out.println("Unable to get connection at " + url);
		  throw new DbException(e);
			
		
		}
		
		
		
	}
}
