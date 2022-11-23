This is a repository containing a Java Spring Boot application for exposing some HTTP APIs to work with an appointment management system via Dialogflow. It uses a in-memory database.

## SETUP

1. Install Java (version 11 is required)
2. Download asset: *citasapi-x-y.z.jar* from [Github Releases](https://github.com/ruizrube/citasAPI/releases) 
3. Run: $ **java -jar citasapi-x-y.z.jar** from the command line
4. Check that [http://localhost:8080/users](http://localhost:8080/users) provides some demo users in JSON format
5. Install the [ngrok](http://ngrok.com)	tool and configure it with the provided key
6. Run ngrok from the command line: $ **ngrok http 8080** (please aware of the number port)
7. Copy the forwarding URL **https://RANDOMNUMBERS.eu.ngrok.io** -> http://localhost:8080 
8. Check https://RANDOMNUMBERS.eu.ngrok.io/users provides some demo users in JSON format
9. Configure dialogflow fulfillment URL to: https://RANDOMNUMBERS.eu.ngrok.io/dialogflow
10. Please, revise that your intent names and param names match with the expected ones. They are provided below:

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
