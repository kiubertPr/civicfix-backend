package com.civicfix.tfg.model.entities.daos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.civicfix.tfg.model.entities.User;
public interface UserDao extends JpaRepository<User, Long> {
    

   boolean existsByUsername(String username);

   Optional<User> findByUsername(String username);

   Optional<User> findByEmail(String email);

   @Query("SELECT u FROM User u WHERE " +
      "(:searchTerm IS NULL OR :searchTerm = '' OR " +
      "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
      "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
      "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
      "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
      "(:role IS NULL OR u.role = :role)")
   Page<User> findAllWithFilters(Pageable pageable,
                                 @Param("searchTerm") String searchTerm,
                                 @Param("role") User.Role role);


   User findByGoogleIdOrEmail(String googleId, String email);

   Integer countByRole(User.Role role);

   @Query("SELECT u.avatarId FROM User u WHERE u.avatarId IS NOT NULL")
   List<String> findAllAvatarIds();
}
