package com.website.security.service;

import com.website.security.data.dto.Role;
import com.website.security.data.dto.User;
import com.website.security.data.request.AuthenticateRequest;
import com.website.security.data.request.RegisterRequest;
import com.website.security.data.response.AuthenticationResponse;
import com.website.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public AuthenticationResponse register(RegisterRequest request){
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        var user = User
                .builder()
                .username(request.getUsername())
                .name(request.getName())
                .password(request.getPassword())
                .age(request.getAge())
                .balance(request.getBalance())
                .role(Role.USER)
                .build();
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                   request.getUsername(), request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }
}
