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

	public Optional<User> findById(String userIdentityDocument) {
		return userRepository.findByIdentityDocument(userIdentityDocument);
	}

	public List<User> findAll() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}

	
}
