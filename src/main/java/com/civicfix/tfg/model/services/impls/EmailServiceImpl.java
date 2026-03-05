package com.civicfix.tfg.model.services.impls;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.civicfix.tfg.model.services.EmailService;
import com.civicfix.tfg.rest.dtos.ContactFormDto;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String contactEmail;

    public EmailServiceImpl(JavaMailSender mailSender, @Value("${project.contact.email}") String contactEmail) {
        this.mailSender = mailSender;
        this.contactEmail = contactEmail;
    }
    
    public void sendContactEmail(ContactFormDto contactForm) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(contactEmail);
        helper.setSubject("Contacto desde la web: " + contactForm.getSubject());
        helper.setReplyTo(contactForm.getEmail());
        
        String htmlContent = buildEmailContent(contactForm);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
    
    private String buildEmailContent(ContactFormDto contactForm) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;">
                        Nuevo mensaje de contacto
                    </h2>
                    
                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <p><strong>Nombre:</strong> %s</p>
                        <p><strong>Email:</strong> %s</p>
                        <p><strong>Asunto:</strong> %s</p>
                    </div>
                    
                    <div style="background-color: #ffffff; padding: 15px; border-left: 4px solid #3498db;">
                        <h3 style="margin-top: 0; color: #2c3e50;">Mensaje:</h3>
                        <p style="white-space: pre-wrap;">%s</p>
                    </div>
                    
                    <div style="margin-top: 20px; padding: 10px; background-color: #e8f4f8; border-radius: 5px;">
                        <small style="color: #7f8c8d;">
                            Este mensaje fue enviado desde el formulario de contacto de tu sitio web.
                            Puedes responder directamente a este email.
                        </small>
                    </div>
                </div>
            </body>
            </html>
            """,
            contactForm.getName(),
            contactForm.getEmail(),
            contactForm.getSubject(),
            contactForm.getMessage()
        );
    }
}