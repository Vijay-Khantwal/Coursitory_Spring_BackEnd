package com.coursitory.app.Config;

import com.coursitory.app.Services.JwtService;
import com.coursitory.app.Services.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext context;

    // These values will be injected from application.properties
    @Value("${admin.credentials.username}")
    private String adminName;

    @Value("${admin.credentials.password}")
    private String adminPassword;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
//        System.out.println(authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUserName(token);
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("Unauthorized: Token has expired");
                return;
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                response.getWriter().write("Bad Request: Invalid token");
                return;
            }
        }
//        System.out.println(username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Boolean isAdmin = jwtService.extractIsAdmin(token);

            try {
                // Handle admin authentication
                if (isAdmin != null && isAdmin) {
                    if (username.equals(adminName)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                username, null, List.of(new SimpleGrantedAuthority("ADMIN")));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
                        response.getWriter().write("Forbidden: Invalid Admin credentials");
                        return;
                    }
                } else {
                    // Handle regular user authentication
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, List.of(new SimpleGrantedAuthority("USER")));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    } else {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                        response.getWriter().write("Unauthorized: Token validation failed");
                        return;
                    }
                }
            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
                response.getWriter().write("Unauthorized: Token has expired");
                return;
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                response.getWriter().write("Bad Request: Token processing error");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
