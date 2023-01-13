package com.kn.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.ClassOrderer.OrderAnnotation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.kn.jwt.payload.LoginRequest;

@SpringBootTest
@AutoConfigureMockMvc
class KNApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	private static String JWTToken;

	@Test
	@DisplayName("Test whether user can access user information without JWT token")
	public void testForWithoutJwt() throws Exception {
		// the test is excepted for unauthorized status error code from response.
		mockMvc.perform(get("/profile/me")).andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Test for retrieve of jwt by passing correct user credentials")
	public void testForUserWithCorrectCredentials() throws Exception {
		// Create login request payload
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail("aung@gmail.com");
		loginRequest.setPassword("password");

		// Convert payload to json
		ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = objectWriter.writeValueAsString(loginRequest);

		// post request for login except with ok status code
		MvcResult mvcResult = mockMvc
				.perform(post("/auth/login").content(json)
						.contentType(new MediaType(MediaType.APPLICATION_JSON.getType(),
								MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"))))
				.andExpect(status().isOk()).andReturn();
		String token = JsonPath.read(mvcResult.getResponse().getContentAsString(), "accessToken");
		JWTToken = token;
		// ensure there is access token in response body.
		assertTrue(!token.isEmpty());
	}

	@Test
	@DisplayName("Test for OAuth Authentication Facebook")
	public void testForFacebookOAuth() throws Exception {
		// if user choose to login with facebook user should be redirected to facebook
		// login page.
		// the test is excepted for redirect status code.
		String facebookOAuthURL = "http://localhost:8080/oauth2/authorize/facebook"
				+ "?redirectUrl=http://localhost:3000/oauth2/redirect";
		mockMvc.perform(get(facebookOAuthURL)).andExpect(status().is3xxRedirection());
	}

	@Test
	@DisplayName("Test for OAuth Authentication Google")
	public void testForGoogleOAuth() throws Exception {
		// if user choose to login with google user should be redirected to google login
		// page.
		// the test is excepted for redirect status code.
		String goolgleOauthURL = "http://localhost:8080/oauth2/authorize/google"
				+ "?redirectUrl=http://localhost:3000/oauth2/redirect";
		mockMvc.perform(get(goolgleOauthURL)).andExpect(status().is3xxRedirection());
	}

	@Test
	@DisplayName("Test whether user can access user information with JWT token")
	public void testForRetrieveProfileWithJWT() throws Exception {
		//excepted for 200 status when request with JWT
		MvcResult mvcResult =  mockMvc.perform(get("/profile/me").header("Authorization", "Bearer " + JWTToken))
		.andExpect(status().isOk())
		.andReturn();
		
		//get email address from response
		String emailAddress = JsonPath.read(mvcResult.getResponse().getContentAsString(), "email");
	
		//excepted email to equal with original user info
		assertEquals("aung@gmail.com", emailAddress);
	}
}
