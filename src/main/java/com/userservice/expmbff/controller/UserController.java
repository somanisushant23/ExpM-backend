package com.userservice.expmbff.controller;

import com.userservice.expmbff.dto.LoginProfileDto;
import com.userservice.expmbff.dto.SuccessResponse;
import com.userservice.expmbff.dto.UserProfileDto;
import com.userservice.expmbff.dto.UserProfileResponse;
import com.userservice.expmbff.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*@PostMapping("signup")
    public ResponseEntity<SuccessResponse> createUserProfile(@Valid @RequestBody UserProfileDto userProfileDto) {
        logger.info("Creating user profile {}", userProfileDto.getEmail());
        return userService.createUserProfile(userProfileDto.getEmail(), userProfileDto.getName(), userProfileDto.getPassword());
    }

    @PostMapping("signin")
    public UserProfileResponse loginUser(@Valid @RequestBody LoginProfileDto loginProfileDto) throws IncorrectDataException, HttpServerErrorException {
        logger.info("Login in user {}", loginProfileDto.getEmail());
        return userService.loginUser(loginProfileDto.getEmail(), loginProfileDto.getPassword());
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getUserProfile(@RequestHeader("email") String email) {
        logger.info("Get user profile {}", email);
        return userService.getUserProfile(email);
    }*/
}
