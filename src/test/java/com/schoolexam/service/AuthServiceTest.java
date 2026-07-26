package com.schoolexam.service;

import com.schoolexam.dto.AuthDtos.*;
import com.schoolexam.model.User;
import com.schoolexam.repository.UserRepository;
import com.schoolexam.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id(1L)
                .username("teacher@school.edu")
                .password("encoded_pass")
                .fullName("Prof. Eleanor Vance")
                .role("TEACHER")
                .build();
    }

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest("teacher@school.edu", "password123");

        when(userRepository.findByUsername("teacher@school.edu")).thenReturn(Optional.of(sampleUser));
        when(tokenProvider.generateToken("teacher@school.edu")).thenReturn("mocked_jwt_token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked_jwt_token", response.getToken());
        assertEquals("teacher@school.edu", response.getUsername());
        assertEquals("Prof. Eleanor Vance", response.getFullName());
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("newteacher@school.edu", "pass123", "Dr. Alan Grant", "TEACHER");

        when(userRepository.existsByUsername("newteacher@school.edu")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(tokenProvider.generateToken("newteacher@school.edu")).thenReturn("new_jwt_token");

        LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("new_jwt_token", response.getToken());
        assertEquals("newteacher@school.edu", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_DuplicateUsername_ThrowsException() {
        RegisterRequest request = new RegisterRequest("teacher@school.edu", "pass123", "Prof. Eleanor", "TEACHER");

        when(userRepository.existsByUsername("teacher@school.edu")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
