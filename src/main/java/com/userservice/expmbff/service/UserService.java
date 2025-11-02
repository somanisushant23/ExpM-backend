package com.userservice.expmbff.service;

import com.userservice.expmbff.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

   private final UserRepository userRepository;

   public UserService(UserRepository userRepository) {
       this.userRepository = userRepository;
   }

   

}
