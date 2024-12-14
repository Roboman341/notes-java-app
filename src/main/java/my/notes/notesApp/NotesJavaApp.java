package my.notes.notesApp;

import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.data.CustomerRepository;
import my.notes.notesApp.data.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class NotesJavaApp {

	private static final Logger log = LoggerFactory.getLogger(NotesJavaApp.class);

	public static void main(String[] args) {
		SpringApplication.run(NotesJavaApp.class, args);
	}

	@Bean
	public CommandLineRunner demo(CustomerRepository customerRepository, NoteRepository noteRepository){
		return args -> {
			customerRepository.save(new Customer("Roboman341", "myEmail@gmail.com"));
			customerRepository.save(new Customer("TestUser", "TestUser@gmail.com"));
			customerRepository.findAll().forEach(user -> {
				log.info(user.toString());
			});
			noteRepository.save(new Note(null, "Roboman341", "My test note's content", "note title", LocalDateTime.now()));
			noteRepository.save(new Note(null, "TestUser", "my content ABCDEFG", "note title", LocalDateTime.now()));
		};
	}
}
