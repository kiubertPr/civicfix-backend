package com.civicfix.tfg.model.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.PointTransaction;
import com.civicfix.tfg.model.entities.Post;
import com.civicfix.tfg.model.entities.PostVote;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;
import com.civicfix.tfg.model.services.exceptions.PermissionException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostServiceTest {
    
    @Autowired
    private PostService postService;

    @MockitoBean
    private FileService fileService;
    
    @MockitoBean
    private PointTransactionService pointTransactionService;

    private Post createTestPost(String title, String content) {
        User author = new User();
        author.setId(1L);
        return new Post(title, content, "Test Location", 0.0, 0.0, author);
    }

    @Test
    public void createPostTest() throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Test Title", "Test Content");

        doNothing().when(fileService).uploadFile(any(), any());

        Post savedPost = postService.createPost(post, null, null);

        verify(pointTransactionService).createPointTransaction(any(), any(), any(), any());
        assertNotNull(savedPost);
        assertEquals("Test Title", savedPost.getTitle());
        assertEquals("Test Content", savedPost.getContent());
    }

    @Test
    public void getPostByIdTest() throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Test Title", "Test Content");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        Post foundPost = postService.getPostById(savedPost.getId());
        assertNotNull(foundPost);
        assertEquals("Test Title", foundPost.getTitle());
    }

    @Test(expected = InstanceNotFoundException.class)
    public void  getPostByIdNotFoundTest() throws InstanceNotFoundException {
        postService.getPostById(999L);
    }

    @Test
    public void updatePostTest() throws InstanceNotFoundException, IOException, PermissionException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Original Title", "Original Content");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        savedPost.setTitle("Updated Title");
        savedPost.setContent("Updated Content");

        Post updatedPost = postService.update(savedPost.getId(), savedPost, null, null, null);
        
        assertNotNull(updatedPost);
        assertEquals("Updated Title", updatedPost.getTitle());
        assertEquals("Updated Content", updatedPost.getContent());
    }

    @Test(expected = InstanceNotFoundException.class)
    public void updatePostNotFoundTest() throws InstanceNotFoundException, IOException, PermissionException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Test Title", "Test Content");
        postService.update(999L, post, null, null, null);
    }

    @Test(expected = PermissionException.class)
    public void updatePostPermissionDeniedTest() throws InstanceNotFoundException, IOException, PermissionException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Test Title", "Test Content");
        post.setAuthor(new User()); // Set a different author to simulate permission denial
        post.getAuthor().setId(2L); // Simulate a different user ID

        postService.update(1L, post, null, null, null); // Attempt to update with a different user ID
    }

    @Test(expected = InstanceNotFoundException.class)
    public void deletePostTest() throws InstanceNotFoundException, PermissionException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Test Title", "Test Content");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        postService.deletePost(savedPost.getId(), savedPost.getAuthor().getId());

        postService.getPostById(savedPost.getId()); // This should throw InstanceNotFoundException
    }

    @Test(expected = InstanceNotFoundException.class)
    public void deletePostNotFoundTest() throws InstanceNotFoundException, PermissionException, IOException {
        postService.deletePost(999L, 1L); // Attempt to delete a non-existing post
    }

    @Test(expected = PermissionException.class)
    public void deletePostPermissionDeniedTest() throws InstanceNotFoundException, PermissionException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Test Title", "Test Content");
        post.setAuthor(new User()); // Set a different author to simulate permission denial
        post.getAuthor().setId(2L); // Simulate a different user ID

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        postService.deletePost(savedPost.getId(), 3L); // Attempt to delete with a different user ID
    }

    @Test
    public void getPostsByAuthorTest() throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        User author = new User();
        author.setId(1L);
        Post post = createTestPost("Author Post", "Content by Author");
        post.setAuthor(author);

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals(author.getId(), savedPost.getAuthor().getId());

        // Fetch posts by author
        List<Post> postsByAuthor = postService.getPostsByAuthor(author.getId());
        assertNotNull(postsByAuthor);
        assertEquals(savedPost.getId(), postsByAuthor.get(postsByAuthor.size() - 1).getId());
    }

    @Test
    public void getPostsByCategoryTest() throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Category Post", "Content in Category");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals(Post.Category.USER, savedPost.getCategory());

        // Fetch posts by category
        List<Post> postsByCategory = postService.getPostsByCategory(Post.Category.USER);
        assertNotNull(postsByCategory);
        assertTrue(postsByCategory.contains(savedPost));
    }

    @Test
    public void getPostsByCategoryWithPageableTest() throws IOException, InstanceNotFoundException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Category Post", "Content in Category");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals(Post.Category.USER, savedPost.getCategory());

        Page<Post> posts = postService.getPostsByCategory(Post.Category.USER, PageRequest.of(0, 10));
        assertNotNull(posts);
    }

    @Test
    public void getLastPostsTest() throws IOException, InstanceNotFoundException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Last Posts", "Content for Last Posts");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals("Last Posts", savedPost.getTitle());

        List<Post> lastPosts = postService.getLastPosts();
        assertNotNull(lastPosts);
        assertEquals(savedPost.getId(), lastPosts.get(lastPosts.size() - 1).getId());
    }

    @Test
    public void votePostTest() throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Vote Post", "Content for Voting");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals("Vote Post", savedPost.getTitle());

        postService.votePost(savedPost.getId(), 2L, 1);
        
        postService.getPostById(savedPost.getId());
        verify(pointTransactionService).createPointTransaction(2L, PointTransaction.TransactionType.CAST_VOTE, PointTransaction.EntityType.POST, savedPost.getId());
    }

    @Test(expected = InstanceNotFoundException.class)
    public void votePostNotFoundTest() throws InstanceNotFoundException {
        postService.votePost(999L, 2L, 1); // Attempt to vote on a non-existing post
    }
    
    @Test
    public void deleteVotePostTest() throws InstanceNotFoundException, PermissionException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Delete Vote Post", "Content for Deleting Vote");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals("Delete Vote Post", savedPost.getTitle());

        postService.votePost(savedPost.getId(), 2L, 1);

        postService.deleteVote(2L, savedPost.getId());

        verify(pointTransactionService).deletePointTransaction(2L, PointTransaction.TransactionType.CAST_VOTE, PointTransaction.EntityType.POST, savedPost.getId());
    }

    @Test(expected = InstanceNotFoundException.class)
    public void deleteVotePostNotFoundTest() throws InstanceNotFoundException, PermissionException {
        postService.deleteVote(2L, 999L); // Attempt to delete vote on a non-existing post
    }

    @Test
    public void getVotesByUserIdAndOPostVotesInTest() throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Votes By User", "Content for Votes By User");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals("Votes By User", savedPost.getTitle());

        postService.votePost(savedPost.getId(), 2L, 1);

        Map<Long, Integer> votes = postService.getVotesByUserIdAndOPostVotesIn(2L, List.of(savedPost.getId()));
        assertNotNull(votes);
    }

    @Test
    public void getVoteSumsByPostIdsTest() throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Vote Sums Post", "Content for Vote Sums");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals("Vote Sums Post", savedPost.getTitle());

        postService.votePost(savedPost.getId(), 2L, 1);

        Map<Long, Integer> voteSums = postService.getVoteSumsByPostIds(List.of(savedPost.getId()));
        assertNotNull(voteSums);
        assertEquals(Integer.valueOf(1), voteSums.get(savedPost.getId()));
    }

    @Test
    public void getVotesByPostIdTest() throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        Post post = createTestPost("Votes By Post", "Content for Votes By Post");

        doNothing().when(fileService).uploadFile(any(), any());
        Post savedPost = postService.createPost(post, null, null);

        assertNotNull(savedPost);
        assertEquals("Votes By Post", savedPost.getTitle());

        postService.votePost(savedPost.getId(), 2L, 1);

        List<PostVote> votes = postService.getVotesByPostId(savedPost.getId());
        assertNotNull(votes);
        assertEquals(1, votes.get(0).getVote());
    }
}