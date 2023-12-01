package in.gympact.respositry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.gympact.entities.Package;

@Repository
public interface PackageRepositry extends JpaRepository<Package, Integer>{

}
