package com.civicfix.tfg.rest.controllers;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.civicfix.tfg.model.common.exceptions.InstanceNotFoundException;
import com.civicfix.tfg.model.entities.Post;
import com.civicfix.tfg.model.entities.User;
import com.civicfix.tfg.model.entities.daos.PostDao;
import com.civicfix.tfg.model.services.PermissionChecker;
import com.civicfix.tfg.model.services.PostService;
import com.civicfix.tfg.model.services.exceptions.ForbiddenFileTypeException;
import com.civicfix.tfg.model.services.exceptions.MaxFileSizeException;
import com.civicfix.tfg.model.services.exceptions.PermissionException;
import com.civicfix.tfg.rest.common.ErrorsDto;
import com.civicfix.tfg.rest.common.JwtInfo;
import com.civicfix.tfg.rest.dtos.PageDto;
import com.civicfix.tfg.rest.dtos.PostDto;
import com.civicfix.tfg.rest.dtos.conversors.PostConversor;
import com.civicfix.tfg.rest.dtos.request.PostRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/posts")
public class PostController {

    private static final String INSTANCE_NOT_FOUND = "project.exceptions.InstanceNotFoundException";
    private static final String PERMISION_EXCEPTION = "project.exceptions.PermissionException";
    private static final String FORBIDDEN_FILE_TYPE_EXCEPTION = "project.exceptions.ForbiddenFileTypeException";
    private static final String MAX_FILE_SIZE_EXCEPTION = "project.exceptions.MaxFileSizeException";

    private final MessageSource messageSource;
    private final PostService postService;
    private final PostDao postDao;
    private final PermissionChecker permissionChecker;

    public PostController(MessageSource messageSource, PostService postService, PostDao postDao, PermissionChecker permissionChecker) {
        this.messageSource = messageSource;
        this.postService = postService;
        this.postDao = postDao;
        this.permissionChecker = permissionChecker;
    }

