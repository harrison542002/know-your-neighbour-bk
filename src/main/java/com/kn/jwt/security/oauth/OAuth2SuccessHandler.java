package com.kn.jwt.security.oauth;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import com.kn.jwt.config.AppProperties;
import com.kn.jwt.exception.BadRequestException;
import com.kn.jwt.utils.JwtUtils;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private JwtUtils tokenProvider;

	private AppProperties appProperties;

	private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

	@Autowired
	public OAuth2SuccessHandler(JwtUtils tokenProvider, AppProperties appProperties,
			OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
		this.tokenProvider = tokenProvider;
		this.appProperties = appProperties;
		this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String redirectURL = request.getParameter("redirect_uri");
		String targetUrl = determineTargetUrl(redirectURL, response, authentication);
		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}
		clearAuthenticationAttributes(request);
		getRedirectStrategy().sendRedirect(request, response, 
				targetUrl);
		
	}

	protected String determineTargetUrl(String redirectURL, HttpServletResponse response,
			Authentication authentication) {
		if (StringUtils.isEmpty(redirectURL)) {
			throw new BadRequestException(
					"Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}
		String token = tokenProvider.createToken(authentication);
		
		return UriComponentsBuilder.fromUriString(redirectURL).queryParam("token", token).build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
	}

	private boolean isAuthorizedRedirectedUrl(String uri) {
		URI clientRedirectUri = URI.create(uri);
		System.out.println("Authorize URL : " + appProperties.getOAuth2().getAuthorizedRedirectUris().toString());
		return appProperties.getOAuth2().getAuthorizedRedirectUris().stream().anyMatch(authorizedUri -> {
			URI authorizedURI = URI.create(authorizedUri);
			if (authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
					&& authorizedURI.getPort() == clientRedirectUri.getPort()) {
				return true;
			}
			return false;
		});
	}
}
