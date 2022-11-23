package es.uca.spifm.citasapi.appointment;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import es.uca.spifm.citasapi.user.UserNotFoundException;

@RestController
class AppointmentController {

	private final AppointmentService service;

	AppointmentController(AppointmentService service) {
		this.service = service;
	}

	// Aggregate root
	// tag::get-aggregate-root[]
	@GetMapping("/appointments")
	List<Appointment> all() {
		return service.findAll();
	}
	// end::get-aggregate-root[]


	// Single item
	@GetMapping("/appointments/{id}")
	Appointment one(@PathVariable String id) {

		return service.findById(id);
	}

	// Single item
	@GetMapping("/appointments/next")
	Optional<Appointment> next(@RequestParam String userId) {

		try {
			return service.findNextAppointment(userId);
		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
		}
	}

}