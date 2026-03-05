package com.civicfix.tfg.rest.controllers;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.PointTransaction;
import com.civicfix.tfg.model.services.PermissionChecker;
import com.civicfix.tfg.model.services.PointTransactionService;
import com.civicfix.tfg.model.services.exceptions.NotEnoughPointsException;
import com.civicfix.tfg.rest.common.ErrorsDto;
import com.civicfix.tfg.rest.common.JwtInfo;
import com.civicfix.tfg.rest.dtos.PageDto;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/pointSystem")
public class PointSystemController {

    private static final String INSTANCE_NOT_FOUND = "project.exceptions.InstanceNotFoundException";
    private static final String NOT_ENOUGH_POINTS = "project.exceptions.NotEnoughPointsException";

    private final PointTransactionService pointTransactionService;
    private final PermissionChecker permissionChecker;
    private final MessageSource messageSource;

    public PointSystemController(PointTransactionService pointTransactionService, PermissionChecker permissionChecker, MessageSource messageSource) {
        this.pointTransactionService = pointTransactionService;
        this.permissionChecker = permissionChecker;
        this.messageSource = messageSource;
    }

    @ExceptionHandler(InstanceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorsDto handleInstanceNotFoundException(InstanceNotFoundException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(INSTANCE_NOT_FOUND, null,
				INSTANCE_NOT_FOUND, locale);

		return new ErrorsDto(errorMessage);

    }
    @ExceptionHandler(NotEnoughPointsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorsDto handleNotEnoughPointsException(NotEnoughPointsException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(NOT_ENOUGH_POINTS, null,
				NOT_ENOUGH_POINTS, locale);

		return new ErrorsDto(errorMessage);

    }

    @GetMapping("/history")
    public ResponseEntity<PageDto<PointTransaction>> getUserPointsHistory(
        @AuthenticationPrincipal JwtInfo jwtInfo,
        @RequestParam( defaultValue = "0") int page,
        @RequestParam( defaultValue = "10") int size, 
        @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
        @RequestParam(required = false, defaultValue = "desc") String sortDirection) throws InstanceNotFoundException {
        
        permissionChecker.checkUser(jwtInfo.getUserId());

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PointTransaction> pointTransactions = pointTransactionService.getPointTransactionsByUserId(jwtInfo.getUserId(), pageable);

        PageDto<PointTransaction> response = new PageDto<>(
            pointTransactions.getContent(),
            pointTransactions.getNumber(),
            pointTransactions.getSize(),
            pointTransactions.getTotalElements(),
            pointTransactions.getTotalPages(),
            pointTransactions.isLast(),
            pointTransactions.isFirst(),
            pointTransactions.isEmpty()
        );

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/redeem")
    public ResponseEntity<Void> postMethodName(
        @AuthenticationPrincipal JwtInfo jwtInfo,
        @RequestBody Integer points
        ) throws InstanceNotFoundException, NotEnoughPointsException {
            
        permissionChecker.checkUser(jwtInfo.getUserId());

        pointTransactionService.changePoints(jwtInfo.getUserId(), points);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}
