package com.civicfix.tfg.model.services;

import java.security.GeneralSecurityException;

import com.civicfix.tfg.model.entities.User;

import io.jsonwebtoken.io.IOException;

public interface GoogleOAuth2Service {
    
    public User processGoogleUser(String googleToken) throws IllegalArgumentException, GeneralSecurityException, IOException, java.io.IOException;
}
