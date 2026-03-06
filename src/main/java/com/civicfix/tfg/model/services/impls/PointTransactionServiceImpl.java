package com.civicfix.tfg.model.services.impls;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.PointTransaction;
import com.civicfix.tfg.model.entities.daos.PointTransactionDao;
import com.civicfix.tfg.model.services.PermissionChecker;
import com.civicfix.tfg.model.services.PointTransactionService;
import com.civicfix.tfg.model.services.exceptions.NotEnoughPointsException;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PointTransactionServiceImpl implements PointTransactionService {

    private final PointTransactionDao pointTransactionDao;
    private final PermissionChecker permissionChecker;

    public PointTransactionServiceImpl(PointTransactionDao pointTransactionDao, PermissionChecker permissionChecker) {
        this.pointTransactionDao = pointTransactionDao;
        this.permissionChecker = permissionChecker;
    }

    private static final Map<PointTransaction.TransactionType, Integer> POINT_VALUES = Map.of(
        PointTransaction.TransactionType.DAILY_LOGIN, 5,
        PointTransaction.TransactionType.CREATE_POST, 5,
        PointTransaction.TransactionType.RECEIVE_VOTE, 2,
        PointTransaction.TransactionType.CAST_VOTE, 1,
        PointTransaction.TransactionType.PARTICIPATE_SURVEY, 3,
        PointTransaction.TransactionType.SOLVED_POST, 50
    );
    
    @Override
    public void createPointTransaction(Long userId, PointTransaction.TransactionType reason, PointTransaction.EntityType entityType, Long entityId) throws InstanceNotFoundException {
        permissionChecker.checkUser(userId);

        if (!POINT_VALUES.containsKey(reason)) {
            throw new IllegalArgumentException("Invalid transaction reason: " + reason);
        }

        if (reason == PointTransaction.TransactionType.DAILY_LOGIN) {
            Optional<PointTransaction> existingTransaction = pointTransactionDao
                    .findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(userId, reason, entityType, entityId);
            if (existingTransaction.isPresent() && ChronoUnit.HOURS.between(existingTransaction.get().getCreatedAt(), LocalDateTime.now()) < 24) {
                return;
            }
        }
                
        PointTransaction pointTransaction = new PointTransaction(userId, POINT_VALUES.get(reason), reason, entityType, entityId);
        pointTransactionDao.save(pointTransaction);
    }

    @Override
    public void changePoints(Long userId, int points) throws InstanceNotFoundException, NotEnoughPointsException {
        permissionChecker.checkUser(userId);

        if (getTotalPointsByUserId(userId) + points < 0) {
            throw new NotEnoughPointsException();
        }

        PointTransaction pointTransaction = new PointTransaction(userId, -points, PointTransaction.TransactionType.CHANGE_POINTS, PointTransaction.EntityType.SYSTEM, null);
        pointTransactionDao.save(pointTransaction);
    }

    @Override
    @Transactional
    public long getTotalPointsByUserId(Long userId) throws InstanceNotFoundException {
        
        permissionChecker.checkUser(userId);

        return pointTransactionDao.getTotalPointsByUserId(userId) != null ? 
               pointTransactionDao.getTotalPointsByUserId(userId) : 0L;
    }

    @Override
    @Transactional
    public Map<Long, Long> getTotalPointsByUserIds(List<Long> userIds) throws InstanceNotFoundException {

        return pointTransactionDao.findTotalPointsByUserIds(userIds).stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Long) row[1])
            ));
    }

    @Override
    @Transactional
    public Page<PointTransaction> getPointTransactionsByUserId(Long userId, Pageable pageable) throws InstanceNotFoundException{
        
        permissionChecker.checkUser(userId);
        return pointTransactionDao.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Optional<PointTransaction> findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
        Long userId,
        PointTransaction.TransactionType reason,
        PointTransaction.EntityType entityType,
        Long entityId) {
        
        return pointTransactionDao.findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            userId, reason, entityType, entityId);
    }

    @Override
    public void deletePointTransaction(Long userId, PointTransaction.TransactionType reason, PointTransaction.EntityType entityType, long entityId) {
        
        Optional<PointTransaction> pointTransaction = pointTransactionDao.findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            userId, reason, entityType, entityId
        );

        if (pointTransaction.isPresent()) {
            pointTransactionDao.delete(pointTransaction.get());
        }

    }
}
