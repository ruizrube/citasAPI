package es.uca.spifm.citasapi.appointment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.uca.spifm.citasapi.user.User;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

	List<Appointment> findByUserAndDateTimeLessThan(User user, LocalDateTime dateTime);

	List<Appointment> findByUserAndDateTimeGreaterThanEqualOrderByDateTimeAsc(User user, LocalDateTime dateTime);

	List<Appointment> findByDateTimeBetween(LocalDateTime from, LocalDateTime to);

}
