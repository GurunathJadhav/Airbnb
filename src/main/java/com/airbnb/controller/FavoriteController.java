package com.airbnb.controller;

import com.airbnb.entity.Favorite;
import com.airbnb.entity.Property;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.FavoriteRepository;
import com.airbnb.repository.PropertyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/favorite")
public class FavoriteController {

    private FavoriteRepository favoriteRepository;
    private PropertyRepository propertyRepository;

    public FavoriteController(FavoriteRepository favoriteRepository, PropertyRepository propertyRepository) {
        this.favoriteRepository = favoriteRepository;
        this.propertyRepository = propertyRepository;
    }

    @PostMapping()
    public ResponseEntity<Favorite>addFavorite(@RequestBody Favorite favorite, @AuthenticationPrincipal PropertyUser user) {
        Property property = favorite.getProperty();
        Optional<Property> byId = propertyRepository.findById(property.getId());
        if (byId.isPresent()) {
            Property property1 = byId.get();
            favorite.setProperty(property1);

        }
        favorite.setPropertyUser(user);
        Favorite savedFavorite = favoriteRepository.save(favorite);

        return new ResponseEntity<>(savedFavorite, HttpStatus.CREATED);
    }
}
