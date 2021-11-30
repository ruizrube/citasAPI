package es.uca.spifm.citasapi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.actions.api.ActionContext;
import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.gson.internal.LinkedTreeMap;

import es.uca.spifm.citasapi.appointment.Appointment;
import es.uca.spifm.citasapi.appointment.AppointmentNotAvailableException;
import es.uca.spifm.citasapi.appointment.AppointmentService;
import es.uca.spifm.citasapi.appointment.AppointmentType;
import es.uca.spifm.citasapi.user.User;
import es.uca.spifm.citasapi.user.UserNotFoundException;
import es.uca.spifm.citasapi.user.UserService;

@Component
public class DialogFlowIntents extends DialogflowApp {

	private static final String NO_USER_MSG = "Lo siento, pero no te tengo registrado en el sistema de salud";

	private static final String NO_APPOINTMENT_MSG = "Lo siento, pero a esa hora no hay disponibilidad de cita con su doctor";

	private DateTimeFormatter isoDateFormatter = DateTimeFormatter.ISO_DATE_TIME;

	@Autowired
	private UserService userService;
	@Autowired
	private AppointmentService appointmentService;

	@ForIntent("Identificarme Intent")
	public ActionResponse identificationIntent(ActionRequest request) {

		Optional<User> user = userService.findById((String) request.getParameter("identityDocument"));
		ResponseBuilder builder;
		if (user.isPresent()) {
			builder = getResponseBuilder(request);
			builder.add("Hola " + user.get().getFirstName() + " " + user.get().getLastName());
			ActionContext context = new ActionContext("ctx-useridentified", 10);
			Map<String, String> params = new HashMap<String, String>();
			params.put("identityDocument", user.get().getIdentityDocument());
			params.put("userName", user.get().getFirstName());
			context.setParameters(params);
			builder.add(context);
			
		} else {

			builder = getResponseBuilder(request);
			builder.add(NO_USER_MSG);
		}

		ActionResponse actionResponse = builder.build();

		return actionResponse;
	}

	@ForIntent("Mi Próxima Cita Intent")
	public ActionResponse rememberAppointmentIntent(ActionRequest request) {

		ActionContext context = request.getContext("ctx-useridentified");

		String identityNumber = (String) context.getParameters().get("identityDocument");

		ResponseBuilder builder;

		try {
			Optional<Appointment> appointmentOpt = appointmentService.findNextAppointment(identityNumber);

			if (appointmentOpt.isPresent()) {
				Appointment appointment = appointmentOpt.get();
				builder = getResponseBuilder(request);
				builder.add("Su próxima cita es el día " + appointment.getDateTime().getDayOfMonth() + " a las "
						+ appointment.getDateTime().getHour() + " horas y " + appointment.getDateTime().getMinute()
						+ " minutos");

			} else {

				builder = getResponseBuilder(request);
				builder.add("No tiene ninguna cita");
			}
		} catch (UserNotFoundException e) {
			builder = getResponseBuilder(request);
			builder.add(NO_USER_MSG);
		}

		ActionResponse actionResponse = builder.build();
		return actionResponse;
	}

	@ForIntent("Registrar Cita Intent")
	public ActionResponse registerAppointmentIntent(ActionRequest request) {
		
		ActionContext context = request.getContext("ctx-useridentified");

		// Read user id
		String identityNumber = (String) context.getParameters().get("identityDocument");

		// Read date time

		Object obj = request.getParameter("dateTime");
		String value = "";
		if (obj instanceof LinkedTreeMap) {
			LinkedTreeMap map = (LinkedTreeMap) request.getParameter("dateTime");
			value = (String) map.values().stream().findFirst().get();
		} else if (obj instanceof String) {
			value = (String) request.getParameter("dateTime");
		}

		LocalDateTime dateTime = LocalDateTime.parse(value, isoDateFormatter);

		// Read appointmentType
		AppointmentType appointmentType = AppointmentType.valueOf((String) request.getParameter("appointmentType"));
		String subject = (String) request.getParameter("subject");

		return confirmAppointment(request, identityNumber, dateTime, appointmentType, subject);
	}

	@ForIntent("Consultar Disponibilidad Intent")
	public ActionResponse queryAvaliability(ActionRequest request) {

		LocalDateTime slot = appointmentService.findNextAvailableSlot(AppointmentType.FACE_TO_FACE);

		ResponseBuilder builder = getResponseBuilder(request);

		builder.add("La siguiente cita disponible es para el día " + renderDateTime(slot)
				+ ". ¿Quieres confirmar esta cita?");

		ActionContext context = new ActionContext("ctx-slotproposed", 10);
		Map<String, String> params = new HashMap<String, String>();
		params.put("dateTime", slot.format(isoDateFormatter));
		context.setParameters(params);
		builder.add(context);

		ActionResponse actionResponse = builder.build();
		return actionResponse;

	}

	@ForIntent("Consultar Disponibilidad Intent - yes")
	public ActionResponse confirmAppointmentIntent(ActionRequest request) {

		// Read user id
		ActionContext context = request.getContext("ctx-useridentified");
		String identityNumber = (String) context.getParameters().get("identityDocument");

		// Read date time
		context = request.getContext("ctx-slotproposed");
		String value = (String) context.getParameters().get("dateTime");
		LocalDateTime dateTime = LocalDateTime.parse(value, isoDateFormatter);

		// Read appointmentType
//		AppointmentType appointmentType = AppointmentType.valueOf((String) request.getParameter("appointmentType"));
//		String subject = (String) request.getParameter("subject");
//		
		return confirmAppointment(request, identityNumber, dateTime, AppointmentType.FACE_TO_FACE, "subject");
	}

	@ForIntent("Consultar Disponibilidad Intent - no")
	public ActionResponse cancelAppointmentIntent(ActionRequest request) {

		// Read user id
		ActionContext context = request.getContext("ctx-useridentified");
		String identityNumber = (String) context.getParameters().get("identityDocument");

		// Read date time
		context = request.getContext("ctx-slotproposed");
		String value = (String) context.getParameters().get("dateTime");
		LocalDateTime dateTime = LocalDateTime.parse(value, isoDateFormatter);

		// Read appointmentType
//		AppointmentType appointmentType = AppointmentType.valueOf((String) request.getParameter("appointmentType"));
//		String subject = (String) request.getParameter("subject");
//		
		return confirmAppointment(request, identityNumber, dateTime, AppointmentType.FACE_TO_FACE, "subject");
	}

	private ActionResponse confirmAppointment(ActionRequest request, String identityNumber, LocalDateTime dateTime,
			AppointmentType appointmentType, String subject) {
		ResponseBuilder builder;

		Appointment appointment;
		try {

			appointment = appointmentService.confirmAppointment(identityNumber, dateTime, appointmentType, subject);

			builder = getResponseBuilder(request);
			builder.add("Le confirmo que su próxima cita es el día " + renderDateTime(appointment.getDateTime()));

		} catch (UserNotFoundException e) {
			builder = getResponseBuilder(request);
			builder.add(NO_USER_MSG);
		} catch (AppointmentNotAvailableException e) {
			builder = getResponseBuilder(request);
			builder.add(NO_APPOINTMENT_MSG);
		}

		ActionResponse actionResponse = builder.build();
		return actionResponse;
	}

	private String renderDateTime(LocalDateTime dateTime) {
		// TODO Auto-generated method stub
		return dateTime.getDayOfMonth() + " de "
				+ dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es_ES")) + " a las "
				+ dateTime.getHour() + " horas y " + dateTime.getMinute() + " minutos";
	}

}