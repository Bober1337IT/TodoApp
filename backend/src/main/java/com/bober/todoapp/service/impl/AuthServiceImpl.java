package com.bober.todoapp.service.impl;

import com.bober.todoapp.domain.dto.user.AuthResponseDTO;
import com.bober.todoapp.domain.dto.user.LoginRequestDTO;
import com.bober.todoapp.domain.dto.user.RegisterRequestDTO;
import com.bober.todoapp.domain.entity.User;
import com.bober.todoapp.exception.EmailAlreadyExistsException;
import com.bober.todoapp.exception.InvalidCredentialsException;
import com.bober.todoapp.repository.UserRepository;
import com.bober.todoapp.security.JwtService;
import com.bober.todoapp.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User(
                null,
                request.email(),
                passwordEncoder.encode(request.password()),
                Instant.now()
        );

        User saved = userRepository.save(user);
        String token = jwtService.createToken(saved.getId(), saved.getEmail());
        return AuthResponseDTO.bearer(token, saved.getEmail());
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.createToken(user.getId(), user.getEmail());
        return AuthResponseDTO.bearer(token, user.getEmail());
    }
}
