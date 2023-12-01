package in.gympact.respositry;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.Message;
import in.gympact.entities.User;

@Repository
public interface MessageRepositry extends JpaRepository<Message, Integer>{
	
}
