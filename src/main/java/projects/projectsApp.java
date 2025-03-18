package projects;

// Utility to connect to a database
import java.sql.Connection;

import projects.dao.DbConnection;

public class projectsApp {


// Public method, meaning its accessible anywhere outside the class, it returns no value,
//	Its the main method.
		public static void main(String[] args)  {

			
			//Connection object declares a variable "conn' as a type of connection
			// DbConnection is a method that is used to establish a connection to a database.
			Connection conn = DbConnection.getConnection();

	}
}