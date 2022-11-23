## SETUP

1. Install Java (version 11 is required)
2. Install and configure the [ngrok](http://ngrok.com)	tool
3. Configure key and run ngrok from the command line: $ **ngrok http 80**
4. Download file: *citasapi-x-y.z.jar* from [Github Releases](https://github.com/ruizrube/citasAPI/releases) 
5. run: java -jar *citasapi-x-y.z.jar* from the command line
6. Check https://urlgeneratedbyngrok/users 

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
   * Input parameters: 
     * identityDocumentNumber (@sys.any)
     * givenName (@sys.person)
   * Postconditions:
     * Provides new context: user-identified
  

* Get the next appointment of the user
  * Intent name: NextAppointment
  * Preconditions:
     * Requires context: user-identified


* Make a new appointment
  * Intent name: MakeAppointment
  * Input parameters: 
    * appointmentType (@AppointmentType)
  * Preconditions:
     * Requires context: user-identified
  * Postconditions:
     * Provides new context: appointment-pending
 

* Confirm the current appointment 
  * Intent name: MakeAppointment-confirm
  * Preconditions:
     * Requires context: user-identified
     * Requires context: appointment-pending
  * Postconditions:
    * Removes context: appointment-pending

* Cancel the current appointment 
  * Intent name: MakeAppointment-cancel 
  * Preconditions:
    * Requires context: user-identified
    * Requires context: appointment-pending
  * Postconditions:
    * Removes context: appointment-pending
  

List of entities for this appointment service:
* AppointmentType
  * CUT: corte, pelar, recortar
  * COLORING: colorear, teñir, pintar
  * CONSULTATION: consultar, dudas, aconsejar
