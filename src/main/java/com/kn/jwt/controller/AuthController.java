package com.kn.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kn.jwt.data.User;
import com.kn.jwt.exception.BadRequestException;
import com.kn.jwt.model.AuthProvider;
import com.kn.jwt.payload.AuthResponse;
import com.kn.jwt.payload.LoginRequest;
import com.kn.jwt.payload.SignUpRequest;
import com.kn.jwt.repo.UserRepository;
import com.kn.jwt.utils.JwtUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.createToken(authentication);
		return ResponseEntity.ok(new AuthResponse(token));
	}
	
	@PostMapping("/signUp")
	public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest){
		if(userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new BadRequestException("Email address already in use.");
        }
		User user = new User();
		user.setName(signUpRequest.getName());
		user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		user.setProvider(AuthProvider.local);
		user.setEmail(signUpRequest.getEmail());
		User result = userRepository.save(user);
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(result.getEmail(),  signUpRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.createToken(authentication);
		return ResponseEntity.ok(new AuthResponse(token));
	}
}
