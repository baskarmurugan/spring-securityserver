package com.auth.securityserver.service;

import com.auth.securityserver.model.AuthResponse;
import com.auth.securityserver.model.JwtAuthenticationResponse;
import com.auth.securityserver.model.LoginRequest;
import com.auth.securityserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userDetailsRepository;
    private final JWTService jwtService;
    private final String userPath = "/api/user/";
    private final String adminPath = "/api/admin/";
    public Optional<String > defaultResponse;
    public com.auth.securityserver.model.UserDetails saveEmp(com.auth.securityserver.model.UserDetails userEntity) {
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
       // userEntity.setRole("user");
        return userDetailsRepository.save(userEntity);
    }

    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            var user = userDetailsRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
            String jwt = jwtService.generateToken(user);
            JwtAuthenticationResponse response = new JwtAuthenticationResponse(jwt);

            System.out.println(jwt);
            return response;
        }catch (Exception e){
            logger.info("ERROR in login {}",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
       //return null;
    }

    public AuthResponse verifyToken(String token) {
        try {

            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // Remove "Bearer " prefix
                System.out.println("Token after stripping Bearer prefix: " + token);
            }

            String username = jwtService.extractUserName(token);
            UserDetails user = userDetailsRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Token is invalid"));

            if (jwtService.isTokenValid(token, user)) {
                logger.info("Token is valid");
                return new AuthResponse(true, username); // or return only true if username is not needed
            }

            throw new IllegalArgumentException("Token is invalid");
        }catch (Exception e){
            e.printStackTrace();
            logger.info("Error verifying token {}",e.getMessage());
            throw  new  RuntimeException("Error verifying token");
        }

    }


    public AuthResponse verifyTokenAuthorization(String token,String path) {
        try {
            System.out.println("Received token: " + token);
            System.out.println("Received token: " + path);

            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // Remove "Bearer " prefix
                System.out.println("Token after stripping Bearer prefix: " + token);
            }

            String username = jwtService.extractUserName(token);
            UserDetails user = userDetailsRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Token is invalid"));

            if (jwtService.isTokenValid(token, user) && customAuthorization(token,path)) {
                logger.info("Token is valid");
                return new AuthResponse(true, username); // or return only true if username is not needed
            }

            throw new IllegalArgumentException("Token is invalid");
        }catch (Exception e){

            logger.info("Error verifying token {}",e.getMessage());
            throw  new  RuntimeException("Error verifying token");
        }

    }
    private boolean customAuthorization(String token, String path){
        String authority = jwtService.extractRole(token);
        String role = extractRoleFromString(authority);

        if ("user".equalsIgnoreCase(role) && path.startsWith(userPath)){
            logger.info("Authorization successful for role: {}" , role);
            return true;
        }
//        else if ("admin".equalsIgnoreCase(role) && path.startsWith(adminPath)) {
//            logger.info("Authorization successful for role: {}" , role);
//            return true;
//        }
        else if ("admin".equalsIgnoreCase(role)){
            return true;
        }
        else if (!path.startsWith(userPath) && !path.startsWith(adminPath)){
            logger.info("Authorization does not invoke as its a global API - PATH = {}" , path);
            return true;
        }
        else {
            logger.info("Authorization Failed for role: {} and path: {}" , role,path);
            return false;
        }
    }
    private String extractRoleFromString(String roleString) {
        Pattern pattern = Pattern.compile("authority=(\\w+)");
        Matcher matcher = pattern.matcher(roleString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    public ResponseEntity<?> getUser(Long id){
        try {
            return ResponseEntity.ok(userDetailsRepository.findById(id)) ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok("User not found");

    }

}
