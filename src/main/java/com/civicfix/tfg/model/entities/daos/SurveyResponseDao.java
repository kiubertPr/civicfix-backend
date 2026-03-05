package com.civicfix.tfg.model.entities.daos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.civicfix.tfg.model.entities.SurveyResponse;

public interface SurveyResponseDao extends JpaRepository<SurveyResponse, Long> {
    
    Optional<SurveyResponse> findByUserIdAndSurveyId(Long userId, Long surveyId);
    List<SurveyResponse> findByUserId(Long userId);
    List<SurveyResponse> findBySurveyId(Long surveyId);
    void deleteByUserIdAndSurveyId(Long userId, Long surveyId);

    public interface OptionVoteSummary {
        Long getSurveyId();
        Integer getOptionId();
        Long getVoteCount();
    }

    @Query("SELECT r.surveyId as surveyId, s as optionId, COUNT(s) as voteCount " +
       "FROM SurveyResponse r JOIN r.selectedOptions s " +
       "WHERE r.surveyId IN :surveyIds " +
       "GROUP BY r.surveyId, s")
    List<OptionVoteSummary> findOptionVoteCountsBySurveyIds(@Param("surveyIds") List<Long> surveyIds);

    @Query("SELECT r FROM SurveyResponse r WHERE r.userId = :userId AND r.surveyId IN :surveyIds")
    List<SurveyResponse> findByUserIdAndSurveyIds(Long userId, List<Long> surveyIds);

}