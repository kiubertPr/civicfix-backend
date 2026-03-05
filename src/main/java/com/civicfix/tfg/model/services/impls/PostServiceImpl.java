package com.civicfix.tfg.model.services.impls;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.IOException;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.PostVote;
import com.civicfix.tfg.model.entities.PostVoteId;
import com.civicfix.tfg.model.entities.RelatedPost;
import com.civicfix.tfg.model.entities.Post;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.entities.PointTransaction;
import com.civicfix.tfg.model.entities.daos.PostDao;
import com.civicfix.tfg.model.entities.daos.PostVoteDao;
import com.civicfix.tfg.model.entities.daos.PostVoteDao.UserVoteView;
import com.civicfix.tfg.model.entities.daos.PostVoteDao.VoteSummary;
import com.civicfix.tfg.model.services.FileService;
import com.civicfix.tfg.model.services.PermissionChecker;
import com.civicfix.tfg.model.services.PointTransactionService;
import com.civicfix.tfg.model.services.PostService;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;
import com.civicfix.tfg.model.services.exceptions.PermissionException;

@Service
@Transactional
public class PostServiceImpl implements PostService{

    private final PostDao postDao;
    private final FileService fileService;
    private final PermissionChecker permissionChecker;
    private final PostVoteDao postVoteDao;
    private final PointTransactionService pointTransactionService;

    public PostServiceImpl(PostDao postDao, FileService fileService, PermissionChecker permissionChecker, PostVoteDao postVoteDao, PointTransactionService pointTransactionService) {
        this.postDao = postDao;
        this.fileService = fileService;
        this.permissionChecker = permissionChecker;
        this.postVoteDao = postVoteDao;
        this.pointTransactionService = pointTransactionService;
    }

    @Override
    public Post createPost(Post post, List<MultipartFile> files, List<Long> relatedPostIds) throws IOException, InstanceNotFoundException, ForbiddenFileTypeException, MaxFileSizeException{

        post.setDate(LocalDateTime.now());

        fileService.uploadFile(post, files);

        if (relatedPostIds != null && !relatedPostIds.isEmpty() && post.getCategory() == Post.Category.ADMINISTRATION) {
            List<RelatedPost> relatedPosts = getRelatedPosts(relatedPostIds);
            post.setRelatedPosts(relatedPosts);
        }

        postDao.save(post);

        pointTransactionService.createPointTransaction(
            post.getAuthor().getId(),
            PointTransaction.TransactionType.CREATE_POST,
            PointTransaction.EntityType.POST,
            post.getId()
        );

        return post;
    }

    @Override
    public Post getPostById(Long id) throws InstanceNotFoundException {
        Optional<Post> post = postDao.findById(id);

        if (!post.isPresent())
            throw new InstanceNotFoundException("project.entities.post", id);
        
        return post.get();
    }

