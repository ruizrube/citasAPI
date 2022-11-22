## REST API

HTTP APIs for this appointment service 
* List of users
  * URI: https://server/users
* List of appointments
  * URI: https://server/appointments
* DialogFlow Webhooks
  * URI: https://server/dialogflow

## DIALOGFLOW WEBHOOKS


Webhooks for this appointment service:

* Sign in a user on the system 
   * Intent name: SignIn
   * Input parameter: identityDocumentNumber (@sys.any)
   * Required context: user-identified

* Get the next appointment of the user
  * Intent name: NextAppointment
  * Required context: UserIdentified

* Make a new appointment
  * Intent name: MakeAppointment
  * Required context: user-identified
  * Provides context: appointment-pending
  

* Confirm the current appointment 
  * Intent name: MakeAppointment-confirm
  * Input context: user-identified
  * Input context: appointment-pending
  * Input parameter: appointmentType (@AppointmentType)
  * Input parameter: subject (@sys.any)
  * Removes context: appointment-pending


* Cancel the current appointment 
  * Intent name: MakeAppointment-cancel 
  * Required context: UserIdentified
  * Removes context: appointment-pending
  

List of entities for this appointment service:
* AppointmentType
  * Values: CUT, COLORING, CONSULTATION
