package es.uca.spifm.citasapi.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

	public Optional<Appointment> findNextAppointment(String userIdentityDocument) throws UserNotFoundException {
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

	public Appointment confirmAppointment(String userId, LocalDateTime dateTime, AppointmentType type, String subject)
			throws UserNotFoundException {

		Optional<User> user = userRepository.findByIdentityDocument(userId);
		if (user.isPresent()) {

			List<Appointment> appointments = appointmentRepository.findByAssignedDoctorAndDateTimeBetween(
					user.get().getDoctor(), dateTime.truncatedTo(ChronoUnit.HOURS),
					dateTime.plusHours(1).truncatedTo(ChronoUnit.HOURS));

			if (appointments.size() == 0) {
				Appointment appointment = new Appointment();
				appointment.setUser(user.get());
				appointment.setDateTime(dateTime);
				appointment.setType(type);
				appointment.setSubject(subject);
				appointment.setAssignedDoctor(user.get().getDoctor());
				return appointmentRepository.save(appointment);
			} else {
				throw new AppointmentNotAvailableException(user.get().getDoctor(), dateTime);
			}

		} else {
			throw new UserNotFoundException(userId);
		}

	}

	public Appointment confirmAppointment(String userId, LocalDateTime dateTime, AppointmentType type)
			throws UserNotFoundException {

		return confirmAppointment(userId, dateTime, type, "");

	}

	public LocalDateTime findNextAvailableSlot(AppointmentType type) {

		return findNextAvailableSlotAfterDate(type, LocalDateTime.now());

	}

	public LocalDateTime findNextAvailableSlotAfterDate(AppointmentType type, LocalDateTime dateTime) {

		Random ran = new Random(System.currentTimeMillis());
		LocalDate newDate = dateTime.plusDays(ran.nextInt(10) + 1).toLocalDate();

		ran = new Random(System.currentTimeMillis());
		LocalTime newTime = LocalTime.of(ran.nextInt(12) + 8, 0);

		return LocalDateTime.of(newDate, newTime);

	}

	public Appointment findById(String id) {
		return appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));

	}

	public List<Appointment> findAll() {
		// TODO Auto-generated method stub
		return appointmentRepository.findAll();
	}
}
