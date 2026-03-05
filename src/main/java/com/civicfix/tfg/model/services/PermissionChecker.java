package com.civicfix.tfg.model.services;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.User;

public interface PermissionChecker {
    
    User checkUser(Long userId) throws InstanceNotFoundException;
}
