package util.exceptions;

public class ConnectionErrorException extends Exception {

	public ConnectionErrorException(Throwable cause) {
		super(cause);
	}
	
	public ConnectionErrorException(String message) {
		super(message);
	}
	
}
