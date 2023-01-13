package com.kn.jwt.security.oauth.user;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuthUserInfo{

	private Map<String, Object> attributes;
	
	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
		this.attributes = attributes;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return (String) attributes.get("sub");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return (String) attributes.get("name");
	}

	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return (String) attributes.get("email");
	}

	@Override
	public String getImageUrl() {
		// TODO Auto-generated method stub
		return (String) attributes.get("picture");
	}

}
