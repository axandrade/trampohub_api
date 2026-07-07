package br.com.alexandrade.trampohub_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.alexandrade.trampohub_api.dto.CandidaturaRequest;
import br.com.alexandrade.trampohub_api.dto.CandidaturaResponse;
import br.com.alexandrade.trampohub_api.dto.VagaResponse;
import br.com.alexandrade.trampohub_api.exception.BusinessException;
import br.com.alexandrade.trampohub_api.exception.FieldValidationException;
import br.com.alexandrade.trampohub_api.exception.ResourceNotFoundException;
import br.com.alexandrade.trampohub_api.model.Candidatura;
import br.com.alexandrade.trampohub_api.model.TipoUsuario;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.model.Vaga;
import br.com.alexandrade.trampohub_api.repository.CandidaturaRepository;
import br.com.alexandrade.trampohub_api.repository.VagaRepository;

@Service
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final VagaRepository vagaRepository;
    private final NotificacaoService notificacaoService;

    public CandidaturaService(CandidaturaRepository candidaturaRepository, VagaRepository vagaRepository,
                               NotificacaoService notificacaoService) {
        this.candidaturaRepository = candidaturaRepository;
        this.vagaRepository = vagaRepository;
        this.notificacaoService = notificacaoService;
    }

    public List<CandidaturaResponse> listar(Usuario usuarioAtual) {
        return buscarEscopo(usuarioAtual).stream().map(this::montarResponse).toList();
    }

    public CandidaturaResponse obter(String id, Usuario usuarioAtual) {
        return montarResponse(buscarComVisibilidade(id, usuarioAtual));
    }

    public CandidaturaResponse criar(CandidaturaRequest request, Usuario usuarioAtual) {
        if (request.vaga() == null || request.vaga().isBlank()) {
            throw new FieldValidationException("vaga", "Este campo é obrigatório.");
        }

        Vaga vaga = vagaRepository.findById(request.vaga())
                .orElseThrow(() -> new FieldValidationException("vaga", "Vaga inválida — objeto não existe."));

        if (vaga.isExpirada()) {
            throw new BusinessException("Essa vaga já está com as candidaturas encerradas.");
        }

        boolean jaExiste = candidaturaRepository.findByVagaIdAndCandidatoId(vaga.getId(), usuarioAtual.getId())
                .isPresent();
        if (jaExiste) {
            throw new BusinessException("Você já se candidatou a essa vaga.");
        }

        Candidatura candidatura = new Candidatura();
        candidatura.setVagaId(vaga.getId());
        candidatura.setCandidatoId(usuarioAtual.getId());
        candidatura.setMensagem(request.mensagem());
        candidaturaRepository.save(candidatura);

        notificacaoService.notificarNovaCandidatura(vaga.getTitulo(), usuarioAtual.getId());

        return montarResponse(candidatura);
    }

    public CandidaturaResponse atualizar(String id, CandidaturaRequest request, Usuario usuarioAtual) {
        Candidatura candidatura = buscarComVisibilidade(id, usuarioAtual);
        if (request.status() != null) {
            candidatura.setStatus(request.status());
        }
        if (request.mensagem() != null) {
            candidatura.setMensagem(request.mensagem());
        }
        candidaturaRepository.save(candidatura);
        return montarResponse(candidatura);
    }

    public void remover(String id, Usuario usuarioAtual) {
        Candidatura candidatura = buscarComVisibilidade(id, usuarioAtual);
        candidaturaRepository.delete(candidatura);
    }

    /** Espelha get_queryset: candidato ve as proprias, empregador ve as das proprias vagas. */
    private List<Candidatura> buscarEscopo(Usuario usuarioAtual) {
        if (usuarioAtual.getTipo() == TipoUsuario.CANDIDATO) {
            return candidaturaRepository.findByCandidatoId(usuarioAtual.getId());
        }
        if (usuarioAtual.getTipo() == TipoUsuario.EMPREGADOR) {
            List<String> vagaIds = vagaRepository.findByEmpregadorId(usuarioAtual.getId())
                    .stream().map(Vaga::getId).toList();
            return candidaturaRepository.findByVagaIdIn(vagaIds);
        }
        return List.of();
    }

    private Candidatura buscarComVisibilidade(String id, Usuario usuarioAtual) {
        return buscarEscopo(usuarioAtual).stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Candidatura não encontrada."));
    }

    private CandidaturaResponse montarResponse(Candidatura candidatura) {
        VagaResponse vagaResponse = vagaRepository.findById(candidatura.getVagaId())
                .map(VagaResponse::de)
                .orElse(null);
        return CandidaturaResponse.de(candidatura, vagaResponse);
    }
}
