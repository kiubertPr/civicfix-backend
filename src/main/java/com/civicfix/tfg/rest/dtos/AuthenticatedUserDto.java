package com.civicfix.tfg.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticatedUserDto {

	/** The service token. */
	private String serviceToken;
	
	/** The user dto. */
	private UserDto userDto;

	/**
	 * Instantiates a new authenticated user dto.
	 */
	public AuthenticatedUserDto() {
	}

	/**
	 * Gets the service token.
	 *
	 * @return the service token
	 */
	public String getServiceToken() {
		return serviceToken;
	}

	/**
	 * Sets the service token.
	 *
	 * @param serviceToken the new service token
	 */
	public void setServiceToken(String serviceToken) {
		this.serviceToken = serviceToken;
	}

	/**
	 * Gets the user dto.
	 *
	 * @return the user dto
	 */
	@JsonProperty("user")
	public UserDto getUserDto() {
		return userDto;
	}

	/**
	 * Sets the user dto.
	 *
	 * @param userDto the new user dto
	 */
	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}

}
