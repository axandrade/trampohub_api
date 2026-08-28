package br.com.alexandrade.trampohub_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import br.com.alexandrade.trampohub_api.enums.TipoUsuario;
import br.com.alexandrade.trampohub_api.exception.FieldValidationException;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.repository.TokenRepository;
import br.com.alexandrade.trampohub_api.repository.UsuarioRepository;

/**
 * Garante que todo campo enviado no cadastro chega intacto até o Usuario salvo no banco -
 * a lacuna que deixou "nome_empresa" sendo perdido silenciosamente (@RequestParam com nome
 * divergente) é justamente o tipo de regressão que estes testes travam.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private FileStorageService fileStorageService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioRepository, tokenRepository, passwordEncoder, fileStorageService);
    }

    private MultipartFile foto() {
        return new MockMultipartFile("foto", "foto.gif", "image/gif", new byte[] {1, 2, 3});
    }

    @Test
    void cadastroDeEmpregadorPreencheTodosOsCamposDoUsuario() {
        when(passwordEncoder.encode("123456")).thenReturn("senha-codificada");

        authService.cadastrar("chefe", "123456", "empregador", "ACME Ltda", null, "chefe@example.com",
                "12.345.678/0001-90");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();

        assertThat(salvo.getUsername()).isEqualTo("chefe");
        assertThat(salvo.getPassword()).isEqualTo("senha-codificada");
        assertThat(salvo.getEmail()).isEqualTo("chefe@example.com");
        assertThat(salvo.getTipo()).isEqualTo(TipoUsuario.EMPREGADOR);
        assertThat(salvo.getNomeEmpresa()).isEqualTo("ACME Ltda");
        assertThat(salvo.getCnpj()).isEqualTo("12345678000190");
    }

    @Test
    void cadastroDeCandidatoPreencheTodosOsCamposDoUsuarioIncluindoFoto() {
        when(passwordEncoder.encode("123456")).thenReturn("senha-codificada");
        when(fileStorageService.salvarFotoPerfil(any())).thenReturn("/media/perfis/foto.gif");

        authService.cadastrar("joao", "123456", "candidato", null, foto(), "joao@example.com", null);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario salvo = captor.getValue();

        assertThat(salvo.getUsername()).isEqualTo("joao");
        assertThat(salvo.getPassword()).isEqualTo("senha-codificada");
        assertThat(salvo.getEmail()).isEqualTo("joao@example.com");
        assertThat(salvo.getTipo()).isEqualTo(TipoUsuario.CANDIDATO);
        assertThat(salvo.getFoto()).isEqualTo("/media/perfis/foto.gif");
    }

    @Test
    void semUsernameNaoSalva() {
        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar(" ", "123456", "empregador", "ACME", null, null, "12345678000190"));

        assertThat(ex.getErrors()).containsKey("username");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void usernameDuplicadoNaoSalva() {
        when(usuarioRepository.existsByUsername("joao")).thenReturn(true);

        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar("joao", "123456", "empregador", "ACME", null, null, "12345678000190"));

        assertThat(ex.getErrors()).containsKey("username");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void senhaCurtaNaoSalva() {
        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar("joao", "123", "empregador", "ACME", null, null, "12345678000190"));

        assertThat(ex.getErrors()).containsKey("password");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void tipoInvalidoNaoSalva() {
        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar("joao", "123456", "gerente", "ACME", null, null, "12345678000190"));

        assertThat(ex.getErrors()).containsKey("tipo");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void candidatoSemFotoNaoSalva() {
        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar("joao", "123456", "candidato", null, null, "joao@example.com", null));

        assertThat(ex.getErrors()).containsKey("foto");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void candidatoSemEmailNaoSalva() {
        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar("joao", "123456", "candidato", null, foto(), null, null));

        assertThat(ex.getErrors()).containsKey("email");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void empregadorNaoPrecisaDeFotoNemEmail() {
        when(passwordEncoder.encode(anyString())).thenReturn("senha-codificada");

        authService.cadastrar("chefe", "123456", "empregador", "ACME Ltda", null, null, "12345678000190");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getFoto()).isNull();
        assertThat(captor.getValue().getEmail()).isNull();
        assertThat(captor.getValue().getNomeEmpresa()).isEqualTo("ACME Ltda");
    }

    @Test
    void empregadorSemCnpjNaoSalva() {
        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar("chefe", "123456", "empregador", "ACME", null, null, null));

        assertThat(ex.getErrors()).containsKey("cnpj");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void empregadorComCnpjInvalidoNaoSalva() {
        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar("chefe", "123456", "empregador", "ACME", null, null, "123"));

        assertThat(ex.getErrors()).containsKey("cnpj");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void empregadorComCnpjDuplicadoNaoSalva() {
        when(usuarioRepository.existsByCnpj("12345678000190")).thenReturn(true);

        FieldValidationException ex = assertThrows(FieldValidationException.class,
                () -> authService.cadastrar("chefe", "123456", "empregador", "ACME", null, null,
                        "12.345.678/0001-90"));

        assertThat(ex.getErrors()).containsKey("cnpj");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void editarPerfilAtualizaNomeDaEmpresaSemApagarOutrosCampos() {
        Usuario existente = new Usuario();
        existente.setId("1");
        existente.setUsername("chefe");
        existente.setPassword("senha-antiga-codificada");
        existente.setEmail("chefe@example.com");
        existente.setTipo(TipoUsuario.EMPREGADOR);
        existente.setNomeEmpresa("Nome Antigo");

        authService.editarPerfil(existente, null, null, "Nome Novo", null, null, null);

        assertThat(existente.getNomeEmpresa()).isEqualTo("Nome Novo");
        assertThat(existente.getUsername()).isEqualTo("chefe");
        assertThat(existente.getEmail()).isEqualTo("chefe@example.com");
        verify(usuarioRepository).save(existente);
    }
}