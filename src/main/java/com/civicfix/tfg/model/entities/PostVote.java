package com.civicfix.tfg.model.entities;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(PostVoteId.class)
@Table(name = "PostVotes")
public class PostVote implements Serializable {

    @Id
    @Column(name = "userId", nullable = false)
    private Long userId;

    @Id
    @Column(name = "postId", nullable = false)
    private Long postId;

    @Column(nullable = false)
    private int vote;

    public PostVote() {}

    public PostVote(Long userId, Long postId, int vote) {
        this.userId = userId;
        this.postId = postId;
        this.vote = vote;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}