package com.civicfix.tfg.rest.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactFormDto {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    @NotBlank(message = "El asunto es requerido")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    private String subject;
    
    @NotBlank(message = "El mensaje es requerido")
    @Size(max = 2000, message = "El mensaje no puede exceder 2000 caracteres")
    private String message;
    
    // Constructores
    public ContactFormDto() {}
    
    public ContactFormDto(String name, String email, String subject, String message) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }
    
    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}