package com.microservice.auth.config;

import com.microservice.auth.entity.UserCredentials;
import com.microservice.auth.repository.UserCredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserCredentialsRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserCredentials> credential = repository.findByName(username);
        return credential.map(CustomUserDetails::new).orElseThrow(()-> new UsernameNotFoundException("user not found with name: " +username));
    }
}
