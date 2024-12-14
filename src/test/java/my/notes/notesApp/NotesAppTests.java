package my.notes.notesApp;

import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.data.CustomerRepository;
import my.notes.notesApp.data.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NotesAppTests {

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private NoteRepository noteRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void canSaveOneCustomer() {
		Customer rob33 = new Customer("testusername2513", "testemail@gmail.com");
		Customer savedCustomer = customerRepository.save(rob33);
		assertThat(savedCustomer.getUserName()).isEqualTo("testusername2513");
	}

	@Test
	void canGetNotes() {
		String expectedContentForNote1 = "This is a note1 for canGetNotes test";
		String expectedContentForNote2 = "This is a note2 for canGetNotes test";
		Note savedNote1 = noteRepository.save(new Note(null, "testusername2513", expectedContentForNote1, expectedContentForNote1.concat("Title"), LocalDateTime.now()));
		Note savedNote2 = noteRepository.save(new Note(null, "testusername2513", expectedContentForNote2, expectedContentForNote1.concat("Title"), LocalDateTime.now()));
		Optional<Note> note1Id = noteRepository.findById(savedNote1.getId());
		Optional<Note> note2Id = noteRepository.findById(savedNote2.getId());
		assertThat(note1Id.get().getContent()).isEqualTo(expectedContentForNote1);
		assertThat(note2Id.get().getContent()).isEqualTo(expectedContentForNote2);
	}

//	void canEditNotes() {
//
//	}
}
