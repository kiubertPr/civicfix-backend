package com.civicfix.tfg.model.services.impls;

import org.springframework.stereotype.Service;

import java.util.Optional;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.entities.daos.UserDao;
import com.civicfix.tfg.model.services.PermissionChecker;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PermissionCheckerImpl implements PermissionChecker {

    private final UserDao userDao;

    public PermissionCheckerImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User checkUser(Long userId) throws InstanceNotFoundException {
        
        Optional <User> user = userDao.findById(userId);

        if (!user.isPresent()) {
			throw new InstanceNotFoundException("project.entities.user", userId);
		}
		
		return user.get();
    }
    
}
