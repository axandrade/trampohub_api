package br.com.alexandrade.trampohub_api.exception;

import java.util.LinkedHashMap;
import java.util.Map;

/** Espelha o formato de erro por campo do DRF: {"campo": "mensagem"}. */
public class FieldValidationException extends RuntimeException {

    private final Map<String, String> errors = new LinkedHashMap<>();

    public FieldValidationException(String field, String message) {
        super(message);
        errors.put(field, message);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
