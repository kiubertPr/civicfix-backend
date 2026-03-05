package com.civicfix.tfg.model.services.exceptions;

public class DuplicateUsernameException extends Exception {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
