package com.civicfix.tfg.model.services.exceptions;

public class DuplicateEmailException extends Exception {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
