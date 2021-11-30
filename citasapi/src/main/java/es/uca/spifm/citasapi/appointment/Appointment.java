package es.uca.spifm.citasapi.appointment;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.uca.spifm.citasapi.user.User;

@Entity
public class Appointment {

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@ManyToOne
    @JsonIgnore
	private User user;
	
	@ManyToOne
    @JsonIgnore
	private User assignedDoctor;
	
	
	private LocalDateTime dateTime;
	private String subject;

	@Enumerated(value = EnumType.STRING)
	private AppointmentType type;

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}

	public User getAssignedDoctor() {
		return assignedDoctor;
	}

	public void setAssignedDoctor(User assignedDoctor) {
		this.assignedDoctor = assignedDoctor;
	}

}
