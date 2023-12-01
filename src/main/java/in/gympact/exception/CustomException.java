package in.gympact.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{

	String message;
	HttpStatus status;
	public CustomException(String message, HttpStatus status) {
		this.message=message;
		this.status=status;
	}
}
