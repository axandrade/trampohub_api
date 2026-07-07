package br.com.alexandrade.trampohub_api.security;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.alexandrade.trampohub_api.model.Token;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.repository.TokenRepository;
import br.com.alexandrade.trampohub_api.repository.UsuarioRepository;
import tools.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Espelha vagas/authentication.py: token opaco "Authorization: Token <key>",
 * invalidado e removido apos app.token-expiration-minutes.
 */
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIX = "Token ";

    private final TokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.token-expiration-minutes:15}")
    private long tokenExpirationMinutes;

    public TokenAuthenticationFilter(TokenRepository tokenRepository, UsuarioRepository usuarioRepository,
                                      ObjectMapper objectMapper) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(PREFIX)) {
            String key = header.substring(PREFIX.length()).trim();
            Optional<Token> tokenOpt = tokenRepository.findByKey(key);

            if (tokenOpt.isEmpty()) {
                unauthorized(response, "Token inválido.");
                return;
            }

            Token token = tokenOpt.get();
            Instant limite = Instant.now().minus(Duration.ofMinutes(tokenExpirationMinutes));
            if (token.getCriadoEm().isBefore(limite)) {
                tokenRepository.delete(token);
                unauthorized(response, "Token expirado. Faça login novamente.");
                return;
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(token.getUsuarioId());
            if (usuarioOpt.isEmpty()) {
                unauthorized(response, "Token inválido.");
                return;
            }

            Usuario usuario = usuarioOpt.get();
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name()));
            var auth = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response, String detail) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(java.util.Map.of("detail", detail)));
    }
}
