package com.coursitory.app.Services;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public interface VideoService {
    public GridFSFile getVideoFile(String videoId) throws IOException;
    public boolean deleteVideo(String videoId);

    public InputStream getVideoInputStream(String videoId) throws IOException;
    public String uploadVideo(MultipartFile file,MultipartFile thumbnail,String title,String description) throws IOException;

    public long getVideoContentLength(String videoId) throws IOException ;

    public String getVideoContentType(String videoId) ;
}
