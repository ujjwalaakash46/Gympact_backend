package in.gympact.entities;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity(name = "notify_group")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Group {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	 private String name;
	 
	 private int totalMembers;
	 
	 @ManyToMany(cascade = {CascadeType.ALL, CascadeType.MERGE})
	 private List<User> members = new LinkedList<>();;
	 
	 @OneToMany(cascade = {CascadeType.ALL, CascadeType.MERGE})
	 private List<Message> messages = new LinkedList<>();;
	 
	 @ManyToOne(cascade = {CascadeType.MERGE})
	 @JsonIgnore
	 private Gym gym;
	 
	 
	
	
}