    @Override
    public Post update(Long id, Post post, List<String> currentFiles,  List<MultipartFile> files, List<Long> relatedPostIds) throws InstanceNotFoundException, IOException, PermissionException, ForbiddenFileTypeException, MaxFileSizeException {

        Optional<Post> existingPost = postDao.findById(id);

        List<String> imagesToDelete = new ArrayList<>();
        List<String> filesToDelete = new ArrayList<>();

        if (!existingPost.isPresent())
            throw new InstanceNotFoundException("project.entities.post", id);

        if(!existingPost.get().getAuthor().getId().equals(post.getAuthor().getId()) && !permissionChecker.checkUser(post.getAuthor().getId()).getRole().equals(User.Role.ADMIN)) {
            throw new PermissionException();
        }
        
        Post updatedPost = existingPost.get();

        updatedPost.setTitle(post.getTitle());
        updatedPost.setContent(post.getContent());
        updatedPost.setLatitude(post.getLatitude());
        updatedPost.setLongitude(post.getLongitude());
        updatedPost.setLocation(post.getLocation());

        for (Map.Entry<String, String> entry : updatedPost.getImages().entrySet()) {
                String key = entry.getKey();
                String url = entry.getValue();

                if (currentFiles == null || !currentFiles.contains(url)) {
                    imagesToDelete.add(key);      
                    fileService.deleteFile(key);
                }
        }

        for (Map.Entry<String, String> entry : updatedPost.getFiles().entrySet()) {
                String key = entry.getKey();
                String url = entry.getValue();

                if (currentFiles == null || !currentFiles.contains(url)) {
                    filesToDelete.add(key);       
                    fileService.deleteFile(key);
                }
        }

        updatedPost.getImages().keySet().removeAll(imagesToDelete);
        updatedPost.getFiles().keySet().removeAll(filesToDelete);

        if (files != null && !files.isEmpty()) {
            fileService.uploadFile(updatedPost, files);
        }

        if (relatedPostIds != null && !relatedPostIds.isEmpty() && updatedPost.getCategory() == Post.Category.ADMINISTRATION) {
            List<RelatedPost> relatedPosts = getRelatedPosts(relatedPostIds);
            
            if (!new HashSet<>(updatedPost.getRelatedPosts()).equals(new HashSet<>(post.getRelatedPosts()))) {
                for (RelatedPost relatedPost : updatedPost.getRelatedPosts()) {
                    if (!relatedPostIds.contains(relatedPost.getId())) {
                        postDao.findById(relatedPost.getId()).ifPresent(rp -> {
                            rp.setSolved(false);
                            postDao.save(rp);
                            pointTransactionService.deletePointTransaction(
                                rp.getAuthor().getId(),
                                PointTransaction.TransactionType.SOLVED_POST,
                                PointTransaction.EntityType.POST,
                                rp.getId()
                            );
                        });
                    }
                }
                
            }
            updatedPost.setRelatedPosts(relatedPosts);
        }

        postDao.save(updatedPost);

        return updatedPost;
    }

    @Override
    public void deletePost(Long postId, Long userId) throws InstanceNotFoundException, PermissionException, IOException {
        Optional<Post> post = postDao.findById(postId);
        User user = permissionChecker.checkUser(userId);

        if (!post.isPresent())
            throw new InstanceNotFoundException("project.entities.post", postId);

        if(!post.get().getAuthor().getId().equals(userId) && !user.getRole().equals(User.Role.ADMIN))
            throw new PermissionException();

        if (post.get().getImages() != null) {
            for (String publicId : post.get().getImages().keySet()) {
                fileService.deleteFile(publicId);
            }
            
        }

        if (post.get().getFiles() != null) {
            for (String publicId : post.get().getFiles().keySet()) {
                fileService.deleteFile(publicId);
            }
            
        }
        
        postDao.delete(post.get());
    }

