package es.uca.spifm.citasapi.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.uca.spifm.citasapi.user.User;

public interface AppointmentRepository extends JpaRepository<Appointment, String>{

	List<Appointment> findByUserAndDateTimeLessThan(Optional<User> user, LocalDateTime dateTime);

	 List<Appointment> findByUserAndDateTimeGreaterThanEqualOrderByDateTimeAsc(Optional<User> user, LocalDateTime dateTime);

}
