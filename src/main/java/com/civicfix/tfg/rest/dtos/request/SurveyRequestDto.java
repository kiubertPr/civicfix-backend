package com.civicfix.tfg.rest.dtos.request;

import java.util.List;

public class SurveyRequestDto {
    
    private String question;

    private int type;

    private List<String> options;

    private String endDateTime;

    public SurveyRequestDto() {
    }

    public SurveyRequestDto(String question, int type, List<String> options, String endDateTime) {
        this.question = question;
        this.type = type;
        this.options = options;
        this.endDateTime = endDateTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

}