    @Override
    public void deleteAllByUserId(Long userId) throws InstanceNotFoundException, IOException, PermissionException {
        List<Post> posts = postDao.findByAuthorId(userId);
        for (Post post : posts) {
            deletePost(post.getId(), userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByAuthor(Long userId) throws InstanceNotFoundException {
        Optional<Post> post = postDao.findById(userId);
        if (!post.isPresent())
            throw new InstanceNotFoundException("project.entities.post", userId);
        
        return postDao.findByAuthorId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getPostsByCategory(Post.Category category, Pageable pageable) {
        return postDao.findByCategory(category, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByCategory(Post.Category category) {
        return postDao.findByCategoryOrderByDateDesc(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getLastPosts() {
        return postDao.findByDateAfter(LocalDateTime.now().minusHours(72));
    }


    // Vote methods

    @Override
    @Transactional
    public void votePost(Long postId, Long userId, int vote) throws InstanceNotFoundException {
        
        User user = permissionChecker.checkUser(userId);
        Optional<Post> post = postDao.findById(postId);

        if (!post.isPresent())
            throw new InstanceNotFoundException("project.entities.post", postId);

        Post existingPost = post.get();

        Optional<PointTransaction> existingTransaction = pointTransactionService.
        findFirstByUserIdAndReasonAndEntityTypeAndEntityIdOrderByCreatedAtDesc(
            userId,
            PointTransaction.TransactionType.CAST_VOTE,
            PointTransaction.EntityType.POST,
            postId
        );

        if (!existingTransaction.isPresent()) {
            pointTransactionService.createPointTransaction(
                userId,
                PointTransaction.TransactionType.CAST_VOTE,
                PointTransaction.EntityType.POST,
                postId
            );

            pointTransactionService.createPointTransaction(
                existingPost.getAuthor().getId(),
                PointTransaction.TransactionType.RECEIVE_VOTE,
                PointTransaction.EntityType.POST,
                postId
            );
        }

        postVoteDao.findByUserIdAndPostId(userId, postId)
                .map(existingVote -> {
                    existingVote.setVote(vote);
                    postVoteDao.save(existingVote);
                    return postDao.save(existingPost);
                })
                .orElseGet(() -> {
                    PostVote newVote = new PostVote(user.getId(), post.get().getId(), vote);
                    postVoteDao.save(newVote);
                    return postDao.save(existingPost);
                });
    }

    @Override
    @Transactional
    public void deleteVote(Long userId, Long postId) throws InstanceNotFoundException {

        Optional<Post> post = postDao.findById(postId);
        Optional<PostVote> postVote = postVoteDao.findByUserIdAndPostId(userId, postId);

        if (!postVote.isPresent()) {
            throw new InstanceNotFoundException("project.entities.postvote", userId);
        }

        if (!post.isPresent())
            throw new InstanceNotFoundException("project.entities.post", postId);

        pointTransactionService.deletePointTransaction(userId,
            PointTransaction.TransactionType.CAST_VOTE,
            PointTransaction.EntityType.POST,
            postId);

        pointTransactionService.deletePointTransaction(
            post.get().getAuthor().getId(),
            PointTransaction.TransactionType.RECEIVE_VOTE,
            PointTransaction.EntityType.POST,
            postId);

        postVoteDao.deleteById(new PostVoteId(userId, postId));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getVotesByUserIdAndOPostVotesIn(Long userId, List<Long> postIds) {

        Map<Long, Integer> voteMap = new HashMap<>();
        List<UserVoteView> userVotes = postVoteDao.findUserVotesForPosts(userId, postIds);
        userVotes.forEach(v -> voteMap.put(v.getPostId(), v.getUserVote()));

        return voteMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Integer> getVoteSumsByPostIds(List<Long> postIds) {
        Map<Long, Integer> voteMap = new HashMap<>();
        List<VoteSummary> voteSummaries = postVoteDao.findVoteSumsByPostIds(postIds);
        voteSummaries.forEach(v -> voteMap.put(v.getPostId(), v.getTotalVotes()));

        return voteMap;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostVote> getVotesByPostId(Long postId) {
        return postVoteDao.findByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDao.PostToListView> getPostsToListViewBySolved(boolean isSolved) {
        return postDao.findBySolved(isSolved);
    }

    private List<RelatedPost> getRelatedPosts(List<Long> relatedPostIds) throws InstanceNotFoundException{
        List<RelatedPost> relatedPosts = new ArrayList<>();
        for (Long relatedPostId : relatedPostIds) {
            Optional<Post> relatedPost = postDao.findById(relatedPostId);
            if (!relatedPost.isPresent()) {
                continue;
            }

            if (!relatedPost.get().isSolved()) {
                pointTransactionService.createPointTransaction(
                    relatedPost.get().getAuthor().getId(),
                    PointTransaction.TransactionType.CAST_VOTE,
                    PointTransaction.EntityType.POST,
                    relatedPost.get().getId()
                );
                relatedPost.get().setSolved(true);
                postDao.save(relatedPost.get());
            }

            relatedPosts.add(new RelatedPost(relatedPost.get().getId(), relatedPost.get().getTitle()));
        }
        return relatedPosts;
    }
}
