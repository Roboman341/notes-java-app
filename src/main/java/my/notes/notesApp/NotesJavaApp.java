package my.notes.notesApp;

import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.data.CustomerRepository;
import my.notes.notesApp.data.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
public class NotesJavaApp {

	private static final Logger log = LoggerFactory.getLogger(NotesJavaApp.class);

	public static void main(String[] args) {
		SpringApplication.run(NotesJavaApp.class, args);
	}

	 //Not needed since in-memory db is used. TODO: get rid of it later
//	@Bean
//	public CommandLineRunner demo(CustomerRepository customerRepository, NoteRepository noteRepository, PasswordEncoder passwordEncoder){
//		return args -> {
//			customerRepository.save(new Customer(null, System.getenv("ADMIN_USERNAME"), System.getenv("ADMIN_EMAIL"), passwordEncoder.encode(System.getenv("ADMIN_PASSWORD")), "admin"));
//			customerRepository.save(new Customer(null, System.getenv("STUDENT_USERNAME"), System.getenv("STUDENT_EMAIL"), passwordEncoder.encode(System.getenv("STUDENT_PASSWORD")), "student"));
//			customerRepository.findAll().forEach(user -> {
//				log.info(user.toString());
//			});
//			noteRepository.save(new Note(null, "Roboman341", "My test note's content", "note title", LocalDateTime.now()));
//			noteRepository.save(new Note(null, "TestUser", "my content ABCDEFG", "note title", LocalDateTime.now()));
//		};
//	}
}
