package com.airbnb.controller;

import com.airbnb.entity.Images;
import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.ImagesRepository;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.service.BucketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private ImagesRepository imagesRepository;
    private PropertyRepository propertyRepository;
    private BucketService service;

    public ImageController(ImagesRepository imagesRepository, PropertyRepository propertyRepository, BucketService service) {
        this.imagesRepository = imagesRepository;
        this.propertyRepository = propertyRepository;
        this.service = service;
    }

    @PostMapping(path = "/upload/file/{bucketName}/property/{propertyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam MultipartFile file,
                                             @PathVariable String bucketName,
                                             @PathVariable long propertyId,
                                             @AuthenticationPrincipal PropertyUser user) {
        String imageUrl = service.uploadFile(file, bucketName);
        Optional<Property> property = propertyRepository.findById(propertyId);
        if(property.isPresent()) {
            Property property1 = property.get();
            Images images=new Images();
            images.setImageUrl(imageUrl);
            images.setProperty(property1);
            images.setPropertyUser(user);
            Images savedImage = imagesRepository.save(images);
            return new ResponseEntity<>(savedImage, HttpStatus.OK);
        }
        return new ResponseEntity<>("Property is not added", HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
