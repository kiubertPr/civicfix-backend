package com.civicfix.tfg.rest.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.civicfix.tfg.model.services.EmailService;
import com.civicfix.tfg.rest.dtos.ContactFormDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/mail")
public class MailController {

    private final EmailService emailService;

    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/contact")
    public ResponseEntity<Map<String, String>> sendContactEmail(@Valid @RequestBody ContactFormDto contactForm) {
        Map<String, String> response = new HashMap<>();
        
        try {
            emailService.sendContactEmail(contactForm);
            response.put("message", "Mensaje enviado correctamente");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("globalError", "Error al enviar el mensaje: " + e.getMessage()));
        }

    }
}
