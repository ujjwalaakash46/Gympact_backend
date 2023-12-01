package in.gympact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import in.gympact.pojo.ResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ResponseDto> customeExceptionHandling( CustomException ex){
		return new ResponseEntity<ResponseDto>(new ResponseDto(ex.message,null), ex.status);
	}
	
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDto> handleAssertionFailure(IllegalArgumentException ex) {
        return new ResponseEntity<ResponseDto>(new ResponseDto(ex.getMessage(),null), HttpStatus.BAD_REQUEST);
    }

}
