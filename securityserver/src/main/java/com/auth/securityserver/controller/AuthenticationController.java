package com.auth.securityserver.controller;
import com.auth.securityserver.model.AuthResponse;
import com.auth.securityserver.model.JwtAuthenticationResponse;
import com.auth.securityserver.model.LoginRequest;
import com.auth.securityserver.model.UserDetails;
import com.auth.securityserver.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public JwtAuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        System.out.println(loginRequest.toString());
        return authenticationService.login(loginRequest);
    }

    @GetMapping("/verifyAuthorization")
    public AuthResponse verifyTokenAuthorization(@RequestHeader("Authorization") String token, @RequestParam("path") String path) {
        return authenticationService.verifyTokenAuthorization(token,path);
    }

    @GetMapping("/verify")
    public AuthResponse verifyToken(@RequestHeader("Authorization") String token) {
        return authenticationService.verifyToken(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDetails> signUp(@RequestBody UserDetails userEntity){
        return ResponseEntity.ok(authenticationService.saveEmp(userEntity));
    }

    @GetMapping("/getUser")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        return (authenticationService.getUser(id));
    }

    @GetMapping("/test")
    public ResponseEntity<?> test()
    {

        throw new ArithmeticException();

       // return (authenticationService.getUser(id));
    }
}