package es.uca.spifm.citasapi.appointment;

public class AppointmentNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2963800934402043054L;

	AppointmentNotFoundException(String id) {
		super("Could not find appointment " + id);
	}
}
