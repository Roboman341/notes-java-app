package my.notes.notesApp.web.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import my.notes.notesApp.biz.model.Customer;
import my.notes.notesApp.biz.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/register")
public class RegisterController {
    private CustomerService customerService;

    public RegisterController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String showCreateNewCustomerFrom (Model model) {
        model.addAttribute("customer", new Customer());
        return "new-customer"; // html file within a folder
    }

    @PostMapping
    public String saveCustomer(@Valid @ModelAttribute("customer") Customer customer, BindingResult result) {
        if (result.hasErrors()) {
            return "new-customer"; // html file within a folder
        }
        try {
            customerService.createNewCustomer(
                    customer.getUsername(),
                    customer.getEmail(),
                    customer.getPassword()
            );
            log.info("User has been created. Username : {}, Email: {}", customer.getUsername(), customer.getEmail());
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            result.rejectValue("username", "error.username", e.getMessage());
            return "new-customer";
        }
    }
}
