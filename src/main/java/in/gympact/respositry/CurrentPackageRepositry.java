package in.gympact.respositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.gympact.entities.CurrentPackage;

@Repository
public interface CurrentPackageRepositry extends JpaRepository<CurrentPackage, Integer> {

}
