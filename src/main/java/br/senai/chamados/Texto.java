package br.senai.chamados;

import java.text.Normalizer;

/** Utilitário para comparar textos digitados sem depender de acento, caixa ou espaços extras. */
final class Texto {
    private Texto() {}

    static String normalizar(String texto) {
        if (texto == null) return "";
        String semAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase().replaceAll("[\\s_-]+", " ");
    }
}
