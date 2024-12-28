package com.coursitory.app.Services;

import com.coursitory.app.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    JwtService jwtService;

    @Value("${admin.credentials.username}")
    private String adminName;

    @Value("${admin.credentials.password}")
    private String adminPassword;
    public String verifyAdmin(User user) {
        if(user.getUsername().equals(adminName) && user.getPassword().equals(adminPassword)){
            return jwtService.generateToken(adminName,true);
        }
        return "Invalid Credentials";
    }
}
