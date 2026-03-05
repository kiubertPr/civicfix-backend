package com.civicfix.tfg.model.entities.daos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.civicfix.tfg.model.entities.PointTransaction;

public interface PointTransactionDao extends JpaRepository<PointTransaction, Long> {
    
    Page<PointTransaction> findByUserId(Long userId, Pageable pageable);
    List<PointTransaction> findByEntityType(PointTransaction.EntityType entityType);
    List<PointTransaction> findByEntityId(Long entityId);
    List<PointTransaction> findByCreatedAtAfter(LocalDateTime date);
    List<PointTransaction> findByCreatedAtBefore(LocalDateTime date);
    List<PointTransaction> findByReason(PointTransaction.TransactionType reason);
    Optional<PointTransaction> findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
        Long userId,
        PointTransaction.TransactionType reason,
        PointTransaction.EntityType entityType,
        Long entityId);
    
    @Query("SELECT SUM(p.points) FROM PointTransaction p WHERE p.userId = :userId")
    Integer getTotalPointsByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT pt.userId, SUM(pt.points)
        FROM PointTransaction pt
        WHERE pt.userId IN :userIds
        GROUP BY pt.userId
    """)
    List<Object[]> findTotalPointsByUserIds(@Param("userIds") List<Long> userIds);

}