    @ExceptionHandler(InstanceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorsDto handleInstanceNotFoundException(InstanceNotFoundException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(INSTANCE_NOT_FOUND, null,
				INSTANCE_NOT_FOUND, locale);

		return new ErrorsDto(errorMessage);

    }

    @ExceptionHandler(ForbiddenFileTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorsDto handleForbiddenFileTypeException(ForbiddenFileTypeException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(FORBIDDEN_FILE_TYPE_EXCEPTION, null,
				FORBIDDEN_FILE_TYPE_EXCEPTION, locale);

		return new ErrorsDto(errorMessage);

    }

    @ExceptionHandler(MaxFileSizeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorsDto handleMaxFileSizeException(MaxFileSizeException exception, Locale locale) {

        String errorMessage = messageSource.getMessage(MAX_FILE_SIZE_EXCEPTION, null,
                MAX_FILE_SIZE_EXCEPTION, locale);

        return new ErrorsDto(errorMessage);

    }

    @ExceptionHandler(PermissionException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorsDto handlePermissionException(PermissionException exception, Locale locale) {

		String errorMessage = messageSource.getMessage(PERMISION_EXCEPTION, null,
				PERMISION_EXCEPTION, locale);

		return new ErrorsDto(errorMessage);

    }

    @PostMapping("/add")
    public ResponseEntity<PostDto> addPost(
        @AuthenticationPrincipal JwtInfo jwtInfo, 
        @RequestPart String postData,
        @RequestPart List<MultipartFile> files
        ) throws InstanceNotFoundException, IOException, ForbiddenFileTypeException, MaxFileSizeException {
        User author = permissionChecker.checkUser(jwtInfo.getUserId());

        PostRequestDto postRequestDto = new ObjectMapper().readValue(postData, PostRequestDto.class);

        Post post = new Post(postRequestDto.getTitle(), postRequestDto.getContent(), postRequestDto.getLocation(), postRequestDto.getLatitude(), postRequestDto.getLongitude(), author);

        if(author.getRole().equals(User.Role.ADMIN)) {
            post.setCategory(Post.Category.ADMINISTRATION);
        }

        postService.createPost(post, files, postRequestDto.getRelatedPostIds());

        return ResponseEntity.ok(PostConversor.toPostDto(post));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@AuthenticationPrincipal JwtInfo jwtInfo, @PathVariable Long postId) throws InstanceNotFoundException, PermissionException, IOException {
        
        permissionChecker.checkUser(jwtInfo.getUserId());
        postService.deletePost(postId, jwtInfo.getUserId());
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDto> putMethodName
    (@AuthenticationPrincipal JwtInfo jwtInfo, 
    @PathVariable Long postId, @RequestPart String postData, 
    @RequestPart(required = false) List<MultipartFile> files) 
    throws InstanceNotFoundException, IOException, PermissionException, ForbiddenFileTypeException, MaxFileSizeException
    {
        User author = permissionChecker.checkUser(jwtInfo.getUserId());

        PostRequestDto postRequestDto = new ObjectMapper().readValue(postData, PostRequestDto.class);

        Post post = new Post(postRequestDto.getTitle(), postRequestDto.getContent(), postRequestDto.getLocation(), postRequestDto.getLatitude(), postRequestDto.getLongitude(), author);

        postService.update(postId, post, postRequestDto.getExistingFiles(), files, postRequestDto.getRelatedPostIds());

        return ResponseEntity.ok(PostConversor.toPostDto(post));
    }   

    @GetMapping("/feed")
    public ResponseEntity<PageDto<PostDto>> getFeed(
        @AuthenticationPrincipal JwtInfo jwtInfo,
        @RequestParam int category,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(required = false, defaultValue = "date") String sortBy,
        @RequestParam(required = false, defaultValue = "desc") String sortDirection
    ) {
        Post.Category categoryEnum = category == 1 ? Post.Category.ADMINISTRATION : Post.Category.USER;

        Sort sort;
        Pageable pageable;

        if (sortBy.equals("date")) {
            sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        } else {
            sort = Sort.by(Sort.Direction.fromString(sortDirection), "date"); // default sort fallback
        }

        pageable = PageRequest.of(page, size, sort);

        Page<Post> postsPage = postService.getPostsByCategory(categoryEnum, pageable);
        List<Long> postIds = postDao.findAll().stream()
            .map(Post::getId).toList();

        Long currentUserId = jwtInfo != null ? jwtInfo.getUserId() : null;

        Map<Long, Integer> voteTotals = new HashMap<>();
        Map<Long, Integer> userVotes = new HashMap<>();

        if (!postIds.isEmpty()) {
            voteTotals = postService.getVoteSumsByPostIds(postIds);

            if (currentUserId != null) {
                userVotes = postService.getVotesByUserIdAndOPostVotesIn(currentUserId, postIds);
            }
        }

        Page<PostDto> dtoPage;

        if (sortBy.equals("votes")) {
            List<PostDto> posts = PostConversor.toPostDtoList(
                postService.getPostsByCategory(categoryEnum), voteTotals, userVotes
            );

            List<PostDto> sortedByVotes = posts.stream()
                .sorted((a, b) -> {
                    int comparison = Integer.compare(b.getVotes(), a.getVotes());
                    return sortDirection.equalsIgnoreCase("desc") ? comparison : -comparison;
                })
                .toList();

            int start = page * size;
            int end = Math.min(start + size, sortedByVotes.size());
            List<PostDto> pageContent = sortedByVotes.subList(start, end);

            dtoPage = new PageImpl<>(pageContent, PageRequest.of(page, size), sortedByVotes.size());

        } else if (sortBy.equals("solved")) {
            List<PostDto> posts = PostConversor.toPostDtoList(
                postService.getPostsByCategory(categoryEnum), voteTotals, userVotes
            );

            List<PostDto> sortedBySolved = posts.stream()
                .sorted(Comparator.comparing(PostDto::isSolved).reversed())
                .toList();

            if (sortDirection.equalsIgnoreCase("asc")) {
                sortedBySolved = sortedBySolved.stream()
                    .sorted(Comparator.comparing(PostDto::isSolved))
                    .toList();
            }

            int start = page * size;
            int end = Math.min(start + size, sortedBySolved.size());
            List<PostDto> pageContent = sortedBySolved.subList(start, end);

            dtoPage = new PageImpl<>(pageContent, PageRequest.of(page, size), sortedBySolved.size());


        }else {
            dtoPage = PostConversor.toPostDtoPage(postsPage, voteTotals, userVotes);
        }

        PageDto<PostDto> response = new PageDto<>(
            dtoPage.getContent(),
            dtoPage.getNumber(),
            dtoPage.getSize(),
            dtoPage.getTotalElements(),
            dtoPage.getTotalPages(),
            dtoPage.isLast(),
            dtoPage.isFirst(),
            dtoPage.isEmpty()
        );

        return ResponseEntity.ok(response);
    }



    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@AuthenticationPrincipal JwtInfo jwtInfo ,@PathVariable Long postId) throws InstanceNotFoundException{
        
        User user = null;
        if (jwtInfo != null) {
            user = permissionChecker.checkUser(jwtInfo.getUserId());
        }
        

        Map<Long, Integer> voteSum = postService.getVoteSumsByPostIds(List.of(postId));
        Map<Long, Integer> userVotes = new HashMap<>();

        if (user != null) {
            userVotes = postService.getVotesByUserIdAndOPostVotesIn(user.getId(), List.of(postId));
        }

        Post post = postService.getPostById(postId);

        return ResponseEntity.ok(PostConversor.toPostDto(post, voteSum.getOrDefault(postId, 0), userVotes.getOrDefault(postId, 0)));
    }

    @GetMapping("/last")
    public ResponseEntity<List<PostDto>> getLastPosts() {
        List<Post> lastPosts = postService.getLastPosts();
        return ResponseEntity.ok(PostConversor.toPostDtoList(lastPosts, new HashMap<>(), new HashMap<>()));
    }

    @GetMapping("/myposts")
    public ResponseEntity<List<PostDto>> getUserPosts(@AuthenticationPrincipal JwtInfo jwtInfo) throws InstanceNotFoundException {

        User user = permissionChecker.checkUser(jwtInfo.getUserId());
        List<Post> userPosts = postService.getPostsByAuthor(user.getId());

        userPosts.sort(Comparator.comparing(Post::getDate).reversed());

        return ResponseEntity.ok(PostConversor.toPostDtoList(userPosts, new HashMap<>(), new HashMap<>()));
    }

    // Vote methods

    @PostMapping("/{postId}/vote")
    public ResponseEntity<PostDto> vote(@AuthenticationPrincipal JwtInfo jwtInfo, @PathVariable Long postId, @RequestParam int vote) throws InstanceNotFoundException {
        User author = permissionChecker.checkUser(jwtInfo.getUserId());
        if (vote < -1 || vote > 1) {
            throw new IllegalArgumentException("Vote must be -1, 0, or 1");
        }

        
        postService.votePost(postId, author.getId(), vote);
        
        Post post = postService.getPostById(postId);
        
        Map<Long, Integer> voteSum = postService.getVoteSumsByPostIds(List.of(postId));
        PostDto updatedPost = PostConversor.toPostDto(post, voteSum.getOrDefault(postId, 0), vote);
        updatedPost.setUserVote(vote); 
        
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}/vote")
    public ResponseEntity<PostDto> deleteVote(@AuthenticationPrincipal JwtInfo jwtInfo, @PathVariable Long postId) throws InstanceNotFoundException, PermissionException {
        permissionChecker.checkUser(jwtInfo.getUserId());
        postService.deleteVote(jwtInfo.getUserId(), postId);
        Map<Long, Integer> voteSum = postService.getVoteSumsByPostIds(List.of(postId));
        PostDto postDto = PostConversor.toPostDto(postService.getPostById(postId), voteSum.getOrDefault(postId, 0),  0);
        return ResponseEntity.ok(postDto);
    }

    @GetMapping("/postSelect")
    public ResponseEntity<List<PostDao.PostToListView>> getPostsBySolved(
        @RequestParam(defaultValue = "false") boolean isSolved) {

        List<PostDao.PostToListView> posts = postService.getPostsToListViewBySolved(isSolved);
        return ResponseEntity.ok(posts);
    }
}
