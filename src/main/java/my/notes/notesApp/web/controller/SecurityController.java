package my.notes.notesApp.web.controller;

import my.notes.notesApp.biz.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/student")
    public String student(Model model){
        model.addAttribute("message", "This is Student");
        return "placeholder";
    }

    @GetMapping("/admin")
    public String admin(Model model){
        model.addAttribute("message", "This is Admin");
        return "placeholder";
    }

    @PostMapping("/create")
    public String createNewCustomer(@RequestParam("username") String username,
                                    @RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    Model model) {
        UserDetails newUser = customerService.createNewCustomer(username, email, password);
        model.addAttribute("newUser", newUser);
        model.addAttribute("message", "New user has been created");
        return "placeholder";
    }
}
