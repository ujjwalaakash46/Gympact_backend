package in.gympact.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Attendance {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private LocalDateTime dateTime;
	
	@JsonIgnoreProperties(value= {"hibernateLazyInitializer","applications"})
	@ManyToOne(fetch=FetchType.LAZY)
	@JsonIgnore
	private User user;
}
