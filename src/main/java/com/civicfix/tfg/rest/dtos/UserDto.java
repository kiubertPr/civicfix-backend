package com.civicfix.tfg.rest.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {

    public interface AllValidations {}
    public interface UpdateValidations {}

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private String avatarId;
    private String role;
    private String provider;
    private Long points;

    public UserDto() {}
    
    public UserDto(Long id, String username, String firstName, String lastName, String email, String avatar, String avatarId, String role, String provider, Long points) {
        this.id = id;
        this.username = username != null ? username.trim() : null;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.email = email.trim();
        this.avatar = avatar != null ? avatar.trim() : null;
        this.avatarId = avatarId != null ? avatarId.trim() : null;
        this.role = role;
        this.provider = provider != null ? provider.trim() : null;
        this.points = points != null ? points : 0L;
    }

    public UserDto(Long id, String username, String firstName, String lastName, String email, String avatar, String avatarId, String role, String provider) {
        this(id, username, firstName, lastName, email, avatar, avatarId, role, provider, 0L);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull(groups={AllValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class})
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull(groups={AllValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class})
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotNull(groups={AllValidations.class, UpdateValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class, UpdateValidations.class})
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotNull(groups={AllValidations.class, UpdateValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class, UpdateValidations.class})
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotNull(groups={AllValidations.class, UpdateValidations.class})
	@Size(min=1, max=60, groups={AllValidations.class, UpdateValidations.class})
	@Email(groups={AllValidations.class, UpdateValidations.class})
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
    
}
