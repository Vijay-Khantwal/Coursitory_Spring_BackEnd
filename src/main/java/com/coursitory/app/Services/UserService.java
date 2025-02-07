package com.coursitory.app.Services;

import com.coursitory.app.Entities.User;
import com.coursitory.app.Repositories.UserRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public String register(User user) {
        try {
            if (userRepository.findByUsername(user.getUsername()) != null) {
                throw new IllegalArgumentException("Username is already taken.");
            }
        } catch (Exception e) {
            return null;
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnrolled(new ArrayList<>());
        return userRepository.save(user).getId().toString();
    }

    public String verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(user.getUsername(),false);
            return token;
        }
        return null;
    }


    public void save(User user) {
        userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Value("${admin.credentials.username}")
    private String adminName;

    @Value("${admin.credentials.password}")
    private String adminPassword;
    public String verifyAdmin(User user) {
        if(user.getUsername().equals(adminName) && user.getPassword().equals(adminPassword)){
            return jwtService.generateToken(adminName,true);
        }
        return null;
    }

    public String handleGLogin(String email) {
        String decodedMail = new String(Base64.getDecoder().decode(email));
//        System.out.println(decodedMail);
        User user = findUserByUsername(decodedMail);
        if(user == null){
            user = new User();
            user.setUsername(decodedMail);
            user.setPassword(encoder.encode(decodedMail));
            user.setEnrolled(new ArrayList<>());
            userRepository.save(user);
        }

        return jwtService.generateToken(user.getUsername(),false);
    }
}
