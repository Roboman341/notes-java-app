package my.notes.notesApp.data;

import my.notes.notesApp.biz.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> { //TODO: opt for JpaRepository to get pagination
    List<Customer> findByUsername(String username);
    Customer findById (long id);
}
