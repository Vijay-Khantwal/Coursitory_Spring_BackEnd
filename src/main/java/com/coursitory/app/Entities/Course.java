package com.coursitory.app.Entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;


@Document(collection = "courses")
public class Course {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @NonNull
    private String title;
    private String description;
    private List<String> tags;
    private double rating = 0;
    private List<String> videoList;
    private List<String> pdfList ;
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId thumbnail;

    public Course(@NonNull String title, String description, List<String> tags) {
        this.title = title;
        this.description = description;
        this.tags = tags;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public @NonNull String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<String> videoList) {
        this.videoList = videoList;
    }

    public List<String> getPdfList() {
        return pdfList;
    }

    public void setPdfList(List<String> pdfList) {
        this.pdfList = pdfList;
    }

    public ObjectId getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ObjectId thumbnail) {
        this.thumbnail = thumbnail;
    }
}
