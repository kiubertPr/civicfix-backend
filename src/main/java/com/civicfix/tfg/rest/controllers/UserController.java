package com.civicfix.tfg.rest.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.services.GoogleOAuth2Service;
import com.civicfix.tfg.model.services.PermissionChecker;
import com.civicfix.tfg.model.services.PointTransactionService;
import com.civicfix.tfg.model.services.UserService;
import com.civicfix.tfg.model.services.exceptions.DuplicateEmailException;
import com.civicfix.tfg.model.services.exceptions.DuplicateUsernameException;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.IncorrectLoginException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;
import com.civicfix.tfg.model.services.exceptions.PermissionException;
import com.civicfix.tfg.model.services.exceptions.UserDisableException;
import com.civicfix.tfg.rest.common.ErrorsDto;
import com.civicfix.tfg.rest.common.JwtGenerator;
import com.civicfix.tfg.rest.common.JwtInfo;
import com.civicfix.tfg.rest.dtos.AuthenticatedUserDto;
import com.civicfix.tfg.rest.dtos.UserDto;
import com.civicfix.tfg.rest.dtos.conversors.UserConversor;
import com.civicfix.tfg.rest.dtos.request.ChangePasswordParamsDto;
import com.civicfix.tfg.rest.dtos.request.LoginParamsDto;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/users")
public class UserController {
    
    private static final String INCORRECT_LOGIN_EXCEPTION_CODE = "project.exceptions.IncorrectLoginException";
	private static final String INSTANCE_NOT_FOUND = "project.exceptions.InstanceNotFoundException";
    private static final String PERMISION_EXCEPTION = "project.exceptions.PermissionException";
	private static final String DUPLICATED_USERNAME = "project.exceptions.DuplicateUsernameException";
	private static final String DUPLICATED_EMAIL = "project.exceptions.DuplicateEmailException";
	private static final String FORBIDDEN_FILE_TYPE_EXCEPTION = "project.exceptions.ForbiddenFileTypeException";
	private static final String MAX_FILE_SIZE_EXCEPTION = "project.exceptions.MaxFileSizeException";
	private static final String USER_DISABLE_EXCEPTION = "project.exceptions.UserDisableException";

    private final MessageSource messageSource;
    private final JwtGenerator jwtGenerator;
	private final PermissionChecker permissionChecker;
    private final UserService userService;
	private final PointTransactionService pointTransactionService;
	private final GoogleOAuth2Service googleOAuth2Service;

	public UserController(MessageSource messageSource ,UserService userService, JwtGenerator jwtGenerator, PermissionChecker permissionChecker, PointTransactionService pointTransactionService, GoogleOAuth2Service googleOAuth2Service) {
		this.messageSource = messageSource;
		this.userService = userService;
		this.jwtGenerator = jwtGenerator;
		this.permissionChecker = permissionChecker;
		this.pointTransactionService = pointTransactionService;
		this.googleOAuth2Service = googleOAuth2Service;
	}

