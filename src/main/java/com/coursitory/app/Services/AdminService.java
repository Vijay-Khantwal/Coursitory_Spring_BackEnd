package com.coursitory.app.Services;

import com.coursitory.app.Entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    JwtService jwtService;


}
