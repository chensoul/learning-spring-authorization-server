package sample.config;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class OAuth2TokenCustomizerConfig implements OAuth2TokenCustomizer<JwtEncodingContext> {

	@Override
	public void customize(JwtEncodingContext context) {
		OAuth2TokenType tokenType = context.getTokenType();
		JwtClaimsSet.Builder claims = context.getClaims();
		Authentication principal = context.getPrincipal();

		if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
			Set<String> authorities = principal.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());
			claims.claim("authorities", authorities);
		}
	}

}
