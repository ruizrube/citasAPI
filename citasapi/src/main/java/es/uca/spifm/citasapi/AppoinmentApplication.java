package es.uca.spifm.citasapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import es.uca.spifm.citasapi.user.User;
import es.uca.spifm.citasapi.user.UserService;

@SpringBootApplication
@EnableJpaRepositories
public class AppoinmentApplication implements CommandLineRunner {

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(AppoinmentApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		User user = new User();
		user.setFirstName("José Luis");
		user.setLastName("Fernandez");
		user.setIdentityDocumentNumber("111");
		user = userService.save(user);

		user = new User();
		user.setFirstName("María del Carmen");
		user.setLastName("Gonzalez");
		user.setIdentityDocumentNumber("222");
		user = userService.save(user);

		user = new User();
		user.setFirstName("Francisco");
		user.setLastName("Ruiz");
		user.setIdentityDocumentNumber("333");
		user = userService.save(user);

		user = new User();
		user.setFirstName("Ana María");
		user.setLastName("López");
		user.setIdentityDocumentNumber("444");
		user = userService.save(user);

	}

}
