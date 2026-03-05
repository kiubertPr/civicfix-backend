package com.civicfix.tfg.model.entities;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SurveyResponses")
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long surveyId;

    private Long userId;

    @ElementCollection
    @CollectionTable(
        name = "survey_response_options",
        joinColumns = @jakarta.persistence.JoinColumn(name = "surveyResponseId")
    )
    @Column(name = "optionId")
    private List<Integer> selectedOptions;

    public SurveyResponse() {
    }

    public SurveyResponse(Long surveyId, Long userId, List<Integer> selectedOptions) {
        this.surveyId = surveyId;
        this.userId = userId;
        this.selectedOptions = selectedOptions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Integer> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<Integer> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }


    
    
}
