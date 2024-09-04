package com.airbnb.controller;

import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.entity.Review;
import com.airbnb.repository.PropertyRepository;
import com.airbnb.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private PropertyRepository propertyRepository;

    private ReviewRepository reviewRepository;
    public ReviewController(PropertyRepository propertyRepository, ReviewRepository reviewRepository) {
        this.propertyRepository = propertyRepository;
        this.reviewRepository = reviewRepository;
    }

    @PostMapping("/addReview/{propertyId}")
    public ResponseEntity<String > addReview(@PathVariable long propertyId, @RequestBody Review review, @AuthenticationPrincipal PropertyUser user){



        Optional<Property> property = propertyRepository.findById(propertyId);
        Property property1 = property.get();
        Review r = reviewRepository.findReviewByUSer(property1, user);

        if(r!=null){
            return new ResponseEntity<>("You already given review for this property",HttpStatus.BAD_REQUEST);
        }
        review.setProperty(property1);
        review.setPropertyUser(user);
        reviewRepository.save(review);

        return new ResponseEntity<>("Review is added", HttpStatus.CREATED);

    }

    @GetMapping("/allReviews")
    public ResponseEntity<List<Review>> allReviews(@AuthenticationPrincipal PropertyUser user){
        List<Review> allReviews = reviewRepository.findByPropertyUser(user);


        return new ResponseEntity<>(allReviews,HttpStatus.OK);
    }
}
