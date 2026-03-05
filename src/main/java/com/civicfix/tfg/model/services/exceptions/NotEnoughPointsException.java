package com.civicfix.tfg.model.services.exceptions;

public class NotEnoughPointsException extends Exception {
    public NotEnoughPointsException() {
        super("Not enough points to perform this action.");
    }
}
