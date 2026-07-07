package br.com.alexandrade.trampohub_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.alexandrade.trampohub_api.dto.VagaRequest;
import br.com.alexandrade.trampohub_api.dto.VagaResponse;
import br.com.alexandrade.trampohub_api.exception.FieldValidationException;
import br.com.alexandrade.trampohub_api.exception.ResourceNotFoundException;
import br.com.alexandrade.trampohub_api.enums.TipoUsuario;
import br.com.alexandrade.trampohub_api.model.Usuario;
import br.com.alexandrade.trampohub_api.model.Vaga;
import br.com.alexandrade.trampohub_api.repository.VagaRepository;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;

    public VagaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    public List<VagaResponse> listar(Usuario usuarioAtual) {
        return buscarEscopo(usuarioAtual).stream().map(VagaResponse::de).toList();
    }

    public VagaResponse obter(String id, Usuario usuarioAtual) {
        return VagaResponse.de(buscarComVisibilidade(id, usuarioAtual));
    }

    public VagaResponse criar(VagaRequest request, Usuario usuarioAtual) {
        validarCampos(request);

        Vaga vaga = new Vaga();
        aplicarCampos(vaga, request);
        vaga.setEmpregadorId(usuarioAtual.getId());
        vagaRepository.save(vaga);
        return VagaResponse.de(vaga);
    }

    public VagaResponse atualizarCompleto(String id, VagaRequest request, Usuario usuarioAtual) {
        validarCampos(request);
        Vaga vaga = buscarDePropriedade(id, usuarioAtual);
        aplicarCampos(vaga, request);
        vagaRepository.save(vaga);
        return VagaResponse.de(vaga);
    }

    public VagaResponse atualizarParcial(String id, VagaRequest request, Usuario usuarioAtual) {
        Vaga vaga = buscarDePropriedade(id, usuarioAtual);

        if (request.dataInicio() != null && request.dataFim() != null
                && request.dataInicio().isAfter(request.dataFim())) {
            throw new FieldValidationException("data_fim", "A data final deve ser depois da data inicial.");
        }

        if (request.titulo() != null) vaga.setTitulo(request.titulo());
        if (request.descricao() != null) vaga.setDescricao(request.descricao());
        if (request.empresa() != null) vaga.setEmpresa(request.empresa());
        if (request.localizacao() != null) vaga.setLocalizacao(request.localizacao());
        if (request.salario() != null) vaga.setSalario(request.salario());
        if (request.tipoContrato() != null) vaga.setTipoContrato(request.tipoContrato());
        if (request.modalidade() != null) vaga.setModalidade(request.modalidade());
        if (request.dataInicio() != null) vaga.setDataInicio(request.dataInicio());
        if (request.dataFim() != null) vaga.setDataFim(request.dataFim());

        vagaRepository.save(vaga);
        return VagaResponse.de(vaga);
    }

    public void remover(String id, Usuario usuarioAtual) {
        Vaga vaga = buscarDePropriedade(id, usuarioAtual);
        vagaRepository.delete(vaga);
    }

    private void validarCampos(VagaRequest request) {
        if (request.dataFim() == null) {
            throw new FieldValidationException("data_fim", "Este campo é obrigatório.");
        }
        if (request.dataInicio() != null && request.dataInicio().isAfter(request.dataFim())) {
            throw new FieldValidationException("data_fim", "A data final deve ser depois da data inicial.");
        }
    }

    private void aplicarCampos(Vaga vaga, VagaRequest request) {
        vaga.setTitulo(request.titulo());
        vaga.setDescricao(request.descricao());
        vaga.setEmpresa(request.empresa());
        vaga.setLocalizacao(request.localizacao());
        vaga.setSalario(request.salario());
        vaga.setTipoContrato(request.tipoContrato());
        vaga.setModalidade(request.modalidade());
        vaga.setDataInicio(request.dataInicio());
        vaga.setDataFim(request.dataFim());
    }

    /** Espelha get_queryset: empregador autenticado so enxerga as proprias vagas; demais veem todas. */
    private List<Vaga> buscarEscopo(Usuario usuarioAtual) {
        if (usuarioAtual != null && usuarioAtual.getTipo() == TipoUsuario.EMPREGADOR) {
            return vagaRepository.findByEmpregadorId(usuarioAtual.getId());
        }
        return vagaRepository.findAll();
    }

    private Vaga buscarComVisibilidade(String id, Usuario usuarioAtual) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada."));

        if (usuarioAtual != null && usuarioAtual.getTipo() == TipoUsuario.EMPREGADOR
                && !vaga.getEmpregadorId().equals(usuarioAtual.getId())) {
            throw new ResourceNotFoundException("Vaga não encontrada.");
        }
        return vaga;
    }

    /** Usada por create/update/delete: exige que a vaga pertenca ao empregador autenticado. */
    private Vaga buscarDePropriedade(String id, Usuario usuarioAtual) {
        Vaga vaga = vagaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada."));

        if (!vaga.getEmpregadorId().equals(usuarioAtual.getId())) {
            throw new ResourceNotFoundException("Vaga não encontrada.");
        }
        return vaga;
    }
}
