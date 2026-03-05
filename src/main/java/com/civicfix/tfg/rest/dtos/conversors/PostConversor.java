package com.civicfix.tfg.rest.dtos.conversors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.civicfix.tfg.model.entities.Post;
import com.civicfix.tfg.rest.dtos.PostDto;

public class PostConversor {
    
    private PostConversor() {
        // Private constructor to prevent instantiation
    }

    public static final PostDto toPostDto(Post post, Integer voteSum, Integer userVote) {
        return new PostDto(post.getId(), post.getTitle(), post.getContent(), post.getLocation(), post.getLatitude(), post.getLongitude(), post.getDate(), post.isSolved(),
                new ArrayList<>(post.getRelatedPosts()), new ArrayList<>(post.getImages().values()), new ArrayList<>(post.getFiles().values()), UserConversor.toUserDto(post.getAuthor()), post.getCategory(), voteSum, userVote);
    }

    public static final Page<PostDto> toPostDtoPage(Page<Post> posts,  Map<Long, Integer> voteSumTotals, Map<Long, Integer> userVotes) {
        return posts.map(post -> toPostDto(post, voteSumTotals.getOrDefault(post.getId(), 0), userVotes.getOrDefault(post.getId(), 0)));
    }

    public static final PostDto toPostDto(Post post) {
        return new PostDto(post.getId(), post.getTitle(), post.getContent(), post.getLocation(), post.getLatitude(), post.getLongitude(), post.getDate(), post.isSolved(),
                new ArrayList<>(post.getRelatedPosts()), new ArrayList<>(post.getImages().values()), new ArrayList<>(post.getFiles().values()), UserConversor.toUserDto(post.getAuthor()), post.getCategory(), 0, 0);
    }

    public static final Page<PostDto> toPostDtoPage(Page<Post> posts) {
        return posts.map(PostConversor::toPostDto);
    }
    
    public static final List<PostDto> toPostDtoList(List<Post> posts, Map<Long, Integer> voteSumTotals, Map<Long, Integer> userVotes) {
        List<PostDto> dtoList = new ArrayList<>();
        for (Post post : posts) {
            dtoList.add(toPostDto(post, voteSumTotals.getOrDefault(post.getId(), 0), userVotes.getOrDefault(post.getId(), 0)));
        }
        return dtoList;
    }

}
