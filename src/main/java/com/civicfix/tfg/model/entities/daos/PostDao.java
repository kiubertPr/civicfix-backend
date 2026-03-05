package com.civicfix.tfg.model.entities.daos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.civicfix.tfg.model.entities.Post;

public interface PostDao extends JpaRepository<Post, Long> {
    
    List<Post> findByAuthorId(Long userId);
    Page<Post> findByCategory(Post.Category category, Pageable pageable);
    List<Post> findByCategoryOrderByDateDesc(Post.Category category);
    List<Post> findByDateAfter(LocalDateTime date);

    List<PostToListView> findBySolved(boolean isSolved);

    interface PostToListView {
        Long getId();
        String getTitle();
    }

}
