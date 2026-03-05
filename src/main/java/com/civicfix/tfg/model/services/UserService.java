package com.civicfix.tfg.model.services;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.services.exceptions.DuplicateEmailException;
import com.civicfix.tfg.model.services.exceptions.DuplicateUsernameException;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.IncorrectLoginException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;
import com.civicfix.tfg.model.services.exceptions.PermissionException;
import com.civicfix.tfg.model.services.exceptions.UserDisableException;
import com.civicfix.tfg.rest.dtos.UserDto;
import com.civicfix.tfg.rest.dtos.request.ChangePasswordParamsDto;

import com.civicfix.tfg.model.entities.User;

public interface UserService {
    
    void signUp(User user) throws DuplicateEmailException, DuplicateUsernameException;

    User login(String username, String password) throws IncorrectLoginException, InstanceNotFoundException, UserDisableException;

    User loginFromId(Long id) throws InstanceNotFoundException, UserDisableException;

    User update(Long id, UserDto updateData, ChangePasswordParamsDto passwordParams, MultipartFile avatar) throws InstanceNotFoundException,  IncorrectLoginException, IOException, ForbiddenFileTypeException, MaxFileSizeException, UserDisableException;

    void changePassword(Long id, String oldPassword, String newPassword) throws InstanceNotFoundException, IncorrectLoginException, UserDisableException;

    Long getUserId(String username) throws InstanceNotFoundException;

    void deleteUser(Long id) throws InstanceNotFoundException;

    void disableUser(Long id) throws InstanceNotFoundException, PermissionException, IOException;

    Page <User> getAllUsers(Pageable pageable, String searchTerm, User.Role roleFilter);

    Integer countUsersByRole(User.Role role);

}
