package es.uca.spifm.citasapi.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	@GetMapping("/Appointments")
	List<Appointment> all() {
		return service.findAll();
	}
	// end::get-aggregate-root[]

	@PostMapping("/Appointments")
	Appointment newAppointment(@RequestParam String userId, @RequestParam LocalDateTime dateTime,
			@RequestParam AppointmentType type, @RequestParam String subject) {

		try {
			return service.confirmAppointment(userId, dateTime, type, subject);
		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
		}

	}

	// Single item
	@GetMapping("/Appointments/{id}")
	Appointment one(@PathVariable String id) {

		return service.findById(id);
	}

	// Single item
	@GetMapping("/Appointments/next")
	Optional<Appointment> next(@RequestParam String userId) {

		try {
			return service.findNextAppointment(userId);
		} catch (UserNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
		}
	}

//	@PutMapping("/Appointments/{id}")
//	Appointment replaceAppointment(@RequestBody Appointment newAppointment, @PathVariable Long id) {
//
//		return repository.findById(id).map(appointment -> {
//			appointment.setDateTime(newAppointment.getDateTime());
//			appointment.setSubject(newAppointment.getSubject());
//			appointment.setUserId(newAppointment.getUserId());
//			return repository.save(appointment);
//		}).orElseGet(() -> {
//			newAppointment.setId(id);
//			return repository.save(newAppointment);
//		});
//	}
//
//	@DeleteMapping("/Appointments/{id}")
//	void deleteAppointment(@PathVariable Long id) {
//		repository.deleteById(id);
//	}
}