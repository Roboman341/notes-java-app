package my.notes.notesApp.web.controller;

import jakarta.validation.Valid;
import my.notes.notesApp.biz.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/test")
public class SecurityController {
    @Autowired
    CustomerService customerService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "This is Home");
        return "placeholder";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("message", "This is Admin");
        return "placeholder";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_STUDENT')")
    @GetMapping("/student")
    public String student(Model model){
        model.addAttribute("message", "This is Student");
        return "placeholder";
    }

    @PostMapping("/create")
    public String createNewCustomer(
            @Valid @RequestParam("username") String username,
            @Valid @RequestParam("email") String email,
            @Valid @RequestParam("password") String password,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }
        UserDetails newUser = customerService.createNewCustomer(username, email, password);
        return "redirect:/login";
    }
}
