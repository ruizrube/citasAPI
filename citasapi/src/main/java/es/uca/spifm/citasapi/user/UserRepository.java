package es.uca.spifm.citasapi.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>{

	Optional<User> findByIdentityDocumentNumber(String identityDocumentNumber);


}
