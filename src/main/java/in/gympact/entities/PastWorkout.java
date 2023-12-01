package in.gympact.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import in.gympact.jsonViews.View;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class PastWorkout {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", shape = JsonFormat.Shape.STRING)
	private LocalDateTime dateTime;
	
	@JsonIgnoreProperties(value= {"hibernateLazyInitializer","applications"})
	@ManyToOne(fetch=FetchType.LAZY)
	private Workout workout;
	
	@JsonIgnoreProperties(value= {"hibernateLazyInitializer","applications"})
	@ManyToOne(fetch=FetchType.LAZY)
	@JsonIgnore
	User user;
	
}
