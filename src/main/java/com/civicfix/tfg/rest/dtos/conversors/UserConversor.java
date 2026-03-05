package com.civicfix.tfg.rest.dtos.conversors;

import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.rest.dtos.AuthenticatedUserDto;
import com.civicfix.tfg.rest.dtos.UserDto;

public class UserConversor {

	/**
	 * Instantiates a new user conversor.
	 */
	private UserConversor() {}

	/**
	 * To user dto.
	 *
	 * @param user the user
	 * @return the user dto
	 */
	public static final UserDto toUserDto(User user, Long points) {
		return new UserDto(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
				user.getAvatar(), user.getAvatarId(),  user.getRole().toString(), user.getProvider().toString(), points != null ? points : 0L);
	}

	public static final UserDto toUserDto(User user) {
		return new UserDto(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(),
				user.getAvatar(), user.getAvatarId(), user.getRole().toString(), user.getProvider().toString());
	}

	/**
	 * To user.
	 *
	 * @param userDto the user dto
	 * @return the user
	 */
	public static final User toUser(UserDto userDto) {

		return new User(userDto.getUsername(), userDto.getPassword(), userDto.getFirstName(), userDto.getLastName(),
				userDto.getEmail(), userDto.getAvatar(), userDto.getAvatarId());
	}

	/**
	 * To authenticated user dto.
	 *
	 * @param serviceToken the service token
	 * @param user         the user
	 * @return the authenticated user dto
	 */
	public static final AuthenticatedUserDto toAuthenticatedUserDto(String serviceToken, User user, Long points) {

		AuthenticatedUserDto authenticatedUserDto = new AuthenticatedUserDto();
		authenticatedUserDto.setServiceToken(serviceToken);
		authenticatedUserDto.setUserDto(toUserDto(user, points));
		return authenticatedUserDto;

	}

}
