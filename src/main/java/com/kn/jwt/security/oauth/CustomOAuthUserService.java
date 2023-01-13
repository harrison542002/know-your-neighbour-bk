package com.kn.jwt.security.oauth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kn.jwt.data.User;
import com.kn.jwt.exception.OAuth2AuthenticationProcessingException;
import com.kn.jwt.model.AuthProvider;
import com.kn.jwt.repo.UserRepository;
import com.kn.jwt.security.UserPrincipal;
import com.kn.jwt.security.oauth.user.OAuth2UserInfoFactory;
import com.kn.jwt.security.oauth.user.OAuthUserInfo;

@Service
public class CustomOAuthUserService extends DefaultOAuth2UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest)
			throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		try {
			return processOAuthUser(userRequest, oAuth2User);
		} catch (OAuth2AuthenticationProcessingException e) {
			throw new OAuth2AuthenticationException(new OAuth2Error(HttpStatus.CONFLICT.toString()), e.getMsg());
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception ex) {
			// Throwing an instance of AuthenticationException will trigger the
			// OAuth2AuthenticationFailureHandler
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User processOAuthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
		OAuthUserInfo oAuthUserInfo = OAuth2UserInfoFactory
				.getOAuth2UserInfo(userRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
		System.out.println(oAuthUserInfo.getAttributes());
		if (StringUtils.isEmpty(oAuthUserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		Optional<User> userOptional = userRepository.findByEmail(oAuthUserInfo.getEmail());
		User user;
		if (userOptional.isPresent()) {
			user = userOptional.get();
			if (!user.getProvider()
					.equals(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId()))) {
				throw new OAuth2AuthenticationProcessingException(
						"Looks like you're signed up with " + userRequest.getClientRegistration().getRegistrationId()
								+ " account. Please use your " + user.getProvider() + " account to login");
			}
			user = updateExistingUser(user, oAuthUserInfo);
		} else {
			user = registerNewUser(userRequest, oAuthUserInfo);
		}
		return UserPrincipal.create(user, oAuth2User.getAttributes());
	}

	private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuthUserInfo oAuth2UserInfo) {
		User user = new User();

		user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
		user.setProviderId(oAuth2UserInfo.getId());
		user.setName(oAuth2UserInfo.getName());
		user.setEmail(oAuth2UserInfo.getEmail());
		user.setImageUrl(oAuth2UserInfo.getImageUrl());
		return userRepository.save(user);
	}

	private User updateExistingUser(User existingUser, OAuthUserInfo oAuth2UserInfo) {
		existingUser.setName(oAuth2UserInfo.getName());
		existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
		return userRepository.save(existingUser);
	}
}
