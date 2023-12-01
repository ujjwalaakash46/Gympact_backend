package in.gympact.respositry;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Progress;
import in.gympact.entities.User;

@Repository
public interface ProgressRepositry extends JpaRepository<Progress, Integer> {

	public Progress findTopByUserAndDateTimeGreaterThanEqualOrderByDateTime(User user, LocalDateTime dateTime);
	
	public Progress findTopByUserAndDateTimeGreaterThanEqualOrderByDateTimeAsc(User user, LocalDateTime dateTime);
	
	public Progress findTopByUserOrderByDateTimeAsc(User user);
	
	public Progress findTopByUserOrderByDateTimeDesc(User user);
	
	public List<Progress> findByUserOrderByDateTimeDesc(User user);
	
}
