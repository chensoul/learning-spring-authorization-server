package com.chensoul.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

@SpringBootApplication
public class AuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationServerApplication.class, args);
	}

	/**
	 * Creating this bean initialized the following endpoints:
	 * /oauth2/authorize
	 * /oauth2/device_authorization
	 * /oauth2/token
	 * /oauth2/jwks
	 * /oauth2/revoke
	 * /oauth2/introspect
	 * /connect/register
	 * /userinfo
	 * /connect/logout
	 * <p>
	 * For java based client registration configuration, it is very important to initialize this bean
	 */
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

}
