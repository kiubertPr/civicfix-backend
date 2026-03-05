package com.civicfix.tfg.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "Posts")
public class Post {

    public enum Category {
        USER, ADMINISTRATION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String location;
    private Double latitude;
    private Double longitude;
    private LocalDateTime date;
    private boolean solved;

    @ElementCollection
    @CollectionTable(
        name = "posts_images",
        joinColumns = @JoinColumn(name = "postsId")
    )
    @MapKeyColumn(name = "publicId")
    @Column(name = "url")
    private Map<String, String> images = new HashMap<>();

    @ElementCollection
    @CollectionTable(
        name = "posts_files",
        joinColumns = @JoinColumn(name = "postsId")
    )
    @MapKeyColumn(name = "publicId")
    @Column(name = "url")
    private Map<String, String> files = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User author;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ElementCollection
    @CollectionTable(
        name = "related_posts",
        joinColumns = @JoinColumn(name = "postId")
    )
    private List<RelatedPost> relatedPosts = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String content, String location, Double latitude, Double longitude, User author) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.author = author;
        this.category = Category.USER;
        this.solved = false;
        this.date = LocalDateTime.now();
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Map<String,String> getImages() {
        return images;
    }

    public void setImages(Map<String,String> images) {
        this.images = images;
    }

    public Map<String,String> getFiles() {
        return files;
    }

    public void setFiles(Map<String,String> files) {
        this.files = files;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public List<RelatedPost> getRelatedPosts() {
        return relatedPosts;
    }

    public void setRelatedPosts(List<RelatedPost> relatedPosts) {
        this.relatedPosts = relatedPosts;
    }

}
