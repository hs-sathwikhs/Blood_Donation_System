package com.blooddonation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class WebController {
    @GetMapping("/")
    public String index() { return "index"; }

    @GetMapping({"/donor", "/organiser", "/hospital", "/inventory"})
    public String dashboard(HttpServletRequest req, Model model) {
        String role = req.getRequestURI().substring(1);
        if ("inventory".equals(role)) role = "admin";
        String title = switch (role) {
            case "donor" -> "Give, when you're ready.";
            case "organiser" -> "Bring people together.";
            case "hospital" -> "Keep care moving.";
            default -> "Every unit counts.";
        };
        String note = switch (role) {
            case "donor" -> "Find a camp and keep track of your donations.";
            case "organiser" -> "Plan your camps, manage places and follow each visit.";
            case "hospital" -> "Check available blood and follow your requests.";
            default -> "Follow usable stock, incoming batches and hospital requests.";
        };
        model.addAttribute("role", role); model.addAttribute("title", title); model.addAttribute("note", note);
        return "dashboard";
    }
}
