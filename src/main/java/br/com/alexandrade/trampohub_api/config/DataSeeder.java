package br.com.alexandrade.trampohub_api.config;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.alexandrade.trampohub_api.enums.Modalidade;
import br.com.alexandrade.trampohub_api.enums.TipoContrato;
import br.com.alexandrade.trampohub_api.enums.TipoUsuario;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.model.Vaga;
import br.com.alexandrade.trampohub_api.repository.UsuarioRepository;
import br.com.alexandrade.trampohub_api.repository.VagaRepository;

/**
 * Popula empresas e vagas de demonstracao no primeiro startup. Idempotente: cada
 * empresa e identificada pelo username, cada vaga pelo par (titulo, empregadorId),
 * entao rodar de novo em cima de um banco ja semeado nao duplica nada.
 */
@Component
@ConditionalOnProperty(prefix = "app", name = "seed-demo-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final String SENHA_PADRAO = "senha123";

    private final UsuarioRepository usuarioRepository;
    private final VagaRepository vagaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository, VagaRepository vagaRepository,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.vagaRepository = vagaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Usuario techSolutions = seedEmpregador("techsolutions", "contato@techsolutionsbrasil.com.br",
                "Tech Solutions Brasil Ltda", "12345678000190");
        Usuario alphaLog = seedEmpregador("alphalog", "rh@alphalogistica.com.br",
                "Alpha Logistica e Transportes S.A.", "98765432000110");

        seedVaga(techSolutions, "Desenvolvedor(a) Backend Java",
                "Atuar no desenvolvimento e manutencao de APIs REST com Spring Boot e MongoDB.",
                "Sao Paulo, SP", new BigDecimal("8500.00"), TipoContrato.CLT, Modalidade.HIBRIDO);
        seedVaga(techSolutions, "Desenvolvedor(a) Frontend Angular",
                "Construcao de interfaces com Angular e integracao com APIs REST.",
                "Remoto", new BigDecimal("7500.00"), TipoContrato.PJ, Modalidade.REMOTO);
        seedVaga(techSolutions, "Estagio em Suporte de TI",
                "Suporte tecnico a usuarios internos e manutencao de equipamentos.",
                "Sao Paulo, SP", new BigDecimal("1800.00"), TipoContrato.ESTAGIO, Modalidade.PRESENCIAL);

        seedVaga(alphaLog, "Analista de Logistica Pleno",
                "Planejamento e controle de rotas, fretes e armazenagem.",
                "Campinas, SP", new BigDecimal("5200.00"), TipoContrato.CLT, Modalidade.PRESENCIAL);
        seedVaga(alphaLog, "Motorista Carreteiro",
                "Transporte de cargas entre filiais em rotas interestaduais.",
                "Curitiba, PR", new BigDecimal("4500.00"), TipoContrato.CLT, Modalidade.PRESENCIAL);
        seedVaga(alphaLog, "Analista Fiscal Freelancer",
                "Apuracao de impostos e apoio em fechamento fiscal mensal.",
                "Remoto", new BigDecimal("3800.00"), TipoContrato.FREELANCE, Modalidade.REMOTO);

        seedCandidato("alexsandro.andrade", "teste@teste.com.br", "teste21");
    }

    private Usuario seedEmpregador(String username, String email, String nomeEmpresa, String cnpj) {
        return usuarioRepository.findByUsername(username).orElseGet(() -> {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(SENHA_PADRAO));
            usuario.setEmail(email);
            usuario.setTipo(TipoUsuario.EMPREGADOR);
            usuario.setNomeEmpresa(nomeEmpresa);
            usuario.setCnpj(cnpj);
            return usuarioRepository.save(usuario);
        });
    }

    private void seedCandidato(String username, String email, String password) {
        if (usuarioRepository.existsByUsername(username)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setEmail(email);
        usuario.setTipo(TipoUsuario.CANDIDATO);
        usuarioRepository.save(usuario);
    }

    private void seedVaga(Usuario empregador, String titulo, String descricao, String localizacao,
                           BigDecimal salario, TipoContrato tipoContrato, Modalidade modalidade) {
        boolean jaExiste = vagaRepository.findByEmpregadorId(empregador.getId()).stream()
                .anyMatch(vaga -> vaga.getTitulo().equals(titulo));
        if (jaExiste) {
            return;
        }

        Vaga vaga = new Vaga();
        vaga.setTitulo(titulo);
        vaga.setDescricao(descricao);
        vaga.setEmpresa(empregador.getNomeEmpresa());
        vaga.setLocalizacao(localizacao);
        vaga.setSalario(salario);
        vaga.setTipoContrato(tipoContrato);
        vaga.setModalidade(modalidade);
        vaga.setEmpregadorId(empregador.getId());
        vaga.setDataInicio(Instant.now());
        vagaRepository.save(vaga);
    }
}
