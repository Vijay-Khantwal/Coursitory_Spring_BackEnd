package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.Video;
import com.coursitory.app.Repositories.VideoRepository;
import com.coursitory.app.Services.VideoService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

@RestController
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    VideoRepository videoRepository;

    @PostMapping("/upload/video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
                                         @RequestParam("title") String title,
                                         @RequestParam(value = "description",required = false,defaultValue = "No description yet!") String description) throws IOException {
//        System.out.println("/upload "+title+" "+description);
        String videoId = videoService.uploadVideo(file, thumbnail,title,description);
        return ResponseEntity.ok().body("{\"videoId\": \"" + videoId + "\"}");
    }

    @GetMapping("/stream/video/{videoId}")
    public ResponseEntity<Resource> streamVideoRange(
            @PathVariable String videoId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {

        try {
            InputStream videoStream = videoService.getVideoInputStream(videoId);
            long videoLength = videoService.getVideoContentLength(videoId);

            String contentType = videoService.getVideoContentType(videoId);
            System.out.println(contentType);
            System.out.println("header :" + rangeHeader);

            if (rangeHeader == null) {
                HttpHeaders headers = new HttpHeaders();
                return ResponseEntity.status(HttpStatus.OK)
                        .headers(headers)
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(new InputStreamResource(videoStream));
            }

            // Parse Range header
            String range = rangeHeader.replace("bytes=", "");
            long rangeStart = Long.parseLong(range.split("-")[0]);
            long rangeEnd = rangeStart + 1024 * 1024 - 1;

            if (rangeEnd >= videoLength) {
                rangeEnd = videoLength - 1;
            }

            long contentLength = rangeEnd - rangeStart + 1;
            videoStream.skipNBytes(rangeStart);
            rangeEnd = videoLength - 1;

//            byte[] buffer = new byte[(int) contentLength];
//            int read = videoStream.read(buffer, 0, buffer.length);

//          ######## very slow (because of read function above) , reason - inner implementation of loop(ig)
//            System.out.println("read "+read);
//            rangeEnd = rangeStart+ read - 1;
//            contentLength = rangeEnd - rangeStart+1;
//          ########

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + videoLength);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("X-Content-Type-Options", "nosniff");

            return ResponseEntity
                    .status(HttpStatus.PARTIAL_CONTENT)
                    .headers(headers)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(new InputStreamResource(videoStream));

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/video/{videoId}")
    public ResponseEntity<String> deleteVideoById(@PathVariable String videoId) {
        boolean isDeleted = videoService.deleteVideo(videoId);
        if (isDeleted) {
            //success
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Video successfully deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Video not found with given id:- " + videoId);
        }
    }

    @GetMapping("get/metadata/{videoId}")
    public ResponseEntity<Video> getVideoById(@PathVariable ObjectId videoId) {
//        System.out.println("/metadata/"+fileId);
        Optional<Video> video = videoRepository.findById(videoId.toString());

        if(video.isPresent()){
            Video vid = video.get();
            return new ResponseEntity<>(vid, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
