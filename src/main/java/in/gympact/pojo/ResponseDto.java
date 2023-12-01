package in.gympact.pojo;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseDto {
	public ResponseDto() {}
	
	public String message;
	public Object data;
}
