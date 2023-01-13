package com.kn.jwt.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app")
@Component
public class AppProperties {
	private final Auth auth = new Auth();
	private final OAuth2 oauth2 = new OAuth2();

	@Getter
	@Setter
	public static class Auth {
		private String tokenSecret;
		private String tokenExpirationMsec;
	}

	@Getter
	@Setter
	public static class OAuth2
	{
		private List<String> authorizedRedirectUris = new ArrayList<String>();
	}
	
	public Auth getAuth() {
		return auth;
	}
	public OAuth2 getOAuth2() {
		return oauth2;
	}

}
