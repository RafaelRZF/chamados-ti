package br.senai.chamados;

public enum Prioridade {
    BAIXA("Baixa"), MEDIA("Média"), ALTA("Alta");

    private final String rotulo;

    Prioridade(String rotulo) { this.rotulo = rotulo; }

    public String getRotulo() { return rotulo; }

    /** Converte o texto digitado; lança exceção se não for baixa, média ou alta. */
    public static Prioridade interpretar(String texto) {
        switch (Texto.normalizar(texto)) {
            case "baixa": return BAIXA;
            case "media": return MEDIA;
            case "alta":  return ALTA;
            default:
                throw new RegraNegocioException(
                        "Prioridade inválida. Use: baixa, média ou alta.");
        }
    }
}
