package in.gympact.entities;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import in.gympact.enums.Enum.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private Integer coin;
	private Integer level;
	private Float weight;
	private Float heigth;
	private String name;
	private String gender;
	private String phone;
	private String email;
	private String password;
	private String goal;
	private String profileImg;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", shape = JsonFormat.Shape.STRING)
	private LocalDateTime dob;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", shape = JsonFormat.Shape.STRING)
	private LocalDateTime joinOn;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", shape = JsonFormat.Shape.STRING)
	private LocalDateTime lastVisit;
	
	private Role role;
	private Boolean active;
	private Integer highestStreak;
	private Integer currentStreak;
	private Integer highestStreakThisMonth;
	private Integer waterReminder;
	private Integer stretchReminder;
	
	
	@ManyToMany(cascade = {CascadeType.ALL, CascadeType.MERGE})
	private List<Workout> workoutList = new LinkedList<>();
	
	@ManyToMany(cascade = {CascadeType.ALL, CascadeType.MERGE})
	private List<Badges> badgesList= new LinkedList<>();
	
	@ManyToOne(cascade = {CascadeType.ALL, CascadeType.MERGE})
	private Diet diet;

	@OneToMany(cascade = {CascadeType.MERGE}, mappedBy = "user")
	private List<Attendance> attendanceList= new LinkedList<>();
	
	@OneToMany(cascade = {CascadeType.ALL, CascadeType.MERGE}, mappedBy = "user")
	private List<Progress> progressList= new LinkedList<>();
	
	@OneToMany(cascade = {CascadeType.ALL, CascadeType.MERGE}, mappedBy = "user")
	private List<PastWorkout> pastWorkoutList= new LinkedList<>();
	
	@OneToOne(cascade = {CascadeType.ALL, CascadeType.MERGE})
	private CurrentPackage currentPackage;
	
	@ManyToOne(cascade = {CascadeType.ALL})
	private Gym gym;
	
	@ManyToMany(cascade = {CascadeType.ALL, CascadeType.MERGE})
	private List<Message> notifications;

}
