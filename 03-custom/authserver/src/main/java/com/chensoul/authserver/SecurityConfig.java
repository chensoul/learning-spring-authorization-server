package com.chensoul.authserver;

import com.chensoul.authserver.jose.Jwks;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

	// This first SecurityFilterChain Bean is only specific to authorization server specific configurations
	// More on this can be found in this stackoverflow question answers:
	// https://stackoverflow.com/questions/69126874/why-two-formlogin-configured-in-spring-authorization-server-sample-code
	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(withDefaults());

//		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
//			.oidc(Customizer.withDefaults())
//			.tokenEndpoint(tokenEndpointCustomizer -> {
//				tokenEndpointCustomizer.errorResponseHandler(new CustomAuthenticationFailureHandler());
//			}).clientAuthentication(clientAuthenticationCustomizer -> {
//				clientAuthenticationCustomizer.errorResponseHandler(new CustomAuthenticationFailureHandler());
//			});

		return http.exceptionHandling(e -> e.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
			.oauth2ResourceServer(auth -> auth.jwt(withDefaults())).build();

	}

	// This second SecurityFilterChain bean is responsible for any other security configurations
	@Bean
	@Order(2)
	public SecurityFilterChain clientAppSecurityFilterChain(HttpSecurity http) throws Exception {
		return http.formLogin(withDefaults())
			.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated()).build();
	}

	// In-memory user registration
	@Bean
	public UserDetailsService userDetailsService() {
		var user1 = User.withUsername("user").password("{noop}password").authorities("read").build();
		return new InMemoryUserDetailsManager(user1);
	}

	// In-memory authorization server client registration
	@Bean
	public RegisteredClientRepository registeredClientRepository() {

		RegisteredClient simpleClient = RegisteredClient
			.withId("client-1")
			.clientId("client")
			.clientSecret("{noop}secret")
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
			.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
			.build();

		RegisteredClient oidcClient = RegisteredClient
			.withId("oidc-client")
			.clientId("oidc-client")
			.clientSecret("{noop}secret2")
			.scope("read")
			.scope(OidcScopes.OPENID)
			.scope(OidcScopes.PROFILE)
			.scope("write")
			.redirectUri("http://127.0.0.1:8081/login/oauth2/code/oidc-client")
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
			.clientSettings(clientSettings()).build();

		return new InMemoryRegisteredClientRepository(oidcClient, simpleClient);
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

	@Bean
	ClientSettings clientSettings() {
		return ClientSettings.builder()
			.requireAuthorizationConsent(true)  // Display post-login authorization consent screen
			.requireProofKey(true)              // flag to enable Proof Key for Code Exchange (PKCE)
			.build();
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		RSAKey rsaKey = Jwks.generateRsa();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
	}

//	@Value("${spring.security.oauth2.jwk.rsaPublicKey}")
//	private Resource rsaPublicKey;
//
//	@Value("${spring.security.oauth2.jwk.rsaPrivateKey}")
//	private Resource rsaPrivateKey;
//
//	@Bean
//	public JWKSource<SecurityContext> jwkSource() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
//		JWKSet jwkSet = Jwks.buildJWKSet(rsaPublicKey, rsaPrivateKey);
//		return new ImmutableJWKSet<>(jwkSet);
//	}


//	@Value("${spring.security.oauth2.jwk.keystore-location}")
//	private Resource keystoreLocation;
//
//	@Value("${spring.security.oauth2.jwk.store-password}")
//	private String storePassword;
//
//	@Bean
//	public JWKSource<SecurityContext> jwkSource() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
//		JWKSet jwkSet = Jwks.buildJWKSet(keystoreLocation, storePassword);
//		return new ImmutableJWKSet<>(jwkSet);
//	}

	@Bean
	public BearerTokenResolver bearerTokenResolver() {
		DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
		bearerTokenResolver.setAllowUriQueryParameter(true);
		return bearerTokenResolver;
	}
}
