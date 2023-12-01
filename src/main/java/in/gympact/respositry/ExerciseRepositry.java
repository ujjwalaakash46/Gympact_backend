package in.gympact.respositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Exercise;

@Repository
public interface ExerciseRepositry extends JpaRepository<Exercise, Integer>   {

}
