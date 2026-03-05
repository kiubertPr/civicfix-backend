package com.civicfix.tfg.model.entities;

import java.io.Serializable;
import java.util.Objects;

public class PostVoteId implements Serializable {

    private Long userId;
    private Long postId;

    public PostVoteId() {}

    public PostVoteId(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostVoteId)) return false;
        PostVoteId that = (PostVoteId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(postId, that.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, postId);
    }
}

