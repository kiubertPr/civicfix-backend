package com.civicfix.tfg.model.entities;

import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class RelatedPost {

    private Long id;
    private String title;

    public RelatedPost() {
    }

    public RelatedPost(Long id, String title) {
        this.id = id;
        this.title = title;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedPost)) return false;
        RelatedPost that = (RelatedPost) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
