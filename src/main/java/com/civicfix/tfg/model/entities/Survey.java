package com.civicfix.tfg.model.entities;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

@Entity
@Table(name = "Surveys")
public class Survey {
    
    public enum SurveyType {
        SELECCION,
        MULTIPLE,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime endDateTime;

    @Enumerated(EnumType.ORDINAL)
    private SurveyType type;

    @ElementCollection
    @CollectionTable(
        name = "survey_options",
        joinColumns = @JoinColumn(name = "surveyId")
    )
    @MapKeyColumn(name = "optionId")
    @Column(name = "optionText")
    private Map<Integer, String> options;

    public Survey() {
    }

    public Survey(String question, SurveyType type, Map<Integer, String> options, LocalDateTime endDateTime) {
        this.question = question;
        this.type = type;
        this.options = options;
        this.endDateTime = endDateTime;
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

    public SurveyType getType() {
        return type;
    }

    public void setType(SurveyType type) {
        this.type = type;
    }

    public Map<Integer, String> getOptions() {
        return options;
    }

    public void setOptions(Map<Integer, String> options) {
        this.options = options;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    
    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

}
