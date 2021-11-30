package es.uca.spifm.citasapi.appointment;

import java.time.LocalDateTime;

import es.uca.spifm.citasapi.user.User;

public class AppointmentNotAvailableException  extends RuntimeException {
	

	public AppointmentNotAvailableException(User doctor, LocalDateTime dateTime) {
		super("The temporary slot is not available");
	}
}