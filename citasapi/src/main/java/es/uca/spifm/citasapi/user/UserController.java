package es.uca.spifm.citasapi.user;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {

	private final UserService service;

	UserController(UserService service) {
		this.service = service;
	}

	// Single item
	@GetMapping("/Users/{id}")
	Optional<User> one(@PathVariable String id) {

		return service.findById(id);
	}

}