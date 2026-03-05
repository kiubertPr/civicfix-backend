package com.civicfix.tfg.model.services.exceptions;

public class IncorrectLoginException extends Exception {
    public IncorrectLoginException() {
        super("Incorrect username or password.");
    }
    
}
