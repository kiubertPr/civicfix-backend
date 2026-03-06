package com.civicfix.tfg.model.services.impls;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.entities.PointTransaction.EntityType;
import com.civicfix.tfg.model.entities.PointTransaction.TransactionType;
import com.civicfix.tfg.model.entities.daos.UserDao;
import com.civicfix.tfg.model.services.FileService;
import com.civicfix.tfg.model.services.PermissionChecker;
import com.civicfix.tfg.model.services.PointTransactionService;
import com.civicfix.tfg.model.services.PostService;
import com.civicfix.tfg.model.services.UserService;
import com.civicfix.tfg.model.services.exceptions.DuplicateEmailException;
import com.civicfix.tfg.model.services.exceptions.DuplicateUsernameException;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.IncorrectLoginException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;
import com.civicfix.tfg.model.services.exceptions.PermissionException;
import com.civicfix.tfg.model.services.exceptions.UserDisableException;
import com.civicfix.tfg.rest.dtos.UserDto;
import com.civicfix.tfg.rest.dtos.request.ChangePasswordParamsDto;
import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PermissionChecker permissionChecker;
    private final FileService fileService;
    private final PointTransactionService pointTransactionService;
    private final PostService postService;

    private final static String DEFAULT_AVATAR_URL = "https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg";

    public UserServiceImpl(UserDao userDao, BCryptPasswordEncoder passwordEncoder, PermissionChecker permissionChecker, FileService fileService, PointTransactionService pointTransactionService, PostService postService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.permissionChecker = permissionChecker;
        this.fileService = fileService;
        this.pointTransactionService = pointTransactionService;
        this.postService = postService;
    }
    

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword)
            throws InstanceNotFoundException, IncorrectLoginException, UserDisableException {
        
        User user = permissionChecker.checkUser(id);

        if (user.getRole() == User.Role.DISABLED) {
            throw new UserDisableException("User is disabled and cannot change password.");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword()) || oldPassword.isBlank() || newPassword.isBlank()) {
            throw new IncorrectLoginException();
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        
    }

    @Override
    public void deleteUser(Long id) throws InstanceNotFoundException {

        User user = userDao.findById(id).orElseThrow(() -> new InstanceNotFoundException("project.entities.user", id));	
		userDao.delete(user);
        
    }

    @Override
    public void disableUser(Long id) throws InstanceNotFoundException, PermissionException, IOException {

        User user = permissionChecker.checkUser(id);

        if (user.getRole() == User.Role.DISABLED) {
            user.setRole(User.Role.USER);
        }else{
            user.setRole(User.Role.DISABLED);
            user.setAvatar(DEFAULT_AVATAR_URL);
            user.setAvatarId(null);
        }

        postService.deleteAllByUserId(id);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable, String searchTerm, User.Role roleFilter) {
        return userDao.findAllWithFilters(pageable, searchTerm, roleFilter);
    }

    @Override
    public Long getUserId(String username) throws InstanceNotFoundException {
        User user = userDao.findByUsername(username).orElseThrow(() -> new InstanceNotFoundException("project.entities.user", username));
		return user.getId();
    }

    @Override
    @Transactional
    public User login(String username, String password) throws IncorrectLoginException, InstanceNotFoundException, UserDisableException {
        
        Optional <User> user = userDao.findByUsername(username);

        if (!user.isPresent()) {
            throw new InstanceNotFoundException("project.entities.user", username);
        }

        if (user.get().getRole() == User.Role.DISABLED) {
            throw new UserDisableException("User is disabled and cannot log in.");
        }

        if(!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new IncorrectLoginException();
        }

        pointTransactionService.createPointTransaction(
            user.get().getId(),
            TransactionType.DAILY_LOGIN,
            EntityType.SYSTEM,
            user.get().getId()
        );

        return user.get();
    }

    @Override
    public User loginFromId(Long id) throws InstanceNotFoundException, UserDisableException {

        User user = permissionChecker.checkUser(id);

        if (user.getRole() == User.Role.DISABLED) {
            throw new UserDisableException("User is disabled and cannot log in.");
        }

        pointTransactionService.createPointTransaction(
            id,
            TransactionType.DAILY_LOGIN,
            EntityType.SYSTEM,
            id
        );
        return permissionChecker.checkUser(id);
    }

    @Override
    public void signUp(User user) throws DuplicateEmailException, DuplicateUsernameException {
        
        if(userDao.existsByUsername(user.getUsername())) {
            throw new DuplicateUsernameException("project.entities.user");
        }

        if (userDao.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("project.entities.user");
            
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        user.setProvider(User.Provider.LOCAL);
        user.setAvatar("https://res.cloudinary.com/civicfix/image/upload/v1744723480/296fe121-5dfa-43f4-98b5-db50019738a7_hln3ql.jpg");

        userDao.save(user);
    }

    @Override
    public User update(Long id, UserDto updateData, ChangePasswordParamsDto passwordParams, MultipartFile avatar) throws InstanceNotFoundException, IncorrectLoginException, IOException, ForbiddenFileTypeException, MaxFileSizeException, UserDisableException {

        User userToUpdate = permissionChecker.checkUser(id);

        if (userToUpdate.getRole() == User.Role.DISABLED) {
            throw new UserDisableException("User is disabled and cannot be updated.");
        }

        userToUpdate.setFirstName(updateData.getFirstName());
        userToUpdate.setLastName(updateData.getLastName());
        userToUpdate.setUsername(updateData.getUsername());

        if (updateData.getProvider().equals("LOCAL")) {
            userToUpdate.setEmail(updateData.getEmail());
        }

        if(passwordParams != null &&
           passwordParams.getOldPassword() != null &&
           passwordParams.getNewPassword() != null &&
           updateData.getProvider().equals("LOCAL")) {
            changePassword(id, passwordParams.getOldPassword(), passwordParams.getNewPassword());
        }

        if(avatar != null && !avatar.isEmpty()) {
            if (userToUpdate.getAvatarId() != null) {
                fileService.deleteFile(userToUpdate.getAvatarId());
            }

            fileService.uploadAvatar(userToUpdate, avatar);
        }

        userDao.save(userToUpdate);

        return userToUpdate;
    }
    

    @Override
    @Transactional
    public Integer countUsersByRole(User.Role role) {
        return userDao.countByRole(role);
    }
}
