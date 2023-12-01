package in.gympact.respositry;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Attendance;
import in.gympact.entities.User;

@Repository
public interface AttendanceRepositry extends JpaRepository<Attendance, Integer> {
	public Attendance findTopByUserAndDateTimeGreaterThanEqualOrderByDateTime(User user, LocalDateTime dateTime);
}
