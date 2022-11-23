package es.uca.spifm.citasapi.appointment;

public enum AppointmentType {

	CUT("Corte de pelo"), COLORING("Coloración de pelo"), CONSULTATION("Consulta de estilismo");

	private final String label;

	private AppointmentType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	
	
	

}
