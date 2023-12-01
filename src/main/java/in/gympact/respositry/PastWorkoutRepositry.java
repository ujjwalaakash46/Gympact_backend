package in.gympact.respositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.PastWorkout;

@Repository
public interface PastWorkoutRepositry extends JpaRepository<PastWorkout, Integer> {

}
