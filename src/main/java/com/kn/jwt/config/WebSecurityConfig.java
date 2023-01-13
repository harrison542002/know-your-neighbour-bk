package com.kn.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.kn.jwt.security.JwtAuthenticationEntryPoint;
import com.kn.jwt.security.JwtRequestFileter;
import com.kn.jwt.security.oauth.CustomOAuthUserService;
import com.kn.jwt.security.oauth.CustomRequestResolver;
import com.kn.jwt.security.oauth.OAuth2FailHandler;
import com.kn.jwt.security.oauth.OAuth2SuccessHandler;
import com.kn.jwt.service.CustomUserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserService customUserDetailsService;

	@Autowired
	private OAuth2SuccessHandler oAuth2AuthenticationSuccessHandler;
	@Autowired
	private OAuth2FailHandler oAuth2AuthenticationFailureHandler;
	@Autowired
	private JwtRequestFileter jwtRequestFileter;

	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		// TODO Auto-generated method stub
		return super.authenticationManagerBean();
	}

	@Bean
	public OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
		OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
				clientRegistrationRepository, "/oauth2/authorize");
		return new CustomRequestResolver(defaultAuthorizationRequestResolver);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf()
				.disable().formLogin().disable().httpBasic().disable().authorizeRequests()
				.antMatchers("/", "/error", "/login/**").permitAll().antMatchers("/auth/**", "/oauth2/**").permitAll()
				.antMatchers(HttpMethod.POST, "/login", "/signUp").permitAll().anyRequest().authenticated().and()
				.oauth2Login().successHandler(oAuth2AuthenticationSuccessHandler)
				.failureHandler(oAuth2AuthenticationFailureHandler).authorizationEndpoint()
				.authorizationRequestResolver(authorizationRequestResolver());
		http.exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint());
		http.addFilterBefore(jwtRequestFileter, UsernamePasswordAuthenticationFilter.class);
	}
}
