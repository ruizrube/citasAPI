package es.uca.spifm.citasapi.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import es.uca.spifm.citasapi.user.User;
import es.uca.spifm.citasapi.user.UserNotFoundException;
import es.uca.spifm.citasapi.user.UserRepository;

@Service
public class AppointmentService {

	private AppointmentRepository appointmentRepository;
	private UserRepository userRepository;

	public AppointmentService(UserRepository userRepository, AppointmentRepository appointmentRepository) {
		this.userRepository = userRepository;
		this.appointmentRepository = appointmentRepository;
	}

	public List<Appointment> findPastAppointments(String userIdentityDocument) throws UserNotFoundException {
		Optional<User> user = userRepository.findByIdentityDocument(userIdentityDocument);
		if (user.isPresent()) {
			return appointmentRepository.findByUserAndDateTimeLessThan(user, LocalDateTime.now());
		} else {
			throw new UserNotFoundException(userIdentityDocument);
		}

	}

	public Optional<Appointment> findNextAppointment(String userIdentityDocument) throws UserNotFoundException{
		Optional<User> user = userRepository.findByIdentityDocument(userIdentityDocument);
		if (user.isPresent()) {

			List<Appointment> data = appointmentRepository.findByUserAndDateTimeGreaterThanEqualOrderByDateTimeAsc(user,
					LocalDateTime.now());
			if (data.size() > 0) {
				return Optional.of(data.get(0));
			} else {
				return Optional.empty();
			}

		} else {
			throw new UserNotFoundException(userIdentityDocument);
		}
	}

	public LocalDateTime findNextAvailableSlot(AppointmentType type) {
		return null;
	}

	public String confirmAppointment(String userId, LocalDateTime dateTime, AppointmentType type, String subject) throws UserNotFoundException {

		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			Appointment appointment = new Appointment();
			appointment.setUser(user.get());
			appointment.setDateTime(dateTime);
			appointment.setType(type);
			appointment.setSubject(subject);
			appointmentRepository.save(appointment);

		} else {
			throw new UserNotFoundException(userId);
		}

		return "";
	}

	public Appointment findById(String id) {
		return appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));

	}
}
