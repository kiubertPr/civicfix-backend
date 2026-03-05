package com.civicfix.tfg.model.services.impls;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.civicfix.tfg.model.entities.Survey;
import com.civicfix.tfg.model.entities.SurveyResponse;
import com.civicfix.tfg.model.entities.PointTransaction;
import com.civicfix.tfg.model.entities.daos.SurveyDao;
import com.civicfix.tfg.model.entities.daos.SurveyResponseDao;
import com.civicfix.tfg.model.services.PointTransactionService;
import com.civicfix.tfg.model.services.SurveyService;
import com.civicfix.tfg.model.services.exceptions.SurveyEndedException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SurveyServiceImpl implements SurveyService{

    private final SurveyDao surveyDao;
    private final SurveyResponseDao surveyResponseDao;
    private final PointTransactionService pointTransactionService;

    public SurveyServiceImpl(SurveyDao surveyDao, SurveyResponseDao surveyResponseDao, PointTransactionService pointTransactionService) {
        this.surveyDao = surveyDao;
        this.surveyResponseDao = surveyResponseDao;
        this.pointTransactionService = pointTransactionService;
    }

    @Override
    public Survey createSurvey(Survey survey) {
        surveyDao.save(survey);
        return survey;
    }

    @Override
    public Survey addAnswer(SurveyResponse surveyResponse) throws InstanceNotFoundException, SurveyEndedException {

        Optional<Survey> opSurvey = surveyDao.findById(surveyResponse.getSurveyId());
        
        if (!opSurvey.isPresent())
            throw new InstanceNotFoundException("project.entities.survey", surveyResponse.getSurveyId());

        Survey survey = opSurvey.get();
        
        if(survey.getEndDateTime() != null && survey.getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new SurveyEndedException();
        }
        
        pointTransactionService.createPointTransaction(
            surveyResponse.getUserId(),
            PointTransaction.TransactionType.PARTICIPATE_SURVEY,
            PointTransaction.EntityType.SURVEY,
            survey.getId()
            );
            
        surveyResponseDao.save(surveyResponse);

        return survey;

    }

    @Override
    public Survey getSurveyById(Long id) throws InstanceNotFoundException {
        Optional<Survey> opSurvey = surveyDao.findById(id);

        if (!opSurvey.isPresent()) {
            throw new InstanceNotFoundException("project.entities.survey",  id);
        }

        return opSurvey.get();
        
    }

    @Override
    public Page<Survey> getAllSurveys(Pageable pageable) {
        return surveyDao.findAll(pageable);
    }

    @Override
    public Map<Long, Map<Integer, Long>> getVoteCountsBySurveyIds(List<Long> surveyIds) {
        List<SurveyResponseDao.OptionVoteSummary> voteCounts = surveyResponseDao.findOptionVoteCountsBySurveyIds(surveyIds);
        
        return voteCounts.stream().collect(
            java.util.stream.Collectors.groupingBy(
                SurveyResponseDao.OptionVoteSummary::getSurveyId,
                java.util.stream.Collectors.toMap(
                    SurveyResponseDao.OptionVoteSummary::getOptionId,
                    SurveyResponseDao.OptionVoteSummary::getVoteCount
                )
            )
        );
    }

    @Override
    public Map<Long, List<Integer>> getUserVotesBySurveyIds(Long userId, List<Long> surveyIds) {
        List<SurveyResponse> responses = surveyResponseDao.findByUserIdAndSurveyIds(userId, surveyIds);
        
        return responses.stream().collect(
            java.util.stream.Collectors.toMap(
                SurveyResponse::getSurveyId,
                SurveyResponse::getSelectedOptions
            )
        );
    }
}
