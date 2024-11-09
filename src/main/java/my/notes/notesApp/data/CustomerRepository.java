package my.notes.notesApp.data;

import my.notes.notesApp.biz.model.Customer;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
    List<Customer> findByUserName(String userName);
    Customer findById (long id);
}
