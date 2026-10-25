package br.senai.chamados;

public enum Status {
    ABERTO("Aberto"), EM_ATENDIMENTO("Em atendimento"), CONCLUIDO("Concluído");

    private final String rotulo;

    Status(String rotulo) { this.rotulo = rotulo; }

    public String getRotulo() { return rotulo; }

    /** Único status que pode vir depois deste (null se o chamado já foi concluído). */
    public Status proximo() {
        switch (this) {
            case ABERTO:         return EM_ATENDIMENTO;
            case EM_ATENDIMENTO: return CONCLUIDO;
            default:             return null;
        }
    }

    public static Status interpretar(String texto) {
        switch (Texto.normalizar(texto)) {
            case "aberto":         return ABERTO;
            case "em atendimento": return EM_ATENDIMENTO;
            case "concluido":      return CONCLUIDO;
            default:
                throw new RegraNegocioException(
                        "Status inválido. Use: aberto, em atendimento ou concluído.");
        }
    }
}
