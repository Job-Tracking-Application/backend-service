package com.jobtracking.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobtracking.auth.dto.LoginRequest;
import com.jobtracking.auth.dto.LoginResponse;
import com.jobtracking.auth.dto.RegisterRequest;
import com.jobtracking.auth.entity.User;
import com.jobtracking.config.JwtUtil;
import com.jobtracking.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	public void register(RegisterRequest request) {
		if(userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already exists");
		}
		
		 User user = User.builder()
		            .username(request.getUsername())
		            .email(request.getEmail())
		            .passwordHash(passwordEncoder.encode(request.getPassword()))
		            .roleId(request.getRoleId())
		            .fullname(request.getFullname())
		            .build();
		
		userRepository.save(user);
	}
	
	public LoginResponse login(LoginRequest request) {

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

	    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
	        throw new RuntimeException("Invalid credentials");
	    }

	    String token = jwtUtil.generateToken(user.getId(), user.getRoleId());

	    return new LoginResponse(token, user.getId(), user.getRoleId());
	}
}
