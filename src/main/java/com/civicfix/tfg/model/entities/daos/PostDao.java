package com.civicfix.tfg.model.entities.daos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query("SELECT KEY(i) FROM Post p JOIN p.images i")
    List<String> findAllImagePublicIds();

    @Query("SELECT KEY(f) FROM Post p JOIN p.files f")
    List<String> findAllFilePublicIds();
}
