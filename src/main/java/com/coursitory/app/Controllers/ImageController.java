package com.coursitory.app.Controllers;

import com.coursitory.app.Entities.Image;
import com.coursitory.app.Repositories.ImageRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ImageController {

    @Autowired
    ImageRepository imageRepository;

    @GetMapping("/get/image/{imageId}")
    public ResponseEntity<Image> getImage(@PathVariable String imageId){
        Optional<Image> img = imageRepository.findById(new ObjectId(imageId));
        if(img.isPresent()){
            return new ResponseEntity<>(img.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
