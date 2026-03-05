package com.civicfix.tfg.model.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.entities.daos.UserDao;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PermissionCheckerTest {
    
    @Autowired
    private PermissionChecker permissionChecker;

    @MockitoBean
    private UserDao userDao;

    @Test
    public void checkUserPermissionTest() throws InstanceNotFoundException {
        User user = new User("Test", "password", "First", "Last", "testuser@example.com", 
                             "https://example.com/avatar.jpg", "avatarId123");

        user.setId(999L);

        when(userDao.findById(user.getId())).thenReturn(Optional.of(user));

        User inBDD = permissionChecker.checkUser(user.getId());

        assertTrue(inBDD.equals(user));
    }

    @Test(expected = InstanceNotFoundException.class)
    public void checkUserPermissionWithInvalidIdTest() throws InstanceNotFoundException {
        Long invalidUserId = 999L; // Assuming this user does not exist

        when(userDao.findById(invalidUserId)).thenReturn(Optional.empty());

        permissionChecker.checkUser(invalidUserId);
    }
}
