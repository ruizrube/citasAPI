package es.uca.spifm.citasapi.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserService {

	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void save(User user) {
		this.userRepository.save(user);
	}

	public long count() {
		
		return userRepository.count();
	}

	public Optional<User> findById(String userIdentityDocument) {
		return userRepository.findByIdentityDocument(userIdentityDocument);
	}

	
}
