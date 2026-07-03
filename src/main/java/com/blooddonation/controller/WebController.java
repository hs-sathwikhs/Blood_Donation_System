package com.blooddonation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/organiser")
    public String organiserDashboard() {
        return "organiser-dashboard";
    }

    @GetMapping("/donor")
    public String donorDashboard() {
        return "donor-dashboard";
    }

    @GetMapping("/hospital")
    public String hospitalDashboard() {
        return "hospital-dashboard";
    }

    @GetMapping("/inventory")
    public String inventoryDashboard() {
        return "inventory-dashboard";
    }
}
