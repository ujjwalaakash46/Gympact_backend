package in.gympact.entities;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import in.gympact.enums.Enum.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity(name="plan")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Package {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@ManyToOne(cascade = {CascadeType.ALL,CascadeType.MERGE})
	@JsonIgnore
	private Gym gym;
	
	private Float price;
	private Integer durationInMonths;
	private String name;
	
	@ElementCollection
//	@CollectionTable(name="benefits_table", joinColumns=@JoinColumn(name="id"))
//	@Column(name="benefits")
	private List<String> benefits = new LinkedList<>();
}
