package in.gympact.entities;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Gym {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	private String name;
	private String gymCode;
	
//	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.ALL})
//	private List<User> admins = new LinkedList<>();;
//	
//	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.ALL})
//	private List<User> trainers = new LinkedList<>();;
	
	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.ALL}, mappedBy = "gym")
	private List<Workout> workouts = new LinkedList<>();;
	
	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.ALL}, mappedBy = "gym")
	private List<Diet> diets = new LinkedList<>();;
	
	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.ALL}, mappedBy = "gym")
	private List<Package> packages = new LinkedList<>();;
	
	@OneToMany(cascade = {CascadeType.MERGE, CascadeType.ALL}, mappedBy = "gym")
	@JsonIgnore
	private  List<Group> groups = new LinkedList<>();;
	
}
