package my.notes.notesApp.biz.service;

import lombok.extern.log4j.Log4j2;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.data.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Bean;

@Log4j2
@Service
public class CustomerService implements UserDetailsService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve user details by username from the database
        Customer customer = customerRepository.findByUserName(username).getFirst();
        if (customer == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return customer; // Assuming Customer implements UserDetails
    }

    public UserDetails createNewCustomer(String username, String email, String password) {
        if (customerRepository.findByUserName(username).isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);
            Customer customer = Customer.builder()
                    .userName(username)
                    .email(email)
                    .password(encodedPassword)
                    .authorities("ROLE_STUDENT") // Assign default authority
                    .build();

            customerRepository.save(customer);
            return customer;
        } else {
            throw new IllegalArgumentException("Username already exists!");
        }
    }

    public Customer getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByUserName(username).getFirst(); // TODO: think about handling exception here
    }
}