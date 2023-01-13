package com.kn.jwt.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kn.jwt.data.User;
import com.kn.jwt.dto.ProfileInfo;
import com.kn.jwt.repo.UserRepository;
import com.kn.jwt.security.UserPrincipal;

@RestController
public class UserController {
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/profile/me")
	public ResponseEntity<?> getProfile(HttpServletRequest request, Authentication authentication){
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		User user = userRepository.findByEmail(userPrincipal.getEmail()).get();
		ProfileInfo profileInfo = new ProfileInfo(userPrincipal.getName(), userPrincipal.getEmail(), user.getImageUrl());
		return ResponseEntity.ok(profileInfo);
	}
}
