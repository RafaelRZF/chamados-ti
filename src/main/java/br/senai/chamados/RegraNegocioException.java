package br.senai.chamados;

/** Erro de validação ou de regra de negócio, com mensagem pensada para o atendente. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
