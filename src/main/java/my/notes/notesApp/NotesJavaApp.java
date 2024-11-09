package my.notes.notesApp;

import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.data.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NotesJavaApp {

	private static final Logger log = LoggerFactory.getLogger(NotesJavaApp.class);

	public static void main(String[] args) {
		SpringApplication.run(NotesJavaApp.class, args);
	}

	@Bean
	public CommandLineRunner demo(CustomerRepository repository){
		return args -> {
			repository.save(new Customer("Roboman341", "myEmail@gmail.com"));
			repository.findAll().forEach(user -> {
				log.info(user.toString());
			});
		};
	}
}
