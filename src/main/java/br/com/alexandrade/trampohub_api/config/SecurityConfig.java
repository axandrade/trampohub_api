package br.com.alexandrade.trampohub_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import br.com.alexandrade.trampohub_api.security.RestAccessDeniedHandler;
import br.com.alexandrade.trampohub_api.security.RestAuthEntryPoint;
import br.com.alexandrade.trampohub_api.security.TokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final RestAuthEntryPoint authEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(TokenAuthenticationFilter tokenAuthenticationFilter,
                           RestAuthEntryPoint authEntryPoint,
                           RestAccessDeniedHandler accessDeniedHandler,
                           CorsConfigurationSource corsConfigurationSource) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/token/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/cadastro/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/media/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vagas/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/vagas/").hasRole("EMPREGADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/vagas/**").hasRole("EMPREGADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/vagas/**").hasRole("EMPREGADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/vagas/**").hasRole("EMPREGADOR")
                        .requestMatchers(HttpMethod.POST, "/api/candidaturas/").hasRole("CANDIDATO")
                        .requestMatchers("/api/candidaturas/**").authenticated()
                        .requestMatchers("/api/perfil/me/").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
