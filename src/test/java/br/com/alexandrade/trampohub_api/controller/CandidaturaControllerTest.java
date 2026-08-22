package br.com.alexandrade.trampohub_api.controller;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.alexandrade.trampohub_api.enums.TipoUsuario;
import br.com.alexandrade.trampohub_api.model.Token;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.repository.TokenRepository;
import br.com.alexandrade.trampohub_api.repository.UsuarioRepository;
import br.com.alexandrade.trampohub_api.service.CandidaturaService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidaturaController.class)
public class CandidaturaControllerTest {

    private static final String TOKEN_KEY = "39327ba57400594d5496336c46ff853ff638221c";
    private static final String USUARIO_ID = "6a898e11ccfabd353e9df8a1";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CandidaturaService candidaturaService;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private TokenRepository tokenRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    public void testCriarCandidatura_DeveEnviarParaRabbitMQ() throws Exception {
        // Arrange: token válido apontando para um usuário CANDIDATO
        Token token = new Token(null, TOKEN_KEY, USUARIO_ID, Instant.now());
        when(tokenRepository.findByKey(TOKEN_KEY)).thenReturn(Optional.of(token));

        Usuario usuario = new Usuario();
        usuario.setId(USUARIO_ID);
        usuario.setUsername("candidato-teste");
        usuario.setEmail("candidato@teste.com");
        usuario.setTipo(TipoUsuario.CANDIDATO);
        when(usuarioRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));

        String requestJson = """
            {
                "vaga": "6a898e11ccfabd353e9df8a3",
                "mensagem": "Teste candidatura"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/candidaturas/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .header("Authorization", "Token " + TOKEN_KEY))
                .andExpect(status().isAccepted());

        // Verifica que rabbitTemplate.convertAndSend foi chamado
        verify(rabbitTemplate).convertAndSend(
                eq("candidatura-exchange"),
                eq("candidatura.criada"),
                any(Object.class)
        );
    }
}
