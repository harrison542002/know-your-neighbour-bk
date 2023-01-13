package com.kn.jwt.security.oauth.user;

import java.util.Map;

import com.kn.jwt.exception.OAuth2AuthenticationProcessingException;
import com.kn.jwt.model.AuthProvider;

public class OAuth2UserInfoFactory {
public static OAuthUserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
	if(registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())) {
		return new FacebookOAuth2UserInfo(attributes);
	} 
	if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
		return new GoogleOAuth2UserInfo(attributes);
	}
	else {
		throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
	}
}
}
