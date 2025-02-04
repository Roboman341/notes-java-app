package my.notes.notesApp;

import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.data.CustomerRepository;
import my.notes.notesApp.data.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NotesAppTests {

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private NoteRepository noteRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

//	@Autowired
//	private DataSource dataSource;
//	@Autowired
//	private JdbcTemplate jdbcTemplate;

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

//	@Test
//	void canGetNotes() {
//		String expectedContentForNote1 = "This is a note1 for canGetNotes test";
//		String expectedContentForNote2 = "This is a note2 for canGetNotes test";
//		Note savedNote1 = noteRepository.save(new Note(null, "My test note's content", "Admin's note", LocalDateTime.now(), customerRepository.findByUserName(System.getenv("ADMIN_USERNAME")).getFirst()));
//		Note savedNote2 = noteRepository.save(new Note(null, "My test note's content", "User's note", LocalDateTime.now(), customerRepository.findByUserName(System.getenv("STUDENT_USERNAME")).getFirst()));
//		Optional<Note> note1Id = noteRepository.findById(savedNote1.getId());
//		Optional<Note> note2Id = noteRepository.findById(savedNote2.getId());
//		assertThat(note1Id.get().getContent()).isEqualTo(expectedContentForNote1);
//		assertThat(note2Id.get().getContent()).isEqualTo(expectedContentForNote2);
//	}

//	void canEditNotes() {
//
//	}
}
