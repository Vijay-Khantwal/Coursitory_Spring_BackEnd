package com.coursitory.app.Services;

import com.coursitory.app.Entities.User;
import com.coursitory.app.Repositories.UserRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    //    public String register(User user){
//        user.setPassword(encoder.encode(user.getPassword()));
//        userRepository.save(user);
//        return user.getId().toString();
//    }
    public String register(User user) {
        try {
            if (userRepository.findByUsername(user.getUsername()) != null) {
                throw new IllegalArgumentException("Username is already taken.");
            }
        } catch (Exception e) {
            return "Username already taken!";
        }
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user).getId().toString();
    }

    public String verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(user.getUsername(),false);
            return "Jwt Token:- " + token;
        }
        return "Invalid Credentials";
    }


}
