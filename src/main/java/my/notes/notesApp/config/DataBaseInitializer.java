package my.notes.notesApp.config;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.model.Note;
import my.notes.notesApp.biz.model.Role;
import my.notes.notesApp.biz.service.CustomerService;
import my.notes.notesApp.data.NoteRepository;
import my.notes.notesApp.data.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
public class DataBaseInitializer {
    private final RoleRepository roleRepository;
    private final CustomerService customerService;
    private final Environment environment;
    private final NoteRepository noteRepository;

    @Autowired
    public DataBaseInitializer(RoleRepository roleRepository,
                               CustomerService customerService,
                               Environment environment, NoteRepository noteRepository) {
        this.roleRepository = roleRepository;
        this.customerService = customerService;
        this.environment = environment;
        this.noteRepository = noteRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initialize() {
        try {
            checkEnvironmentVariables();
            initializeRoles(); // order is important here
            initializeUsers();
            initializeNotes();
        } catch (Exception e) {
            log.error("Database initialization failed: {}", e.getMessage());
        }
    }

    private void checkEnvironmentVariables() {
        List<String> requiredVariables = Arrays.asList(
                "ADMIN_USERNAME", "ADMIN_EMAIL", "ADMIN_PASSWORD",
                "USER_USERNAME", "USER_EMAIL", "USER_PASSWORD"
        );
        List<String> missingVariables = requiredVariables.stream()
                .filter(var -> environment.getProperty(var) == null)
                .collect(Collectors.toList());
        if (!missingVariables.isEmpty()) {
            log.error("Missing required environment variables in .env file: {}", missingVariables);
            throw new IllegalStateException("Missing required environment variables. Please check your .env file.");
        }
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            log.info("Initializing roles...");
            List<Role> rolesList = Arrays.asList(
                    new Role("ROLE_ADMIN"),
                    new Role("ROLE_USER")
            );
            roleRepository.saveAll(rolesList);
            rolesList.forEach(i -> log.info("Created role: {}", i.getName()));
        }
        else {
            log.info("Roles already exist, skipping initialization");
        }
    }

    private void initializeUsers() {
        try {
            customerService.loadUserByUsername(environment.getProperty("ADMIN_USERNAME"));
        } catch (UsernameNotFoundException e) {
            UserDetails newAdminUser = customerService.createNewCustomer(
                    environment.getProperty("ADMIN_USERNAME"),
                    environment.getProperty("ADMIN_EMAIL"),
                    environment.getProperty("ADMIN_PASSWORD")
            );
            customerService.addRoleToCustomer(newAdminUser.getUsername(),"ROLE_ADMIN");
            log.info("Creating new admin: {}", newAdminUser.getUsername());
        }
        try {
            customerService.loadUserByUsername(environment.getProperty("USER_USERNAME"));
        } catch (UsernameNotFoundException e) {
            UserDetails newUser = customerService.createNewCustomer(
                    environment.getProperty("USER_USERNAME"),
                    environment.getProperty("USER_EMAIL"),
                    environment.getProperty("USER_PASSWORD")
            );
            customerService.addRoleToCustomer(newUser.getUsername(),"ROLE_ADMIN");
            log.info("Creating new user: {}", newUser.getUsername());
        }
    }

    private void initializeNotes() {
        try {
            Customer admin = (Customer) customerService.loadUserByUsername(
                    environment.getProperty("ADMIN_USERNAME"));

            Customer user = (Customer) customerService.loadUserByUsername(
                    environment.getProperty("USER_USERNAME"));

            List<Note> notes = null;
            if (noteRepository.count() == 0) {
                notes = Arrays.asList(
                        new Note(null,
                                "My test note's content",
                                "Admin's note",
                                LocalDateTime.now(),
                                admin),
                        new Note(null,
                                "my content ABCDEFG",
                                "User's note",
                                LocalDateTime.now(),
                                user)
                );
                noteRepository.saveAll(notes);
                log.info("Created {} sample notes", notes.size());
            } else {
                log.info("Notes already exist, skipping initialization");
            }
        } catch (IllegalStateException e) {
            log.error("Failed to initialize notes: {}", e.getMessage());
            throw e;
        }
    }
}
