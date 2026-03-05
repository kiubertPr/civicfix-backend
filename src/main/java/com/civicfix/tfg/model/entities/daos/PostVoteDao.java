package com.civicfix.tfg.model.entities.daos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.civicfix.tfg.model.entities.PostVote;
import com.civicfix.tfg.model.entities.PostVoteId;

public interface PostVoteDao extends JpaRepository<PostVote, PostVoteId> {
    
    Optional<PostVote> findByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    List<PostVote> findByUserId(Long userId);
    List<PostVote> findByPostId(Long postId);

    @Query("SELECT v.postId AS postId, SUM(v.vote) AS totalVotes " +
       "FROM PostVote v " +
       "WHERE v.postId IN :postIds " +
       "GROUP BY v.postId")
    List<VoteSummary> findVoteSumsByPostIds(@Param("postIds") List<Long> postIds);

    interface VoteSummary {
        Long getPostId();
        Integer getTotalVotes();
    }

    @Query("SELECT v.postId AS postId, v.vote AS userVote " +
       "FROM PostVote v " +
       "WHERE v.userId = :userId AND v.postId IN :postIds")
    List<UserVoteView> findUserVotesForPosts(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);


    interface UserVoteView {
        Long getPostId();
        Integer getUserVote();
    }

}
