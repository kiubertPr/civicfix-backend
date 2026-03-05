package com.civicfix.tfg.rest.dtos.request;

import java.util.List;

public class PostRequestDto {

    private String title;
    private String content;
    private String location;
    private Double latitude;
    private Double longitude;
    private List<String> existingFiles;
    private List<Long> relatedPostIds;

    public PostRequestDto() {
    }

    public PostRequestDto(String title, String content, String location, Double latitude, Double longitude, List<String> existingFiles, List<Long> relatedPostIds) {
        this.title = title;
        this.content = content;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.existingFiles = existingFiles;
        this.relatedPostIds = relatedPostIds;
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

    public List<String> getExistingFiles() {
        return existingFiles;
    }

    public void setExistingFiles(List<String> existingFiles) {
        this.existingFiles = existingFiles;
    }

    public List<Long> getRelatedPostIds() {
        return relatedPostIds;
    }

    public void setRelatedPostIds(List<Long> relatedPostIds) {
        this.relatedPostIds = relatedPostIds;
    }
}
