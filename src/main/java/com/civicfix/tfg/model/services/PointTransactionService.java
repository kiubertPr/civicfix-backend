package com.civicfix.tfg.model.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;

import com.civicfix.tfg.model.entities.PointTransaction;
import com.civicfix.tfg.model.services.exceptions.NotEnoughPointsException;

public interface PointTransactionService {

    void createPointTransaction(Long userId, PointTransaction.TransactionType reason, PointTransaction.EntityType entityType, Long entityId) throws InstanceNotFoundException;

    void changePoints(Long userId, int points) throws InstanceNotFoundException, NotEnoughPointsException;

    long getTotalPointsByUserId(Long userId) throws InstanceNotFoundException;

    Map<Long, Long> getTotalPointsByUserIds(List<Long> userIds) throws InstanceNotFoundException;

    Page<PointTransaction> getPointTransactionsByUserId(Long userId, Pageable pageable) throws InstanceNotFoundException;

    Optional<PointTransaction> findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
        Long userId,
        PointTransaction.TransactionType reason,
        PointTransaction.EntityType entityType,
        Long entityId);

    void deletePointTransaction(Long userId, PointTransaction.TransactionType reason, PointTransaction.EntityType entityType, long entityId);
}
