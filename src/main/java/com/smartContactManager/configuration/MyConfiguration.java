package com.smartContactManager.configuration;

// import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
//import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MyConfiguration {

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {

		/*
		 * http.authorizeHttpRequests((authorize) ->
		 * authorize.requestMatchers("/**").hasRole("USER")
		 * .requestMatchers("/user/**").hasRole("ADMIN")
		 * .anyRequest().authenticated())
		 * .httpBasic(withDefaults())
		 * .formLogin(withDefaults())
		 * .csrf(AbstractHttpConfigurer::disable);
		 */
		http
				.authorizeHttpRequests(
						(authorize) -> authorize
								// .requestMatchers("/user/index/**").hasRole("USER")
								.requestMatchers("/resources/static/**").permitAll()
								.requestMatchers("/user/index").authenticated()
								// .requestMatchers("/").permitAll()
								.anyRequest().permitAll())
				.formLogin(form -> form.loginPage("/signin")
						.loginProcessingUrl("/signin")
						// .defaultSuccessUrl("/user/index/**")
						// .failureUrl("/login-fail")
						.permitAll())
				.logout((logout) -> logout.deleteCookies("remove")
						.invalidateHttpSession(false)
						.logoutUrl("/logout")
						.logoutSuccessUrl("/signin"))
				.csrf(csrfs -> csrfs.csrfTokenRepository(csrfTokenRepository()).ignoringRequestMatchers(publicUrls));

		return http.build();
	}

	@Bean
	public CsrfTokenRepository csrfTokenRepository() {
		return new HttpSessionCsrfTokenRepository();
	}

	private static final String[] publicUrls = {
			"/resources/static",
			"/css/**",
			"/js/**",
			"/images/**",

	};

	// Configure CSRF ignoring public URLs

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;
	}

	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}
}
