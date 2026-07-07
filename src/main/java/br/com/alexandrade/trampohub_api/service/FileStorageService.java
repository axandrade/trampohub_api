package br.com.alexandrade.trampohub_api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.alexandrade.trampohub_api.exception.BusinessException;

@Service
public class FileStorageService {

    @Value("${app.media.root}")
    private String mediaRoot;

    /** Salva o arquivo em media/{subdir}/ com nome unico e devolve a URL publica (/media/{subdir}/arquivo). */
    public String salvarFotoPerfil(MultipartFile arquivo) {
        String extensao = "";
        String nomeOriginal = StringUtils.cleanPath(arquivo.getOriginalFilename() == null ? "" : arquivo.getOriginalFilename());
        int ponto = nomeOriginal.lastIndexOf('.');
        if (ponto >= 0) {
            extensao = nomeOriginal.substring(ponto);
        }

        String nomeArquivo = UUID.randomUUID() + extensao;
        Path destino = Path.of(mediaRoot, "perfis", nomeArquivo);

        try {
            Files.createDirectories(destino.getParent());
            arquivo.transferTo(destino);
        } catch (IOException e) {
            throw new BusinessException("Não foi possível salvar a foto enviada.");
        }

        return "/media/perfis/" + nomeArquivo;
    }
}
