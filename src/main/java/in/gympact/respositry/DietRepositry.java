package in.gympact.respositry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Diet;
import in.gympact.entities.Gym;

@Repository
public interface DietRepositry extends JpaRepository<Diet, Integer>{
	public List<Diet> findByGym(Gym gym);
}
