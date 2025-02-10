package my.notes.notesApp;

import jakarta.transaction.Transactional;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.biz.service.NoteService;
import my.notes.notesApp.data.CustomerRepository;
import my.notes.notesApp.data.NoteRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NotesAppTests {

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private NoteRepository noteRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
	}

	@Test
	void canSaveNewCustomer() {
		Customer savedCustomer = customerRepository.save(new Customer(null,"student_username","student@student.student",passwordEncoder.encode("studentpassword"),"ROLE_STUDENT"));
		assertThat(savedCustomer.getUsername()).isEqualTo("student_username");
	}

	@Test
	void canFindByUsername() {
		Customer savedCustomer = customerRepository.save(new Customer(null,"student_username","student@student.student",passwordEncoder.encode("studentpassword"),"ROLE_STUDENT"));
		Customer customerUserName = customerRepository.findByUserName(savedCustomer.getUsername()).getFirst();
		assertThat(customerUserName.getUsername()).isEqualTo("student_username");
	}

	@Test
	void canSaveAndRetrieveNotes() {
		Customer newCustomer = new Customer(null,"student_username","student@student.student",
				passwordEncoder.encode("studentpassword"),"ROLE_STUDENT");
		customerRepository.save(newCustomer);
		Note newNote = new Note(null, "My test note's content 123 7654", "Student's note",
				LocalDateTime.now(), newCustomer);
		noteRepository.save(newNote);
		String savedNoteContent = noteRepository.findById(customerRepository.findByUserName("student_username").getFirst().getId()).get().getContent();
		assertThat(savedNoteContent).isEqualTo("My test note's content 123 7654");
	}

	@Test
	@Disabled
	void canDeleteNote () {
	}
}
