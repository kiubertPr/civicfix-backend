package com.civicfix.tfg.rest.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.civicfix.tfg.model.entities.RelatedPost;
import com.civicfix.tfg.model.entities.Post.Category;
public class PostDto {

    private Long id;
    private String title;
    private String content;
    private String location;
    private double latitude;
    private double longitude;
    private LocalDateTime date;
    private boolean solved;
    private int votes;

    private List<RelatedPost> relatedPosts;
    private List<String> images;
    private List<String> files;

    private Integer userVote = 0;

    private UserDto author;
    private Category category;

    public PostDto() {
        this.images = new ArrayList<>();
        this.relatedPosts = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public PostDto(Long id, String title, String content, String location, double latitude, double longitude, LocalDateTime date, boolean solved, List<RelatedPost> relatedPosts, List<String> images, List<String> files, UserDto author, Category category, int votes, int userVote) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.solved = solved;
        this.author = author;
        this.category = category;
        this.relatedPosts = relatedPosts != null ? relatedPosts : new ArrayList<>();
        this.images = images != null ? images : new ArrayList<>();
        this.files = files!= null ? files : new ArrayList<>();
        this.votes = votes;
        this.userVote = userVote;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<String> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getFiles() {
        return Collections.unmodifiableList(files);
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public UserDto getAuthor() {
        return author;
    }

    public void setAuthor(UserDto author) {
        this.author = author;
    }

    public Category getCategory() {
        return category; 
    } 

    public void setCategory(Category category) { 
        this.category = category; 
    }

    public void addImage(String image) {
        if (image != null) {
            this.images.add(image);
        }
    }

    public void removeImage(String image) {
        if (image != null) {
            this.images.remove(image);
        }
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getUserVote() {
        return userVote;
    }

    public void setUserVote(int userVote) {
        this.userVote = userVote;
    }

    public boolean isSolved() {
        return solved;
    }
    
    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public List<RelatedPost> getRelatedPosts() {
        return Collections.unmodifiableList(relatedPosts);
    }

    public void setRelatedPosts(List<RelatedPost> relatedPosts) {
        this.relatedPosts = relatedPosts != null ? relatedPosts : new ArrayList<>();
    }
    
}
