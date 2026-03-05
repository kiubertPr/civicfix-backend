package com.civicfix.tfg.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.Survey;
import com.civicfix.tfg.model.entities.SurveyResponse;
import com.civicfix.tfg.model.services.exceptions.SurveyEndedException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SurveyServiceTest {

    @Autowired
    private SurveyService surveyService;

    @MockitoBean
    private PointTransactionService pointTransactionService;

    @Test
    public void testCreateSurvey() {
        Survey survey = new Survey("Test Question", Survey.SurveyType.SELECCION, Map.of(1, "Option 1", 2, "Option 2"), LocalDateTime.now().plusDays(7));
        Survey createdSurvey = surveyService.createSurvey(survey);
        assertNotNull(createdSurvey);
        assertEquals("Test Question", createdSurvey.getQuestion());
        assertEquals(Survey.SurveyType.SELECCION, createdSurvey.getType());
        assertEquals(2, createdSurvey.getOptions().size());
    }

    @Test
    public void addAnswerAndGetVoteCountsBySurveyIdsTest() throws InstanceNotFoundException, SurveyEndedException {
        Survey survey = new Survey("Test Question", Survey.SurveyType.SELECCION, Map.of(1, "Option 1", 2, "Option 2"), LocalDateTime.now().plusDays(7));
        Survey createdSurvey = surveyService.createSurvey(survey);

        SurveyResponse response = new SurveyResponse(createdSurvey.getId(), 1L, List.of(1));
        Survey updatedSurvey = surveyService.addAnswer(response);

        assertNotNull(updatedSurvey);
        assertEquals(createdSurvey.getId(), updatedSurvey.getId());

        Map<Long, Map<Integer, Long>> voteCounts = surveyService.getVoteCountsBySurveyIds(List.of(createdSurvey.getId()));
        assertNotNull(voteCounts);
        assertEquals(1, voteCounts.size());
        assertTrue(voteCounts.containsKey(createdSurvey.getId()));

        Map<Integer, Long> optionsVotes = voteCounts.get(createdSurvey.getId());
        assertEquals(Long.valueOf(1), optionsVotes.get(1));
    }

    @Test(expected = InstanceNotFoundException.class)
    public void testAddAnswerToNonExistentSurvey() throws InstanceNotFoundException, SurveyEndedException {
        SurveyResponse response = new SurveyResponse(999L, 1L, List.of(1));
        surveyService.addAnswer(response);
    }

    @Test
    public void getSurveyByIdTest() throws InstanceNotFoundException {
        Survey survey = new Survey("Test Question", Survey.SurveyType.SELECCION, Map.of(1, "Option 1", 2, "Option 2"), LocalDateTime.now().plusDays(7));
        Survey createdSurvey = surveyService.createSurvey(survey);

        Survey foundSurvey = surveyService.getSurveyById(createdSurvey.getId());
        assertNotNull(foundSurvey);
        assertEquals(createdSurvey.getId(), foundSurvey.getId());
    }

    @Test(expected = InstanceNotFoundException.class)
    public void getSurveyByIdNotFoundTest() throws InstanceNotFoundException {
        surveyService.getSurveyById(999L);
    }

    @Test
    public void getAllSurveysTest() {
        Survey survey1 = new Survey("Question 1", Survey.SurveyType.SELECCION, Map.of(1, "Option 1", 2, "Option 2"), LocalDateTime.now().plusDays(7));
        Survey survey2 = new Survey("Question 2", Survey.SurveyType.SELECCION, Map.of(3, "Option 3", 4, "Option 4"), LocalDateTime.now().plusDays(7));
        surveyService.createSurvey(survey1);
        surveyService.createSurvey(survey2);

        Page<Survey> surveysPage = surveyService.getAllSurveys(PageRequest.of(0, 10));
        assertNotNull(surveysPage);
    }

    @Test
    public void getUserVotesBySurveyIdsTest() throws InstanceNotFoundException, SurveyEndedException {
        Survey survey = new Survey("Test Question", Survey.SurveyType.SELECCION, Map.of(1, "Option 1", 2, "Option 2"), LocalDateTime.now().plusDays(7));
        Survey createdSurvey = surveyService.createSurvey(survey);

        SurveyResponse response = new SurveyResponse(createdSurvey.getId(), 1L, List.of(1));
        surveyService.addAnswer(response);

        Map<Long, List<Integer>> userVotes = surveyService.getUserVotesBySurveyIds(1L, List.of(createdSurvey.getId()));
        assertNotNull(userVotes);
        assertEquals(1, userVotes.size());
        assertTrue(userVotes.containsKey(createdSurvey.getId()));
        assertEquals(List.of(1), userVotes.get(createdSurvey.getId()));
    }

    @Test(expected = SurveyEndedException.class)
    public void testAddAnswerToEndedSurvey() throws InstanceNotFoundException, SurveyEndedException {
        Survey survey = new Survey("Test Question", Survey.SurveyType.SELECCION, Map.of(1, "Option 1", 2, "Option 2"), LocalDateTime.now().minusDays(1));
        Survey createdSurvey = surveyService.createSurvey(survey);

        SurveyResponse response = new SurveyResponse(createdSurvey.getId(), 1L, List.of(1));
        surveyService.addAnswer(response);
    }

}
