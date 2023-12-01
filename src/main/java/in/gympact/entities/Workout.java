package in.gympact.entities;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import in.gympact.enums.Enum.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Workout {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne(cascade = {CascadeType.ALL,CascadeType.MERGE})
	@JsonIgnore
	private Gym gym;
	
	private Integer userId;
	private String name;
	private String discription;
	private String note;
	private Role userRole;
	private Integer durationInMin;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", shape = JsonFormat.Shape.STRING)
	private LocalDateTime createdDate;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", shape = JsonFormat.Shape.STRING)
	private LocalDateTime updatedDate;
	
	@ManyToMany(cascade = {CascadeType.MERGE})
	private List<Exercise> exercises = new LinkedList<>();
}
