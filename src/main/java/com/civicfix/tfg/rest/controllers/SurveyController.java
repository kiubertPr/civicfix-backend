package com.civicfix.tfg.rest.controllers;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.Survey;
import com.civicfix.tfg.model.entities.SurveyResponse;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.entities.daos.SurveyDao;
import com.civicfix.tfg.model.services.PermissionChecker;
import com.civicfix.tfg.model.services.SurveyService;
import com.civicfix.tfg.model.services.UserService;
import com.civicfix.tfg.model.services.exceptions.PermissionException;
import com.civicfix.tfg.model.services.exceptions.SurveyEndedException;
import com.civicfix.tfg.rest.common.ErrorsDto;
import com.civicfix.tfg.rest.common.JwtInfo;
import com.civicfix.tfg.rest.dtos.PageDto;
import com.civicfix.tfg.rest.dtos.SurveyDto;
import com.civicfix.tfg.rest.dtos.conversors.SurveyConversor;
import com.civicfix.tfg.rest.dtos.request.SurveyRequestDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/surveys")
public class SurveyController {
    
    private static final String INSTANCE_NOT_FOUND = "project.exceptions.InstanceNotFoundException";
    private static final String PERMISION_EXCEPTION = "project.exceptions.PermissionException";
    private static final String SURVEY_ENDED_EXCEPTION = "project.exceptions.SurveyEndedException";

    private final MessageSource messageSource;
    private final PermissionChecker permissionChecker;
    private final SurveyService surveyService;
    private final UserService userService;
    private final SurveyDao surveyDao;

    public SurveyController(MessageSource messageSource, PermissionChecker permissionChecker, SurveyService surveyService, UserService userService, SurveyDao surveyDao) {
        this.messageSource = messageSource;
        this.permissionChecker = permissionChecker;
        this.surveyService = surveyService;
        this.userService = userService;
        this.surveyDao = surveyDao;
    }

