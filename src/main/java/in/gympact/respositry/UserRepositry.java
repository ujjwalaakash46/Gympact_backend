package in.gympact.respositry;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Gym;
import in.gympact.entities.User;
import in.gympact.enums.Enum.Role;

@Repository
public interface UserRepositry extends JpaRepository<User, Integer> {
	
	public List<User> findByGymAndRole(Gym gym, Role role);
	
	@Query("SELECT u FROM User u WHERE (u.name LIKE %:nquery% OR u.phone LIKE %:pquery) AND (u.gym = :gym) AND (u.role = :role)")
	public List<User> findByNameOrPhone(@Param("nquery") String nquery, @Param("pquery") String pquery, @Param("gym") Gym gym, @Param("role") Role role);
	
	public List<User> findByGymAndRoleOrderByHighestStreakThisMonthDesc(Gym gym, Role role);
	
//	@Query(value = "select * from user where phone=:phone", nativeQuery = true)
//	public List<User> findByPhoEquals(@Param("phone") String phone);
	
	public List<User> findByPhone(String phone);
	
	public List<User> findByEmail(String email);
	
	public List<User> findByGymAndRoleAndLastVisitGreaterThanEqual(Gym gym, Role role, LocalDateTime lastVisit);
	
	public List<User> findByGymAndRoleAndJoinOnGreaterThanEqual(Gym gym,Role role, LocalDateTime joinOn);
	
	public List<User> findByGymAndDob(Gym gym, LocalDateTime dob);
	
	public List<User> findByRole(Role role);
	
//	@Query("select u from User u where u.phone=:phone")
//	public List<User> findByPhoEquals(@Param("phone") String phone);
}
