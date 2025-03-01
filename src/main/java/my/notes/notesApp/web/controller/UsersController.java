package my.notes.notesApp.web.controller;

import lombok.extern.log4j.Log4j2;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.service.CustomerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.attribute.UserPrincipalNotFoundException;

@Controller
@RequestMapping("/users")
@Log4j2
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UsersController {

    CustomerService customerService;

    public UsersController (CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String showUsers (Model model) throws IllegalAccessException {
        Customer currentUser = customerService.getCurrentUser();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", customerService.getAllCustomers(currentUser));
        return "users/users"; // html file within a folder
    }

    @PostMapping("/{id}/delete")
    public String deleteCustomer (@PathVariable Long id) throws UserPrincipalNotFoundException, IllegalAccessException {
        Customer currentUser = customerService.getCurrentUser();
        customerService.deleteUserById(id, currentUser);
        return "redirect:/users";
    }
}
