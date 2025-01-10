package com.coursitory.app.Entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videos")
public class Video {

    // Getters and setters
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    @NonNull
    private ObjectId fileId;

    private String title;
    private String description;
    private String videoType;

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId thumbnail;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public @NonNull ObjectId getFileId() {
        return fileId;
    }

    public void setFileId(@NonNull ObjectId fileId) {
        this.fileId = fileId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public ObjectId getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ObjectId thumbnail) {
        this.thumbnail = thumbnail;
    }
}
