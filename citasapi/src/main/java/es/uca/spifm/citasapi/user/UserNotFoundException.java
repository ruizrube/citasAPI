package es.uca.spifm.citasapi.user;

public class UserNotFoundException extends Exception {


	private static final long serialVersionUID = 802398032276978914L;

	public UserNotFoundException(String id) {
		super("Could not find user with id: " + id);
	}

	
	
}
