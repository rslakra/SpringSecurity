package com.rslakra.securityfilterchain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ResourceController {

    @GetMapping({"/", "/index"})
    public String indexPage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginEndpoint() {
        return "views/login";
    }

    @GetMapping("/register")
    public String registerEndpoint() {
        return "views/register";
    }

    @PostMapping("/register")
    public String registerSubmit() {
        // Demo: Just redirect to login with success message
        return "redirect:/login?registered=true";
    }

    @GetMapping("/registration")
    public String registrationEndpoint() {
        return "views/registration";
    }

    @PostMapping("/registration")
    public String registrationSubmit() {
        // Demo: Just redirect with success
        return "redirect:/registration?success=true";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "views/admin";
    }

    @GetMapping("/about-us")
    public String aboutUsEndpoint() {
        return "views/about-us";
    }

    @GetMapping("/contact-us")
    public String contactUsEndpoint() {
        return "views/contact-us";
    }

    @GetMapping("/user")
    public String userEndpoint() {
        return "views/home";
    }

    @GetMapping("/all")
    public String allRolesEndpoint() {
        return "views/allRoles";
    }

    @DeleteMapping("/delete")
    public String deleteEndpoint(@RequestBody String message, Model model) {
        model.addAttribute("message", "I've deleted " + message);
        return "views/delete";
    }
}
