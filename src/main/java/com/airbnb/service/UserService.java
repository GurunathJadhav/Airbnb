package com.airbnb.service;

import com.airbnb.dto.LoginDto;
import com.airbnb.dto.PropertyUserDto;
import com.airbnb.entity.PropertyUser;
import com.airbnb.repository.PropertyUserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private PropertyUserRepository userRepository;

    private JWTService  jwtService;

    public UserService(PropertyUserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public PropertyUser adduser(PropertyUserDto propertyUserDto){
        PropertyUser user=new PropertyUser();
        user.setFirstname(propertyUserDto.getFirstname());
        user.setLastname(propertyUserDto.getLastname());
        user.setUsername(propertyUserDto.getUsername());
        user.setEmail(propertyUserDto.getEmail());
        user.setUserRole(propertyUserDto.getUserRole());
        user.setPassword(BCrypt.hashpw(propertyUserDto.getPassword(),BCrypt.gensalt(10)));

        PropertyUser saved = userRepository.save(user);

        return saved;

    }

    public String verifyLogin(LoginDto loginDto) {
        Optional<PropertyUser> byUsername = userRepository.findByUsername(loginDto.getUsername());
        if(byUsername.isPresent()){
            PropertyUser user = byUsername.get();
            if(BCrypt.checkpw(loginDto.getPassword(),user.getPassword())){
                return jwtService.generateToken(user);
            }
        }
        return null;
    }
}
