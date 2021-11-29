package es.uca.spifm.citasapi.appointment;

public class AppointmentNotFoundException extends RuntimeException {
	AppointmentNotFoundException(String id) {
		super("Could not find appointment " + id);
	}
}
