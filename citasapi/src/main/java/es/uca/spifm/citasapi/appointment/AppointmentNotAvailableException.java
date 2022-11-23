package es.uca.spifm.citasapi.appointment;

import java.time.LocalDateTime;

public class AppointmentNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 4665327678230607596L;

	public AppointmentNotAvailableException(LocalDateTime dateTime) {
		super("The temporary slot is not available");
	}
}