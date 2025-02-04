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
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class NotesJavaApp {
	private static final Logger log = LoggerFactory.getLogger(NotesJavaApp.class);

	public static void main(String[] args) {
		SpringApplication.run(NotesJavaApp.class, args);
	}

	@Bean
	public CommandLineRunner demo(CustomerRepository customerRepository,
								  NoteRepository noteRepository,
								  PasswordEncoder passwordEncoder,
								  Environment env) {
		return args -> {
			// Only run initialization if no users exist
			if (customerRepository.count() == 0) {
				log.info("No users found. Starting database initialization...");
				initializeUsers(customerRepository, passwordEncoder, env);
				initializeNotes(noteRepository, customerRepository, env);
				log.info("Database initialization completed successfully.");
			} else {
				log.info("Users already exist. Skipping database initialization.");
			}
		};
	}

	private void initializeUsers(CustomerRepository customerRepository,
								 PasswordEncoder passwordEncoder,
								 Environment env) {
		try {
			// Create admin user
			Customer admin = new Customer(
					null,
					getRequiredEnvVariable(env, "ADMIN_USERNAME"),
					getRequiredEnvVariable(env, "ADMIN_EMAIL"),
					passwordEncoder.encode(getRequiredEnvVariable(env, "ADMIN_PASSWORD")),
					"ROLE_ADMIN"
			);

			// Create student user
			Customer student = new Customer(
					null,
					getRequiredEnvVariable(env, "STUDENT_USERNAME"),
					getRequiredEnvVariable(env, "STUDENT_EMAIL"),
					passwordEncoder.encode(getRequiredEnvVariable(env, "STUDENT_PASSWORD")),
					"ROLE_STUDENT"
			);

			// Save users in batch
			List<Customer> users = Arrays.asList(admin, student);
			customerRepository.saveAll(users);

			// Log created users
			log.info("Created users:");
			users.forEach(user -> log.info(user.toString()));

		} catch (IllegalStateException e) {
			log.error("Failed to initialize users: {}", e.getMessage());
			throw e;
		}
	}

	private void initializeNotes(NoteRepository noteRepository,
								 CustomerRepository customerRepository,
								 Environment env) {
		try {
			Customer admin = customerRepository.findByUserName(
					getRequiredEnvVariable(env, "ADMIN_USERNAME")).getFirst();

			Customer student = customerRepository.findByUserName(
					getRequiredEnvVariable(env, "STUDENT_USERNAME")).getFirst();

			if (admin == null || student == null) {
				throw new IllegalStateException("Failed to retrieve users from database");
			}

			// Create notes in batch
			List<Note> notes = Arrays.asList(
					new Note(null,
							"My test note's content",
							"Admin's note",
							LocalDateTime.now(),
							admin),
					new Note(null,
							"my content ABCDEFG",
							"Student's note",
							LocalDateTime.now(),
							student)
			);

			noteRepository.saveAll(notes);
			log.info("Created {} sample notes", notes.size());

		} catch (IllegalStateException e) {
			log.error("Failed to initialize notes: {}", e.getMessage());
			throw e;
		}
	}

	private String getRequiredEnvVariable(Environment env, String variableName) {
		String value = env.getProperty(variableName);
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalStateException(
					String.format("Required environment variable '%s' is not set", variableName)
			);
		}
		return value;
	}
}