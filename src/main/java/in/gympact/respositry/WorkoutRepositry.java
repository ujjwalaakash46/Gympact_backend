package in.gympact.respositry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Gym;
import in.gympact.entities.Workout;
import in.gympact.enums.Enum.Role;

@Repository
public interface WorkoutRepositry extends JpaRepository<Workout, Integer>  {
	public List<Workout> findByGymAndUserRoleIsNot(Gym gym, Role role);
}
