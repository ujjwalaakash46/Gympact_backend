package in.gympact.respositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Group;

@Repository
public interface GroupRepositry extends JpaRepository<Group, Integer> {

}
