package com.civicfix.tfg.model.services.exceptions;

public class SurveyEndedException extends Exception {
    public SurveyEndedException() {
        super("Cannot add answer to survey because it has ended.");
    }
}
