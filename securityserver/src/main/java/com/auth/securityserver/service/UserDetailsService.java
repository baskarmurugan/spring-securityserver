package com.auth.securityserver.service;

import com.auth.securityserver.model.UserDetails;
import com.auth.securityserver.repository.UserRepository;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserDetailsService  {

    @Autowired
    private UserRepository userDetailsRepository;


    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService(){

        return new org.springframework.security.core.userdetails.UserDetailsService() {
            @Override
            public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                UserDetails user = userDetailsRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                return User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build();
            }
        };
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              }
}