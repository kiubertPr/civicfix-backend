package com.civicfix.tfg.model.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.Survey;
import com.civicfix.tfg.model.entities.SurveyResponse;
import com.civicfix.tfg.model.services.exceptions.SurveyEndedException;

public interface SurveyService {
    
    public Survey createSurvey(Survey survey);

    public Survey addAnswer(SurveyResponse surveyResponse)  throws InstanceNotFoundException, SurveyEndedException;

    public Survey getSurveyById(Long id) throws InstanceNotFoundException;

    public Page<Survey> getAllSurveys(Pageable pageable);

    public Map<Long, Map<Integer, Long>> getVoteCountsBySurveyIds(List<Long> surveyIds);

    public Map<Long, List<Integer>> getUserVotesBySurveyIds(Long userId, List<Long> surveyIds);

}