    @ExceptionHandler(InstanceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorsDto handleInstanceNotFoundException(InstanceNotFoundException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(INSTANCE_NOT_FOUND, null,
				INSTANCE_NOT_FOUND, locale);

		return new ErrorsDto(errorMessage);

    }

    @ExceptionHandler(PermissionException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorsDto handlePermissionException(PermissionException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(PERMISION_EXCEPTION, null,
				PERMISION_EXCEPTION, locale);

		return new ErrorsDto(errorMessage);

    }

    @ExceptionHandler(SurveyEndedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorsDto handleSurveyEndedException(SurveyEndedException exception, Locale locale) {

        String errorMessage = messageSource.getMessage(SURVEY_ENDED_EXCEPTION, null,
                SURVEY_ENDED_EXCEPTION, locale);

        return new ErrorsDto(errorMessage);
    }

    @PostMapping("/create")
    public ResponseEntity<SurveyDto> createSurvey(
        @AuthenticationPrincipal JwtInfo jwtInfo, 
        @RequestBody SurveyRequestDto surveyRequestDto) throws PermissionException, InstanceNotFoundException {
        
        User  user = permissionChecker.checkUser(jwtInfo.getUserId());

        if (user.getRole() != User.Role.ADMIN)
            throw new PermissionException();
        
        Survey survey = new Survey();
        
        survey.setQuestion(surveyRequestDto.getQuestion());
        survey.setType(Survey.SurveyType.values()[surveyRequestDto.getType()]);

        if (surveyRequestDto.getEndDateTime() != null && !surveyRequestDto.getEndDateTime().isEmpty()) {
            Instant instant = Instant.parse(surveyRequestDto.getEndDateTime());
            survey.setEndDateTime(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        }


        HashMap<Integer, String> options = new HashMap<>();

        for (int i = 0; i < surveyRequestDto.getOptions().size(); i++) {
            options.put(i, surveyRequestDto.getOptions().get(i));
        }

        survey.setOptions(options);

        surveyService.createSurvey(survey);

        return ResponseEntity.ok(SurveyConversor.toSurveyDto(survey, null, null, null, null));
    }

    @GetMapping("/list")
    public ResponseEntity<PageDto<SurveyDto>> getSurveysPage(
        @AuthenticationPrincipal JwtInfo jwtInfo, 
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "5") int size, 
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy, 
        @RequestParam(required = false, defaultValue = "desc") String sortDirection
    ) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), "createdAt");
        Pageable pageRequest = PageRequest.of(page, size, sort);

        Page<Survey> surveysPage = surveyService.getAllSurveys(pageRequest);

        List<Long> surveyIds = surveyDao.findAll().stream()
            .map(Survey::getId).toList();

        Map<Long, Map<Integer, Long>> voteTotalsMap = surveyService.getVoteCountsBySurveyIds(surveyIds);

        // Obtener el número de respuestas por encuesta
        Map<Long, Long> surveyResponseCounts = surveyIds.stream()
            .collect(Collectors.toMap(
            id -> id,
            id -> voteTotalsMap.getOrDefault(id, Collections.emptyMap())
                .values().stream().mapToLong(Long::longValue).sum()
            ));

        Map<Long, List<Integer>> userVoteMap = jwtInfo != null ? surveyService.getUserVotesBySurveyIds(jwtInfo.getUserId(), surveyIds) : new HashMap<>();

        Integer totalUsers = userService.countUsersByRole(User.Role.USER);

        Page<SurveyDto> surveyDtosPage;

        if(sortBy.equalsIgnoreCase("answered")) {
                List<SurveyDto> allDtos = new ArrayList<>(SurveyConversor.toSurveyDto(surveyDao.findAll(), voteTotalsMap, userVoteMap, surveyResponseCounts, totalUsers));
                allDtos.sort((a, b) -> {
                    boolean answeredA = userVoteMap.containsKey(a.getId());
                    boolean answeredB = userVoteMap.containsKey(b.getId());
                    return Boolean.compare(!answeredA, !answeredB); // answered first
                });
                if (sortDirection.equalsIgnoreCase("asc")) {
                    Collections.reverse(allDtos);
                }
                int start = page * size;
                int end = Math.min(start + size, allDtos.size());
                List<SurveyDto> pageContent = allDtos.subList(start, end);
                surveyDtosPage = new PageImpl<>(pageContent, pageRequest, allDtos.size());
            } else {
                surveyDtosPage = SurveyConversor.toSurveyDto(surveysPage, voteTotalsMap, userVoteMap, surveyResponseCounts, totalUsers);
            }

        PageDto<SurveyDto> response = new PageDto<>(
            surveyDtosPage.getContent(),
            surveyDtosPage.getNumber(),
            surveyDtosPage.getSize(),
            surveyDtosPage.getTotalElements(),
            surveyDtosPage.getTotalPages(),
            surveyDtosPage.isLast(),
            surveyDtosPage.isFirst(),
            surveyDtosPage.isEmpty()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/answer/{surveyId}")
    public ResponseEntity<SurveyDto> surveyAnswer(
        @AuthenticationPrincipal JwtInfo jwtInfo,
        @PathVariable Long surveyId,
        @RequestBody List<Integer> selectedOptions) throws InstanceNotFoundException, SurveyEndedException {

        User user = permissionChecker.checkUser(jwtInfo.getUserId());

        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setSurveyId(surveyId);
        surveyResponse.setUserId(user.getId());
        surveyResponse.setSelectedOptions(selectedOptions);

        Survey updatedSurvey = surveyService.addAnswer(surveyResponse);

        Map<Long, Map<Integer, Long>> voteTotalsMap = surveyService.getVoteCountsBySurveyIds(List.of(surveyId));
        Map<Integer, Long> voteTotalsMapForSurvey = voteTotalsMap.getOrDefault(updatedSurvey.getId(), new HashMap<>());
        List<Integer> voteTotals = updatedSurvey.getOptions().keySet().stream().sorted()
            .map(optionId -> voteTotalsMapForSurvey.getOrDefault(optionId, 0L).intValue()).toList();
        
        Integer totalUsers = userService.countUsersByRole(User.Role.USER);

        Long voteTotalsSum = voteTotals.stream().mapToLong(Integer::longValue).sum();

        SurveyDto entity = SurveyConversor.toSurveyDto(updatedSurvey, voteTotals, selectedOptions, voteTotalsSum, totalUsers);

        return ResponseEntity.ok(entity);
    }
    
}
