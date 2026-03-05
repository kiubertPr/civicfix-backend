package com.civicfix.tfg.model.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.PointTransaction;
import com.civicfix.tfg.model.services.exceptions.NotEnoughPointsException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PointTransactionServiceTest {

    @Autowired
    private PointTransactionService pointTransactionService;

    @Test
    public void createPointTransactionTest() throws InstanceNotFoundException {
        Long userId = 1L;
        PointTransaction.TransactionType reason = PointTransaction.TransactionType.CREATE_POST;
        PointTransaction.EntityType entityType = PointTransaction.EntityType.POST;
        Long entityId = 123L;

        pointTransactionService.createPointTransaction(userId, reason, entityType, entityId);

        // Verify that the transaction was created successfully
        long totalPoints = pointTransactionService.getTotalPointsByUserId(userId);
        assertTrue(totalPoints > 0);
    }

    @Test(expected = InstanceNotFoundException.class)
    public void createPointTransactionWithInvalidUserTest() throws InstanceNotFoundException {
        Long invalidUserId = 999L; // Assuming this user does not exist
        PointTransaction.TransactionType reason = PointTransaction.TransactionType.CREATE_POST;
        PointTransaction.EntityType entityType = PointTransaction.EntityType.POST;
        Long entityId = 123L;

        pointTransactionService.createPointTransaction(invalidUserId, reason, entityType, entityId);
    }

    @Test
    public void changePointsTest() throws InstanceNotFoundException, NotEnoughPointsException {
        Long userId = 1L;
        int pointsToSubtract = -1;

        pointTransactionService.createPointTransaction(userId, PointTransaction.TransactionType.DAILY_LOGIN, null, userId);

        pointTransactionService.changePoints(userId, pointsToSubtract);

        long totalPoints = pointTransactionService.getTotalPointsByUserId(userId);
        assertTrue(totalPoints >= pointsToSubtract);
    }

    @Test(expected = NotEnoughPointsException.class)
    public void changePointsWithInsufficientPointsTest() throws InstanceNotFoundException, NotEnoughPointsException {
        Long userId = 1L;
        int pointsToSubtract = -100; // Assuming the user does not have enough points

        pointTransactionService.changePoints(userId, pointsToSubtract);
    }

    @Test
    public void getTotalPointsByUserIdTest() throws InstanceNotFoundException {
        Long userId = 1L;

        long totalPoints = pointTransactionService.getTotalPointsByUserId(userId);
        assertTrue(totalPoints >= 0);
    }
    
    @Test
    public void getTotalPointsByUserIdsTest() throws InstanceNotFoundException {
        Long userId1 = 1L;
        Long userId2 = 2L;

        pointTransactionService.createPointTransaction(userId1, PointTransaction.TransactionType.DAILY_LOGIN, null, userId1);
        pointTransactionService.createPointTransaction(userId2, PointTransaction.TransactionType.CREATE_POST, PointTransaction.EntityType.POST, userId2);

        Map<Long, Long> totalPointsMap = pointTransactionService.getTotalPointsByUserIds(List.of(userId1, userId2));
        
        assertTrue(totalPointsMap.containsKey(userId1));
        assertTrue(totalPointsMap.containsKey(userId2));
    }

    @Test
    public void getPointTransactionsByUserIdTest() throws InstanceNotFoundException {
        Long userId = 1L;
        Pageable pageable = Pageable.unpaged();

        pointTransactionService.createPointTransaction(userId, PointTransaction.TransactionType.DAILY_LOGIN, null, userId);

        Page<PointTransaction> transactions = pointTransactionService.getPointTransactionsByUserId(userId, pageable);
        
        assertTrue(transactions.getTotalElements() > 0);
    }

    @Test
    public void findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDescTest() throws InstanceNotFoundException {
        Long userId = 1L;
        PointTransaction.TransactionType reason = PointTransaction.TransactionType.CREATE_POST;
        PointTransaction.EntityType entityType = PointTransaction.EntityType.POST;
        Long entityId = 123L;

        pointTransactionService.createPointTransaction(userId, reason, entityType, entityId);

        Optional<PointTransaction> transaction = pointTransactionService.findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            userId, reason, entityType, entityId);

        assertTrue(transaction.isPresent());
        assertTrue(transaction.get().getUserId().equals(userId));
        assertTrue(transaction.get().getReason().equals(reason));
        assertTrue(transaction.get().getEntityType().equals(entityType));
        assertTrue(transaction.get().getEntityId().equals(entityId));
    }

    @Test
    public void deletePointTransactionTest() throws InstanceNotFoundException {
        Long userId = 1L;
        PointTransaction.TransactionType reason = PointTransaction.TransactionType.CREATE_POST;
        PointTransaction.EntityType entityType = PointTransaction.EntityType.POST;
        Long entityId = 123L;

        pointTransactionService.createPointTransaction(userId, reason, entityType, entityId);

        Optional<PointTransaction> transaction = pointTransactionService.findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            userId, reason, entityType, entityId);

        assertTrue(transaction.isPresent());

        pointTransactionService.deletePointTransaction(userId, reason, entityType, entityId);

        Optional<PointTransaction> deletedTransaction = pointTransactionService.findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            userId, reason, entityType, entityId);

        assertFalse(deletedTransaction.isPresent());
    }
}
