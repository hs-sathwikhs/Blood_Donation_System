package com.blooddonation;

import com.blooddonation.repository.CampRepository;
import com.blooddonation.repository.DonatorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:authTests;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class AuthFlowTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired DonatorRepository donorRepo;
    @Autowired CampRepository campRepo;

    String login(String role, String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login/" + role).contentType("application/json")
                .content(mapper.writeValueAsString(java.util.Map.of("email", email, "password", password))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(body).get("token").asText();
    }

    @Test
    void donorCannotChangeInventoryOrReadAnotherAccount() throws Exception {
        String token = login("donor", "aarav.patel@gmail.com", "pass123");
        var other = donorRepo.findByEmail("sneha.reddy@gmail.com").orElseThrow();
        mvc.perform(post("/api/inventory/stock/add").header("Authorization", "Bearer " + token)
                .contentType("application/json").content("{\"bloodType\":\"A+\",\"quantity\":\"1\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/donors/" + other.getDonatorId() + "/donations")
                .header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    }

    @Test
    void campRegistrationsRequireTheOwner() throws Exception {
        var camp = campRepo.findAll().stream().filter(c -> c.getName().equals("Indiranagar Blood Drive")).findFirst().orElseThrow();
        mvc.perform(get("/api/camps/" + camp.getCampId() + "/registrations")).andExpect(status().isUnauthorized());
        String token = login("organiser", "rahul.d@bloodaid.org", "password123");
        mvc.perform(get("/api/camps/" + camp.getCampId() + "/registrations")
                .header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    }

    @Test
    void invalidRegistrationAndTokensReturnJson() throws Exception {
        mvc.perform(post("/api/auth/register/donor").contentType("application/json")
                .content("{\"email\":\"bad\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists());
        mvc.perform(get("/api/inventory/stock").header("Authorization", "Bearer broken"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message").exists());
    }
}
