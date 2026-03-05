package com.civicfix.tfg.rest.dtos.conversors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.civicfix.tfg.model.entities.Survey;
import com.civicfix.tfg.rest.dtos.SurveyDto;

public class SurveyConversor {

    private SurveyConversor() {
        // Private constructor to prevent instantiation
    }

    public static final SurveyDto toSurveyDto(Survey survey, List<Integer> voteTotals, List<Integer> userVote, Long surveyResponseCounts, Integer totalUsers) {
        return new SurveyDto(
            survey.getId(), 
            survey.getQuestion(), 
            survey.getCreatedAt(), 
            survey.getType().ordinal(), 
            orderedOptions(survey), 
            survey.getEndDateTime() != null ? survey.getEndDateTime().toString() : null,
            voteTotals, 
            userVote, 
            surveyResponseCounts != null ? surveyResponseCounts : 0L,
            totalUsers != null ? (float) surveyResponseCounts / totalUsers * 100 : 0f
        );
    }

    public static final Page<SurveyDto> toSurveyDto(Page<Survey> surveys, Map<Long, Map<Integer, Long>> voteTotalsMap, Map<Long, List<Integer>> userVoteMap, Map<Long, Long> surveyResponseCounts, Integer totalUsers) {
        return surveys.map(survey -> {
            Map<Integer, Long> voteTotalsMapForSurvey = voteTotalsMap.getOrDefault(survey.getId(), new HashMap<>());
            List<Integer> voteTotals = survey.getOptions().keySet().stream().sorted()
                .map(optionId -> voteTotalsMapForSurvey.getOrDefault(optionId, 0L).intValue())
                .toList();
            List<Integer> userVote = userVoteMap.getOrDefault(survey.getId(), new ArrayList<>());
            Long responseCount = surveyResponseCounts.get(survey.getId());
            return toSurveyDto(survey, voteTotals, userVote, responseCount != null ? responseCount : 0L, totalUsers != null ? totalUsers : 0);
        });
    }

    public static final List<SurveyDto> toSurveyDto(List<Survey> surveys, Map<Long, Map<Integer, Long>> voteTotalsMap, Map<Long, List<Integer>> userVoteMap, Map<Long, Long> surveyResponseCounts, Integer totalUsers) {
        return surveys.stream().map(survey -> {
            Map<Integer, Long> voteTotalsMapForSurvey = voteTotalsMap.getOrDefault(survey.getId(), new HashMap<>());
            List<Integer> voteTotals = survey.getOptions().keySet().stream().sorted()
                .map(optionId -> voteTotalsMapForSurvey.getOrDefault(optionId, 0L).intValue())
                .toList();
            List<Integer> userVote = userVoteMap.getOrDefault(survey.getId(), new ArrayList<>());
            Long responseCount = surveyResponseCounts.get(survey.getId());
            return toSurveyDto(survey, voteTotals, userVote, responseCount != null ? responseCount : 0L, totalUsers != null ? totalUsers : 0);
        }).toList();
    }

    private static List<String> orderedOptions(Survey survey) {
    return survey.getOptions().entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(Map.Entry::getValue)
        .toList();
}
    
}
