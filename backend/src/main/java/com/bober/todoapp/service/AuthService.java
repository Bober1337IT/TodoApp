package com.bober.todoapp.service;

import com.bober.todoapp.domain.dto.user.AuthResponseDTO;
import com.bober.todoapp.domain.dto.user.LoginRequestDTO;
import com.bober.todoapp.domain.dto.user.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);
}
