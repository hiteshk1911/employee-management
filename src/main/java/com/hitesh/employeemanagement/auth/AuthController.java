package com.hitesh.employeemanagement.auth;

import com.hitesh.employeemanagement.security.JwtService;
import com.hitesh.employeemanagement.user.Role;
import com.hitesh.employeemanagement.user.User;
import com.hitesh.employeemanagement.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

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

        refreshTokenService.deleteAllUserTokens(
                user.getUsername()
        );

        String accessToken =
                jwtService.generateToken(
                        user.getUsername()
                );

        RefreshToken refreshToken =
                refreshTokenService.createToken(
                        user.getUsername()
                );

        return ResponseEntity.ok(
                new LoginResponse(
                        accessToken,
                        refreshToken.getToken()
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {

        RefreshToken refreshToken =
                refreshTokenService.validateToken(
                        request.getRefreshToken()
                );

        String accessToken =
                jwtService.generateToken(
                        refreshToken.getUsername()
                );

        return ResponseEntity.ok(
                new RefreshResponse(accessToken)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(
            @Valid @RequestBody LogoutRequest request) {

        refreshTokenService.revokeToken(
                request.getRefreshToken()
        );

        return ResponseEntity.ok(
                new AuthResponse("Logged out successfully")
        );
    }
}