package my.notes.notesApp.biz.service;

import lombok.extern.log4j.Log4j2;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.biz.model.Role;
import my.notes.notesApp.data.CustomerRepository;
import my.notes.notesApp.data.NoteRepository;
import my.notes.notesApp.data.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CustomerService implements UserDetailsService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final NoteRepository noteRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           NoteRepository noteRepository) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.noteRepository = noteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Customer> customers = customerRepository.findByUsername(username);
        if (customers.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return customers.get(0); // Safe way to get the first element
    }

    public UserDetails createNewCustomer(String username, String email, String password) {
        if (customerRepository.findByUsername(username).isEmpty()) {
            Customer customer = Customer.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .roles(new HashSet<>())
                    .build();
            Role userRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("Default role not found"));
            customer.getRoles().add(userRole);
            return customerRepository.save(customer);
        } else {
            throw new IllegalArgumentException("Username already exists!");
        }
    }

    public void addRoleToCustomer(String username, String roleName) {
        Customer customer = customerRepository.findByUsername(username).getFirst();
        Optional<Role> role = roleRepository.findByName(roleName);
        if (roleRepository.findByName(roleName).isPresent() && customer != null) {
            customer.getRoles().add(role.get());
            customerRepository.save(customer);
        } else if (roleRepository.findByName(roleName).isEmpty()) {
            throw new RuntimeException("Role not found");
        }
        else if (customer == null) {
            throw new UsernameNotFoundException("User not found");
        }
        else {
            throw new RuntimeException("Something went wrong while adding a role " + role + "to the customer " + customer);
        }
    }

    public Customer getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByUsername(username).getFirst(); // TODO: think about handling exception here
    }

    public void deleteUserByUsername (String username, Customer requester) throws IllegalAccessException {
        if (requester.getRoles().stream()
                .map(Role::getName)
                .anyMatch(roleName -> "ROLE_ADMIN".equals(roleName))) {
            List<Customer> customerUsername = customerRepository.findByUsername(username);
            if (!customerUsername.isEmpty()) {
                noteRepository.deleteAllById(List.of(customerUsername.getFirst().getId()));
                customerRepository.deleteById(customerUsername.getFirst().getId());
            }
            else {
                throw new UsernameNotFoundException("User not found");
            }
        }
        else {
            throw new IllegalAccessException("You do not have access to delete a user");
        }
    }

    public void deleteUserById (Long id, Customer requester) throws IllegalAccessException, UserPrincipalNotFoundException {
        if (requester.getRoles().stream()
                .map(Role::getName)
                .anyMatch(roleName -> "ROLE_ADMIN".equals(roleName))) {
            Optional<Customer> customer = customerRepository.findById(id);
            if (customer.isPresent()) {
                noteRepository.deleteAllById(List.of(customer.get().getId()));
                customerRepository.deleteById(customer.get().getId());
            }
            else {
                throw new UserPrincipalNotFoundException("User not found");
            }
        }
        else {
            throw new IllegalAccessException("You do not have access to delete a user");
        }
    }

    public Iterable<Customer> getAllCustomers (Customer requester) throws IllegalAccessException {
        if (requester.getRoles().stream()
                .map(Role::getName)
                .anyMatch(roleName -> "ROLE_ADMIN".equals(roleName))) {
            log.info("{} as ADMIN just requested all customers", requester.getUsername());
            return customerRepository.findAll();
        } else {
            log.error("The caller: {} does not have admin rights to list all users", requester.getUsername());
            throw new IllegalAccessException("You do not have access to list all users");
        }
    }

    public boolean isAdmin (Customer customer) {
        if (customer.getRoles().stream()
                .map(Role::getName)
                .anyMatch(roleName -> "ROLE_ADMIN".equals(roleName)))
            return true;
        else return false;
    }
}