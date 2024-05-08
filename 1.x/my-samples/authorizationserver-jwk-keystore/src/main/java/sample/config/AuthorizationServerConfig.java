package sample.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import sample.jose.Jwks;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
	@Value("${spring.security.oauth2.jwk.keystore-location}")
	private Resource keystoreLocation;

	@Value("${spring.security.oauth2.jwk.store-password}")
	private String storePassword;

	/**
	 * @see <a href="https://docs.spring.io/spring-authorization-server/docs/current/reference/html/protocol-endpoints.html">协议端点的</a> Spring Security 过滤器链。
	 */
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		// Enable OpenID Connect 1.0
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults());

		// 未从授权端点进行身份验证时重定向到登录页面
		http.exceptionHandling((exceptionHandling) -> exceptionHandling
			.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

		return http.build();
	}

	/**
	 * @see RegisteredClientRepository 用于管理客户端的实例。
	 */
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient messagingClient = RegisteredClient.withId(UUID.randomUUID().toString())
			.clientId("messaging-client")
			.clientSecret("{noop}secret")
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
			.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
			.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
			.redirectUri("http://127.0.0.1:8080/authorized")
			.postLogoutRedirectUri("http://127.0.0.1:8080/logged-out")
			.scope(OidcScopes.OPENID)
			.scope(OidcScopes.PROFILE)
			.scope("message.read")
			.scope("message.write")
			.scope("user.read")
			.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
			.build();

		return new InMemoryRegisteredClientRepository(messagingClient);
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
		JWKSet jwkSet = Jwks.buildJWKSet(keystoreLocation, storePassword);
		return new ImmutableJWKSet<>(jwkSet);
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	/**
	 * {@link AuthorizationServerSettings} 配置 Spring Authorization Server 的实例。
	 *
	 * @see <a href=
	 * "https://github.com/spring-projects/spring-authorization-server/commit/c60ae4532f1d745bff6eb793113731aba0493b70">Rename ProviderSettings</a>
	 */
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	@Bean
	public BearerTokenResolver bearerTokenResolver() {
		DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
		bearerTokenResolver.setAllowUriQueryParameter(true);
		return bearerTokenResolver;
	}
}
