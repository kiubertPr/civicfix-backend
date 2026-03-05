package com.civicfix.tfg.model.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "PointTransactions")
public class PointTransaction {

    public enum EntityType {
        POST, SURVEY, SYSTEM
    }

    public enum TransactionType {
        DAILY_LOGIN, CREATE_POST, RECEIVE_VOTE, CAST_VOTE, PARTICIPATE_SURVEY, CHANGE_POINTS, SOLVED_POST
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Integer points;

    @Enumerated(EnumType.STRING)
    private TransactionType reason;

    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    private Long entityId;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PointTransaction() {}

    public PointTransaction(Long userId, Integer points, TransactionType reason, EntityType entityType, Long entityId) {
        this.userId = userId;
        this.points = points;
        this.reason = reason;
        this.entityType = entityType;
        this.entityId = entityId;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public TransactionType getReason() {
        return reason;
    }

    public void setReason(TransactionType reason) {
        this.reason = reason;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}