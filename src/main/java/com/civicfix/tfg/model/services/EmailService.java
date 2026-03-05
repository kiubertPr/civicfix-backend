package com.civicfix.tfg.model.services;

import jakarta.mail.MessagingException;

import com.civicfix.tfg.rest.dtos.ContactFormDto;

public interface EmailService {

    public void sendContactEmail(ContactFormDto contactForm) throws MessagingException;
}