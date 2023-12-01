package in.gympact.respositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Badges;

@Repository
public interface BadgesRepositry extends JpaRepository<Badges, Integer> {

}
