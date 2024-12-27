package my.notes.notesApp.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.service.CustomerService;
import my.notes.notesApp.data.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class ApiController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ApiController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDetails> createUser(@RequestBody @Valid UserCreateRequest request) {
        if (!customerRepository.findByUserName(request.username()).isEmpty()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        // Create the new user
        UserDetails user = customerService.createNewCustomer(
                request.username(),
                request.email(),
                request.password());
        return ResponseEntity.ok(user);
    }

    record UserCreateRequest(
            @NotBlank String username,
            @Email String email,
            @NotBlank String password) {}
}