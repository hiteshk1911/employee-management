package com.hitesh.employeemanagement.auth;

import com.hitesh.employeemanagement.user.Role;
import com.hitesh.employeemanagement.user.User;
import com.hitesh.employeemanagement.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.hitesh.employeemanagement.security.JwtService;
import com.hitesh.employeemanagement.user.User;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(
                request.getUsername())) {

            return ResponseEntity
                    .badRequest()
                    .body(new AuthResponse(
                            "Username already exists"
                    ));
        }

        Role role = request.getRole() == null
                ? Role.ROLE_USER
                : request.getRole();

        User user = User.builder()
                .username(request.getUsername())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .role(role)
                .build();

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse(
                        "User registered successfully"
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        User user = userRepository.findByUsername(
                request.getUsername()
        ).orElseThrow(() ->
                new RuntimeException("Invalid credentials")
        );

        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if (!matches) {
            throw new RuntimeException(
                    "Invalid credentials"
            );
        }

        String token =
                jwtService.generateToken(
                        user.getUsername()
                );

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }
}