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
import com.google.api.services.dialogflow_fulfillment.v2.model.EventInput;
import com.google.api.services.dialogflow_fulfillment.v2.model.WebhookResponse;
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

	private static final String NO_USER_MSG = "Lo siento pero no te tengo registrado en la agenda. Llámenos por teléfono. Gracias";

	private static final String NO_APPOINTMENT_MSG = "Lo siento pero a esa hora no hay disponibilidad de cita con su peluquero";

	private static final String NO_AVAILABILITY = "Lo siento pero tenemos la agenda completa. Inténtelo de nuevo mañana";

	private static final String NO_APPOINTMENT_FOR_USER = "Actualmente no tiene ninguna cita concertada con nosotros";

	private static final String NO_USER_IDENTIFIED = "Lo siento, para realizar cualquier gestión debe primero identificarse";

	private DateTimeFormatter isoDateFormatter = DateTimeFormatter.ISO_DATE_TIME;

	@Autowired
	private UserService userService;
	@Autowired
	private AppointmentService appointmentService;

	@ForIntent("SignIn")
	public ActionResponse identificateUserIntent(ActionRequest request) {

		System.out.println("Processing intent: SignIn");

		ResponseBuilder builder = getResponseBuilder(request);

		// Read request parameter
		String identityDocumentNumber = (String) request.getParameter("identityDocumentNumber");
		
		String givenName = readPerson(request.getParameter("givenName"));


		Optional<User> user = userService.findByIdentityDocumentNumberAndGivenName(identityDocumentNumber,givenName);

		if (user.isPresent()) {

			// Write response
			builder.add(
					"Hola " + user.get().getFirstName() + " " + user.get().getLastName() + ", ¿cómo puedo ayudarte?");

			// Set output context and its parameters
			ActionContext context = new ActionContext("user-identified", 10);
			Map<String, String> params = new HashMap<String, String>();
			params.put("userId", user.get().getId());
			params.put("userName", user.get().getFirstName());
			context.setParameters(params);

			builder.add(context);

		} else {
			builder.add(NO_USER_MSG);
		}

		return builder.build();
	}

	

	@ForIntent("NextAppointment")
	public ActionResponse rememberAppointmentIntent(ActionRequest request) {
		System.out.println("Processing intent: NextAppointment");

		ResponseBuilder builder = getResponseBuilder(request);

		// Read context
		ActionContext context = request.getContext("user-identified");

		if (context != null) {
			String userId = (String) context.getParameters().get("userId");
			String name = (String) context.getParameters().get("userName");

			try {
				Optional<Appointment> appointmentOpt = appointmentService.findNextAppointment(userId);

				if (appointmentOpt.isPresent()) {
					Appointment appointment = appointmentOpt.get();

					builder.add("Estimado " + name + ", su próxima cita es el día "
							+ renderDateTime(appointment.getDateTime()));

				} else {

					builder.add(NO_APPOINTMENT_FOR_USER);
				}
			} catch (UserNotFoundException e) {
				builder.add(NO_USER_MSG);
			}
		} else {
			// triggerCustomEvent(builder,EVENT_IDENTIFY_USER);
			builder.add(NO_USER_IDENTIFIED);

		}
		return builder.build();
	}

	@ForIntent("MakeAppointment")
	public ActionResponse queryAvaliabilityIntent(ActionRequest request) {

		ResponseBuilder builder = getResponseBuilder(request);

		System.out.println("Processing intent: MakeAppointment");

		// Read context
		ActionContext context = request.getContext("user-identified");

		if (context != null) {

			LocalDateTime slot = appointmentService.findNextAvailableSlot(AppointmentType.CONSULTATION);

			if (slot != null) {
				builder.add("La siguiente fecha disponible es el día " + renderDateTime(slot)
						+ ". ¿Te gustaría solicitar una cita para ese día?");

				// Read appointmentType 
				AppointmentType appointmentType = AppointmentType
						.valueOf((String) request.getParameter("appointmentType"));

				// Set output context
				context = new ActionContext("appointment-pending", 5);
				Map<String, String> params = new HashMap<String, String>();
				params.put("dateTime", slot.format(isoDateFormatter));
				params.put("appointmentType", appointmentType.name());

				context.setParameters(params);
				builder.add(context);
			} else {
				builder.add(NO_AVAILABILITY);
			}
		} else {
			builder.add(NO_USER_IDENTIFIED);

		}

		return builder.build();

	}

	@ForIntent("MakeAppointment-confirm")
	public ActionResponse confirmAppointmentIntent(ActionRequest request) {
		System.out.println("Processing intent: MakeAppointment-confirm");

		ResponseBuilder builder = getResponseBuilder(request);

		// Read user id from the context
		ActionContext context = request.getContext("user-identified");
		String userId = (String) context.getParameters().get("userId");

		// Read date time from the context
		context = request.getContext("appointment-pending");

		LocalDateTime dateTime = readDateTime(context.getParameters().get("dateTime"));

		AppointmentType appointmentType = AppointmentType
				.valueOf(context.getParameters().get("appointmentType").toString());

		try {

			Appointment appointment = appointmentService.confirmAppointment(userId, dateTime, appointmentType);

			builder.add("Le confirmo que su próxima cita para " + appointment.getType().getLabel().toLowerCase() + " es el "
					+ renderDateTime(appointment.getDateTime()));

			builder.removeContext("appointment-pending");

		} catch (UserNotFoundException e) {
			builder.add(NO_USER_MSG);
		} catch (AppointmentNotAvailableException e) {
			builder.add(NO_APPOINTMENT_MSG);
		}

		return builder.build();

	}

	@ForIntent("MakeAppointment-cancel")
	public ActionResponse cancelAppointmentIntent(ActionRequest request) {
		System.out.println("Processing intent: MakeAppointment-cancel");

		ResponseBuilder builder = getResponseBuilder(request);

		builder.removeContext("appointment-pending");

		return builder.build();

	}

	private String renderDateTime(LocalDateTime dateTime) {
		return dateTime.getDayOfMonth() + " de "
				+ dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")) + " a las "
				+ dateTime.getHour() + " horas y " + dateTime.getMinute() + " minutos";
	}

	private LocalDateTime readDateTime(Object parameter) {
		String value = "";
		if (parameter instanceof LinkedTreeMap) {
			LinkedTreeMap map = (LinkedTreeMap) parameter;
			value = (String) map.values().stream().findFirst().get();
		} else if (parameter instanceof String) {
			value = (String) parameter;
		}

		return LocalDateTime.parse(value, isoDateFormatter);
	}

	private String readPerson(Object parameter) {
		String value = "";
		if (parameter instanceof LinkedTreeMap) {
			LinkedTreeMap map = (LinkedTreeMap) parameter;
			value = (String) map.values().stream().findFirst().get();
		} else if (parameter instanceof String) {
			value = (String) parameter;
		}
		return value;
	}
	
	
	/**
	 * Method to return in the response body the event name to be triggered in
	 * Dialogflow
	 * 
	 * @param builder
	 * @param eventName
	 */
	protected void triggerCustomEvent(ResponseBuilder builder, String eventName) {
		WebhookResponse webhookResponse = new WebhookResponse();
		EventInput eventInput = new EventInput();
		eventInput.setName(eventName);
		webhookResponse.setFollowupEventInput(eventInput);
		builder.setWebhookResponse$actions_on_google(webhookResponse);

	}

}