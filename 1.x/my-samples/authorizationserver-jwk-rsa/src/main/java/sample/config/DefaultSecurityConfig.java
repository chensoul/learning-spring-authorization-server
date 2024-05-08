package sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Web security config
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize ->
				authorize
					.requestMatchers("/assets/**", "/login").permitAll()
					.anyRequest().authenticated()
			)
			.formLogin(withDefaults());

		return http.build();
	}

	// @formatter:off
	@Bean
	public UserDetailsService users() {
		UserDetails user = User.withDefaultPasswordEncoder()
			.username("user1")
			.password("password")
			.roles("USER")
			.build();
		return new InMemoryUserDetailsManager(user);
	}
	// @formatter:on
}
