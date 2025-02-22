package my.notes.notesApp;

import jakarta.transaction.Transactional;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.biz.service.CustomerService;
import my.notes.notesApp.biz.service.NoteService;
import my.notes.notesApp.data.NoteRepository;
import my.notes.notesApp.data.RoleRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NotesAppTests {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private NoteRepository noteRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private NoteService noteService;
	@Autowired
	private RoleRepository roleRepository;

	@Test
	@Transactional
	void canSaveNewCustomer() {
		UserDetails savedCustomer = customerService.createNewCustomer(
				"user_username_for_tests",
				"user_for_tests@user.user",
				passwordEncoder.encode("user_password"));
		customerService.addRoleToCustomer(savedCustomer.getUsername(), "ROLE_USER");
		assertThat(savedCustomer.getUsername()).isEqualTo("user_username_for_tests");
	}

	@Test
	@Transactional
	void canFindByUsername() {
		UserDetails savedCustomer = customerService.createNewCustomer(
				"user_username_for_tests",
				"user_for_tests@user.user",
				passwordEncoder.encode("user_password"));
		customerService.addRoleToCustomer(savedCustomer.getUsername(), "ROLE_USER");
		UserDetails customerUserName = customerService.loadUserByUsername(savedCustomer.getUsername());
		assertThat(customerUserName.getUsername()).isEqualTo("user_username_for_tests");
	}

	@Test
	@Disabled
	void canDeleteCustomer() {

	}

	@Test
	@Transactional
	void canSaveAndRetrieveNotes() {
		UserDetails savedCustomer = customerService.createNewCustomer(
				"user_username_for_tests",
				"user_for_tests@user.user",
				passwordEncoder.encode("user_password"));

		// Set up the Security Context with the test user
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				savedCustomer.getUsername(),
				savedCustomer.getPassword(),
				savedCustomer.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails customerUserName = customerService.loadUserByUsername(savedCustomer.getUsername());
		Note newNote = new Note(null, "My test note's content 123 7654", "User's note", LocalDateTime.now(), (Customer) customerUserName);
		noteService.saveNote(newNote);

		assertThat(newNote.getContent()).isEqualTo("My test note's content 123 7654");
		SecurityContextHolder.clearContext();
		customerService.deleteUserByUsername("user_username_for_tests");
	}

	@Test
	@Transactional
	void canDeleteNote () {
		UserDetails savedCustomer = customerService.createNewCustomer(
				"user_username_for_tests1",
				"user_for_tests@user.user",
				passwordEncoder.encode("user_password"));
		UserDetails customerUserName = customerService.loadUserByUsername(savedCustomer.getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(
				savedCustomer.getUsername(),
				savedCustomer.getPassword(),
				savedCustomer.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		Note newNote = new Note(null, "My test note's content 123 7654", "User's note", LocalDateTime.now(), (Customer) customerUserName);
		noteService.saveNote(newNote);

		noteService.deleteNoteByID(newNote.getId());
		assertThat(noteRepository.findByCreator((Customer) customerUserName)).isEmpty();
		SecurityContextHolder.clearContext();
	}

	@Test
	@Transactional
	void canDeleteAllNotes () {
		UserDetails savedCustomer = customerService.createNewCustomer(
				"user_username_for_tests2",
				"user_for_tests@user.user",
				passwordEncoder.encode("user_password"));
		UserDetails customerUserName = customerService.loadUserByUsername(savedCustomer.getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(
				savedCustomer.getUsername(),
				savedCustomer.getPassword(),
				savedCustomer.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		noteService.saveNote(new Note(null, "My test note's content 312", "User's note", LocalDateTime.now(), (Customer) customerUserName));
		noteService.saveNote(new Note(null, "My test note's content 1", "User's note", LocalDateTime.now(), (Customer) customerUserName));
		noteService.saveNote(new Note(null, "My test note", "User's note", LocalDateTime.now(), (Customer) customerUserName));

		noteService.deleteAllNotesByCreator((Customer) customerUserName);
		assertThat(noteRepository.findByCreator((Customer) customerUserName)).isEmpty();
	}
}
