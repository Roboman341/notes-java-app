package my.notes.notesApp;

import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.data.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NotesAppTests {

	@Autowired
	private CustomerRepository repo;

	@Test
	void contextLoads() {
	}

	@Test
	void canSaveOneCustomer() {
		Customer rob33 = new Customer("testusername2513", "testemail@gmail.com");
		Customer savedCustomer = repo.save(rob33);
		assertThat(savedCustomer.getUserName()).isEqualTo("testusername2513");
	}
}
