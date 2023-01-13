package com.kn.jwt.security.oauth;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CustomRequestResolver implements OAuth2AuthorizationRequestResolver {

	private final OAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver;
	public CustomRequestResolver(OAuth2AuthorizationRequestResolver oauth2AuthorizationRequestResolver) {
		this.oauth2AuthorizationRequestResolver = oauth2AuthorizationRequestResolver;
	}
	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		OAuth2AuthorizationRequest oAuth2AuthorizationRequest = this.oauth2AuthorizationRequestResolver
				.resolve(request);
		return processAdditionalParameters(oAuth2AuthorizationRequest, request);
	}
	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		OAuth2AuthorizationRequest oAuth2AuthorizationRequest = this.oauth2AuthorizationRequestResolver.resolve(request,
				clientRegistrationId);
		return processAdditionalParameters(oAuth2AuthorizationRequest, request);
	}
	private OAuth2AuthorizationRequest processAdditionalParameters(OAuth2AuthorizationRequest authorizationRequest,
			HttpServletRequest request) {
		//to attach redirect URL
		if (authorizationRequest == null) {
			return null;
		}
		String encodedURL = URLEncoder.encode(request.getParameter("redirectUrl"));
		String redirectUri = UriComponentsBuilder.fromUriString(authorizationRequest.getRedirectUri())
				.queryParam("redirect_uri", encodedURL).build(true).toUriString();
		return OAuth2AuthorizationRequest.from(authorizationRequest).redirectUri(redirectUri).build();
	}
}
