package com.civicfix.tfg.model.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.PostVote;
import com.civicfix.tfg.model.entities.daos.PostDao;
import com.civicfix.tfg.model.entities.Post;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;
import com.civicfix.tfg.model.services.exceptions.PermissionException;

public interface PostService {

    public Post createPost(Post post, List<MultipartFile> files, List<Long> relatedPostIds) throws IOException, InstanceNotFoundException, ForbiddenFileTypeException, MaxFileSizeException;

    public Post getPostById(Long id) throws InstanceNotFoundException;

    public Post update(Long id, Post post, List<String> currentFiles, List<MultipartFile> files, List<Long> relatedPostIds) throws InstanceNotFoundException, IOException, PermissionException, ForbiddenFileTypeException, MaxFileSizeException;

    public void deletePost(Long postId, Long userId) throws InstanceNotFoundException, PermissionException, IOException;

    public void deleteAllByUserId(Long userId) throws InstanceNotFoundException, PermissionException, IOException;

    public List<Post> getPostsByAuthor(Long userId) throws InstanceNotFoundException;
    
    public Page<Post> getPostsByCategory(Post.Category category, Pageable pageable);
    
    public List<Post> getPostsByCategory(Post.Category category);

    public List<Post> getLastPosts();

    public void votePost(Long postId, Long userId, int vote) throws InstanceNotFoundException;

    public void deleteVote(Long userId, Long postId) throws InstanceNotFoundException, PermissionException;

    public Map<Long, Integer> getVotesByUserIdAndOPostVotesIn(Long userId, List<Long> postIds);

    public Map<Long, Integer> getVoteSumsByPostIds(List<Long> postIds);

    public List<PostVote> getVotesByPostId(Long postId);

    public List<PostDao.PostToListView> getPostsToListViewBySolved(boolean isSolved);

}
