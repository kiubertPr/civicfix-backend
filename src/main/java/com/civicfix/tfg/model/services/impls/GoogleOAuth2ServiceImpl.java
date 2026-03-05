package com.civicfix.tfg.model.services.impls;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.entities.daos.UserDao;
import com.civicfix.tfg.model.services.GoogleOAuth2Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class GoogleOAuth2ServiceImpl implements GoogleOAuth2Service {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final String googleClientId;

    public GoogleOAuth2ServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder, @Value("${spring.security.oauth2.client.registration.google.client-id}") String googleClientId) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.googleClientId = googleClientId;
    }

    
    public User processGoogleUser(String googleToken) throws IllegalArgumentException, GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifyGoogleToken(googleToken);
        
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            if (firstName == null) {
                firstName = "";
            }
            String lastName = (String) payload.get("family_name");
            if (lastName == null) {
                lastName = "";
            }

            String avatar = (String) payload.get("picture");
            if (avatar == null) {
                avatar = "";
            }
            
            User existingUser = userDao.findByGoogleIdOrEmail(googleId, email);
            
            if (existingUser != null) {
                if (existingUser.getGoogleId() == null) {
                    existingUser.setGoogleId(googleId);
                    existingUser.setProvider(User.Provider.GOOGLE);
                    userDao.save(existingUser);
                }
                return existingUser;
            } else {
                User newUser = new User();
                newUser.setGoogleId(googleId);
                newUser.setEmail(email);
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setAvatar(avatar);
                newUser.setProvider(User.Provider.GOOGLE);
                newUser.setUsername(email);
                newUser.setRole(User.Role.USER);

                String randomPassword = UUID.randomUUID().toString(); 
                newUser.setPassword(passwordEncoder.encode(randomPassword)); 
                
                return userDao.save(newUser);
            }
        }
        throw new IllegalArgumentException("Invalid Google token");
    }
    
    private GoogleIdToken verifyGoogleToken(String tokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(), 
            new GsonFactory())
            .setAudience(Collections.singletonList(googleClientId))
            .build();
            
        return verifier.verify(tokenString);
    }
}