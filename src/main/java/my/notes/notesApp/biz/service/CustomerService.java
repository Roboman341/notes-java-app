package my.notes.notesApp.biz.service;

import lombok.extern.log4j.Log4j2;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.data.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private CustomerRepository customerRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
        String encodedPassword = passwordEncoder().encode(password); // Encode the password
        Customer customer = Customer.builder()
                .userName(username)
                .email(email) // Assuming the Customer entity has an email field
                .password(encodedPassword)
                .authorities("student") // Assign default authority
                .build();

        // Save the new user to the database
        customerRepository.save(customer);

        return customer; // Assuming Customer implements UserDetails
    }
}