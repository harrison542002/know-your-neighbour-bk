package com.kn.jwt.security.oauth;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OAuth2FailHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String targetUrl = UriComponentsBuilder.fromUriString(request.getParameter("redirect_uri"))
				.queryParam("error",
						URLEncoder.encode(((OAuth2AuthenticationException) exception).getMessage()).toString())
				.build().toUriString();
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
