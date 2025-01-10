package com.coursitory.app.Services.impl;

import com.coursitory.app.Entities.Image;
import com.coursitory.app.Entities.Video;
import com.coursitory.app.Repositories.ImageRepository;
import com.coursitory.app.Repositories.VideoRepository;
import com.coursitory.app.Services.VideoService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    ImageRepository imageRepository;

    // Method to fetch the video file by its ID
    public GridFSFile getVideoFile(String videoId) throws IOException {
        Query query = new Query();
        ObjectId objectId = new ObjectId(videoId);
        query.addCriteria(Criteria.where("_id").is(objectId));
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if (gridFSFile != null) {
            return gridFSFile;
        } else {
            throw new IOException("Video not found with ID: " + videoId);
        }
    }

    public boolean deleteVideo(String videoId) {
        try {
            Optional<Video> vid = videoRepository.findById(videoId);
            if (vid.isPresent()) {
//                System.out.println("video present!");
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(vid.get().getFileId()));
                videoRepository.deleteById(videoId);
                GridFSFile gridFSFile = gridFsTemplate.findOne(query);
                if (gridFSFile != null) {
                    gridFsTemplate.delete(query);
                    return true;
                }
                return false;
            } else {
                System.out.println("Video not found!");
                throw new Exception("Video not found!");
            }

        } catch (Exception e) {
            return false;
        }
    }

    // Method to get an InputStream of the video file
    public InputStream getVideoInputStream(String videoId) throws IOException {
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isPresent()) {
            GridFSFile gridFSFile = getVideoFile(video.get().getFileId().toString());
            return gridFsTemplate.getResource(gridFSFile).getInputStream();
        } else {
            throw new IOException("video id invalid");
        }
    }

    public String uploadVideo(MultipartFile file, MultipartFile thumbnail, String title, String description) throws IOException {
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        Video video = new Video();
        video.setFileId(fileId);
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoType(file.getContentType());
        if (thumbnail != null) {
            Image img = new Image(thumbnail.getContentType(), thumbnail.getBytes());
            imageRepository.save(img);
            video.setThumbnail(img.getId());
        }
        videoRepository.save(video);

        return video.getId().toString();
    }

    public long getVideoContentLength(String videoId) throws IOException {
        GridFSFile gridFSFile = getVideoFile(videoRepository.findById(videoId).get().getFileId().toString());
        return gridFSFile.getLength();
    }

    public String getVideoContentType(String videoId) {
        Optional<Video> video = videoRepository.findById(videoId);
        return video.get().getVideoType();
    }


}
