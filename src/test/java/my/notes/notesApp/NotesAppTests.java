package my.notes.notesApp;

import jakarta.transaction.Transactional;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.biz.service.CustomerService;
import my.notes.notesApp.biz.service.NoteService;
import my.notes.notesApp.data.NoteRepository;
import my.notes.notesApp.data.RoleRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class NotesAppTests {

	private static final Logger log = LoggerFactory.getLogger(NotesAppTests.class);
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

	@Test
	@Transactional
	void adminCanListAllUsers () throws IllegalAccessException {
		UserDetails newAdmin = customerService.createNewCustomer(
				"admin_user_for_tests",
				"admin_user@mail.com",
				passwordEncoder.encode("admins_password")
		);
		customerService.addRoleToCustomer(newAdmin.getUsername(), "ROLE_ADMIN");
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				newAdmin.getUsername(),
				newAdmin.getPassword(),
				newAdmin.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		assertThat(customerService.getAllCustomers((Customer) newAdmin).iterator().hasNext()).isTrue();
	}


	@Test
	@Transactional
	void adminCanDeleteUser() throws IllegalAccessException {
		UserDetails newAdmin = customerService.createNewCustomer(
				"admin_user_for_tests2",
				"admin_user@mail.com",
				passwordEncoder.encode("admins_password2")
		);
		customerService.addRoleToCustomer(newAdmin.getUsername(), "ROLE_ADMIN");
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				newAdmin.getUsername(),
				newAdmin.getPassword(),
				newAdmin.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails newUser = customerService.createNewCustomer(
				"simple_user_for_tests2",
				"simple_user_user@mail.com",
				passwordEncoder.encode("simple_users_password2")
		);

		Note userNote = new Note(null, "My test note's content 123 7654", "Admins note", LocalDateTime.now(), (Customer) newUser);
		noteService.saveNote(userNote);
		assertThat(customerService.loadUserByUsername(newUser.getUsername()).getUsername()).isEqualTo("simple_user_for_tests2");

		customerService.deleteUserByUsername(newUser.getUsername(), (Customer) newAdmin);

		assertThrows(UsernameNotFoundException.class, () -> {
			customerService.loadUserByUsername("simple_user_for_tests2");
		});
	}
}