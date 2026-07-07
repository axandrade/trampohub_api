package br.com.alexandrade.trampohub_api.exception;

/** Erro de regra de negócio sem campo associado, retornado como 400 {"detail": mensagem}. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
