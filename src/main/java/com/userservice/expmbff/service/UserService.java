package com.userservice.expmbff.service;

import com.userservice.expmbff.configs.JwtUtils;
import com.userservice.expmbff.dto.SuccessResponse;
import com.userservice.expmbff.dto.UserProfileResponse;
import com.userservice.expmbff.entity.UserEntity;
import com.userservice.expmbff.entity.enums.UserLimit;
import com.userservice.expmbff.exceptions.IncorrectDataException;
import com.userservice.expmbff.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final JwtUtils jwtUtils;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

   private final UserRepository userRepository;

   public UserService(UserRepository userRepository, JwtUtils jwtUtils) {
       this.userRepository = userRepository;
       this.jwtUtils = jwtUtils;
   }

   public ResponseEntity<SuccessResponse> createUserProfile(String email, String name, String password) {
       //TODO add email verification
       if(!userRepository.existsByEmail(email)) {
           String encryptedPwd = bCryptPasswordEncoder.encode(password);
           userRepository.save(new UserEntity(name, email, encryptedPwd, false, true, null, UserLimit.FREE_USER.name()));
           logger.info("User profile created for email: {}", email);
           SuccessResponse response = new SuccessResponse("User profile created successfully");
           return ResponseEntity.ok(response);
       } else {
              logger.warn("User profile creation failed: Email {} already exists", email);
              SuccessResponse response = new SuccessResponse("Email already exists");
              return ResponseEntity.status(409).body(response);
       }
   }

    public UserProfileResponse loginUser(String email, String password) throws IncorrectDataException, HttpServerErrorException {
        Optional<UserEntity> userProfileEntity = userRepository.findByEmail(email);
        if(userProfileEntity.isEmpty()) {
            logger.info("User not found during login!!");
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
        }
        UserEntity user = userProfileEntity.get();
        if(bCryptPasswordEncoder.matches(password, user.getPassword())) {
            logger.info("User logged in {}", user.getEmail());
            //share Jwt token
            UserProfileResponse userProfileResponse = new UserProfileResponse();
            userProfileResponse.setEmail(user.getEmail());
            userProfileResponse.setName(user.getName());
            String token = jwtUtils.createToken(user.getEmail(), getClaims(user));
            userProfileResponse.setToken(token);
            return userProfileResponse;

        } else {
            // throw incorrect password exception
            throw new IncorrectDataException("Unauthorized");
        }
    }

    private Map<String, String> getClaims(UserEntity userEntity) {
        Map<String, String> claims = new HashMap<>();
        claims.put("privilege", userEntity.getPrivilege());
        claims.put("name", userEntity.getName());
        claims.put("email", userEntity.getEmail());
        return claims;
    }
}
