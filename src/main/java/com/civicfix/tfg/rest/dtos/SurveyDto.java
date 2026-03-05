package com.civicfix.tfg.rest.dtos;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyDto {
    
    private Long id;
    private String question;
    private LocalDateTime createdAt;
    private int type;
    private List<String> options;
    private String endDateTime;
    private List<Integer> voteTotals;
    private List<Integer> userVote;
    private Long responseCount;
    private float participationRate;

    public SurveyDto() {
    }

    public SurveyDto(Long id, String question, LocalDateTime createdAt, int type, List<String> options, String endDateTime, List<Integer> voteTotals, List<Integer> userVote, Long responseCount, float participationRate) {
        this.id = id;
        this.question = question;
        this.createdAt = createdAt;
        this.type = type;
        this.options = options;
        this.endDateTime = endDateTime;
        this.voteTotals = voteTotals;
        this.userVote = userVote;
        this.responseCount = responseCount;
        this.participationRate = participationRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public List<Integer> getVoteTotals() {
        return voteTotals;
    }

    public void setVoteTotals(List<Integer> voteTotals) {
        this.voteTotals = voteTotals;
    }

    public List<Integer> getUserVote() {
        return userVote;
    }

    public void setUserVote(List<Integer> userVote) {
        this.userVote = userVote;
    }

    public Long getResponseCount() {
        return responseCount;
    }

    public void setResponseCount(Long responseCount) {
        this.responseCount = responseCount;
    }

    public float getParticipationRate() {
        return participationRate;
    }

    public void setParticipationRate(float participationRate) {
        this.participationRate = participationRate;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }
}
