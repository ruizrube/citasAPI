package es.uca.spifm.citasapi.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserService {

	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public User save(User user) {
		return this.userRepository.saveAndFlush(user);
	}

	public long count() {
		
		return userRepository.count();
	}

	public Optional<User> findById(String userId) {
		return userRepository.findById(userId);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public Optional<User> findByIdentityDocumentNumber(String identityDocumentNumber) {
		return userRepository.findByIdentityDocumentNumber(identityDocumentNumber);

	}

	public Optional<User> findByIdentityDocumentNumberAndGivenName(String identityDocumentNumber, String givenName) {
		
		Optional<User> usr= userRepository.findByIdentityDocumentNumber(identityDocumentNumber);
		
		if(usr.isPresent()) {
			if((usr.get().getFirstName() + " " +usr.get().getLastName()).toLowerCase().contains(givenName.toLowerCase())){
				return usr;
			}
		}
		return Optional.empty();

	}

	
}
