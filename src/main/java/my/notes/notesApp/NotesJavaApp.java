package my.notes.notesApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "my.notes.notesApp") // This should resolve the NullPointerException and properly initialize database with roles and users
public class NotesJavaApp {
	public static void main(String[] args) {
		SpringApplication.run(NotesJavaApp.class, args);
	}
}