    @ExceptionHandler(IncorrectLoginException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorsDto handleIncorrectLoginException(IncorrectLoginException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(INCORRECT_LOGIN_EXCEPTION_CODE, null,
				INCORRECT_LOGIN_EXCEPTION_CODE, locale);

		return new ErrorsDto(errorMessage);

    }

	@ExceptionHandler(UserDisableException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorsDto handleUserDisableException(UserDisableException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(USER_DISABLE_EXCEPTION, null,
				USER_DISABLE_EXCEPTION, locale);

		return new ErrorsDto(errorMessage);

	}

	@ExceptionHandler(ForbiddenFileTypeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorsDto handleForbiddenFileTypeException(ForbiddenFileTypeException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(FORBIDDEN_FILE_TYPE_EXCEPTION, null,
				FORBIDDEN_FILE_TYPE_EXCEPTION, locale);

		return new ErrorsDto(errorMessage);

	}

	@ExceptionHandler(MaxFileSizeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorsDto handleMaxFileSizeException(MaxFileSizeException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(MAX_FILE_SIZE_EXCEPTION, null,
				MAX_FILE_SIZE_EXCEPTION, locale);

		return new ErrorsDto(errorMessage);

	}

	@ExceptionHandler(DuplicateUsernameException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorsDto handleDuplicateUsernameException(DuplicateUsernameException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(DUPLICATED_USERNAME, null,
				DUPLICATED_USERNAME, locale);

		return new ErrorsDto(errorMessage);

    }

	@ExceptionHandler(DuplicateEmailException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorsDto handleDuplicateEmailException(DuplicateEmailException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(DUPLICATED_EMAIL, null,
				DUPLICATED_EMAIL, locale);

		return new ErrorsDto(errorMessage);

    }
	
    /**
	 * Sign up.
	 *
	 * @param userDto the user dto
	 * @return the response entity
     * @throws DuplicateUsernameException 
     * @throws DuplicateEmailException 
	 */
	@PostMapping("/signup")
	public ResponseEntity<AuthenticatedUserDto> signUp(
			@Validated({ UserDto.AllValidations.class }) @RequestBody UserDto userDto)
			throws DuplicateEmailException, DuplicateUsernameException {


		User user = UserConversor.toUser(userDto);

		userService.signUp(user);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId())
				.toUri();

		return ResponseEntity.created(location).body(UserConversor.toAuthenticatedUserDto(generateServiceToken(user), user, 0L));

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

    /**
	 * Login.
	 *
	 * @param params the params
	 * @return the authenticated user dto
	 * @throws IncorrectLoginException the incorrect login exception
     * @throws UserDisableException 
	 */
	@PostMapping("/login")
	public AuthenticatedUserDto login(@Validated @RequestBody LoginParamsDto params) throws IncorrectLoginException, InstanceNotFoundException, UserDisableException {

		User user = userService.login(params.getUsername(), params.getPassword());
		
		return UserConversor.toAuthenticatedUserDto(generateServiceToken(user), user, pointTransactionService.getTotalPointsByUserId(user.getId()));

	}

    /**
	 * Login from service token.
	 *
	 * @param userId       the user id
	 * @param serviceToken the service token
	 * @return the authenticated user dto
	 * @throws InstanceNotFoundException the instance not found exception
     * @throws UserDisableException 
	 */
	@PostMapping("/loginFromServiceToken")
	public AuthenticatedUserDto loginFromServiceToken(@RequestAttribute Long userId,
			@RequestAttribute String serviceToken) throws InstanceNotFoundException, UserDisableException {

		User user = userService.loginFromId(userId);

		return UserConversor.toAuthenticatedUserDto(serviceToken, user, pointTransactionService.getTotalPointsByUserId(user.getId()));

	}

    @PutMapping("/update")
	public ResponseEntity<UserDto> updateProfile(
			@AuthenticationPrincipal JwtInfo jwtInfo,
			@RequestPart String userDto,
			@RequestPart(required = false) String changePasswordParamsDto,
			@RequestPart(required = false) MultipartFile avatar
	)
			throws InstanceNotFoundException, IncorrectLoginException, IOException, ForbiddenFileTypeException, MaxFileSizeException, UserDisableException {

			permissionChecker.checkUser(jwtInfo.getUserId());
			UserDto updateData = new ObjectMapper().readValue(userDto, UserDto.class);
			
			// Solo procesar contraseña si se envía
			ChangePasswordParamsDto changePasswordParams = null;
			if (changePasswordParamsDto != null && !changePasswordParamsDto.trim().isEmpty()) {
				changePasswordParams = new ObjectMapper().readValue(changePasswordParamsDto, ChangePasswordParamsDto.class);
			}
			
			// Actualizar perfil
			User updatedUser = userService.update(jwtInfo.getUserId(), updateData, changePasswordParams, avatar);
			
			return ResponseEntity.ok(UserConversor.toUserDto(updatedUser, pointTransactionService.getTotalPointsByUserId(updatedUser.getId())));
	}

    /**
	 * Generate service token.
	 *
	 * @param user the user
	 * @return the string
	 */
	private String generateServiceToken(User user) {

		JwtInfo jwtInfo = new JwtInfo(user.getId(), user.getUsername(), user.getRole().toString());

		return jwtGenerator.generate(jwtInfo);

	}

    @DeleteMapping("/deleteUser/{userId}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long userId) throws InstanceNotFoundException{
		userService.deleteUser(userId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

	}

	@DeleteMapping("/disableUser/{userId}")
	public ResponseEntity<Void> disableUser(@PathVariable Long userId) throws InstanceNotFoundException, PermissionException, IOException {
		userService.disableUser(userId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

    @GetMapping("/list")
	public ResponseEntity<Page<UserDto>> findAllUsers(
		@AuthenticationPrincipal JwtInfo jwtInfo,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "") String searchTerm,
		@RequestParam(defaultValue = "ALL") String roleFilter
		) throws InstanceNotFoundException, PermissionException {

		User user = permissionChecker.checkUser(jwtInfo.getUserId());

		if (!user.getRole().equals(User.Role.ADMIN)) {
			throw new PermissionException();
		}

		User.Role roleFilterValue;
		switch (roleFilter.toUpperCase(Locale.ROOT)) {
			case "ADMIN":
				roleFilterValue = User.Role.ADMIN;
				break;
			case "USER":
				roleFilterValue = User.Role.USER;
				break;
			default:
				roleFilterValue = null; // All roles
		}

		Sort sort = Sort.by(Sort.Direction.ASC, "id");
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<User> userList = userService.getAllUsers(pageable, searchTerm, roleFilterValue);

		Map<Long, Long> userPointsMap = pointTransactionService.getTotalPointsByUserIds(
			userList.getContent().stream()
				.map(User::getId)
				.toList()
		);
		
		Page<UserDto> userDtoList = userList.map(u ->
			UserConversor.toUserDto(
				u,
				userPointsMap.getOrDefault(u.getId(), 0L)
			)
		);


		return ResponseEntity.ok(userDtoList);
	}

	@PostMapping("/googleLogin")
	public ResponseEntity<AuthenticatedUserDto> googleLogin(@RequestBody Map<String, String> request) {
		try {
			String googleToken = request.get("token");
			User user = googleOAuth2Service.processGoogleUser(googleToken);

			String serviceToken = generateServiceToken(user);
			Long points = pointTransactionService.getTotalPointsByUserId(user.getId());

			return ResponseEntity.ok(UserConversor.toAuthenticatedUserDto(serviceToken, user, points));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
