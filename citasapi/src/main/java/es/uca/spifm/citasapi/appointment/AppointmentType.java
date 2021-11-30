package es.uca.spifm.citasapi.appointment;

public enum AppointmentType {

	PHONE("Phone"), VIDEO("Video"), FACE_TO_FACE("Face_To_Face");

	public final String label;

	private AppointmentType(String label) {
		this.label = label;
	}

}
