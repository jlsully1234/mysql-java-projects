/**
 * 
 */
package projects.exception;

/**
 * 
 */

// Class declared as extending the RuntimeExcdeption, that means it's am unchecked exception
// it is designed to represent a database related exception
@SuppressWarnings("serial")
public class DbException extends RuntimeException {

	
	
	// Constructor 1, correctly calls the superclass constructor with a message, 
	// allowing the exception to carry a descriptive message.
	public DbException(String message) {
		super(message);
	
	}

	// Constructor 2, initializes the exception with a cause
	public DbException(Throwable cause) {
		super(cause);
	
	}
	// Constructor 3 calls the superclass constructor with a message & cause, allowing for detailed exception information.

	public DbException(String message, Throwable cause) {
		super(message, cause);

	}

	
}
