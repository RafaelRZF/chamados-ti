package br.senai.chamados;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre conexões com o banco. Os valores padrão servem para um MySQL local;
 * para mudar, defina as variáveis de ambiente DB_URL, DB_USER e DB_PASSWORD.
 */
public final class Conexao {
    private static final String URL = valor("DB_URL",
            "jdbc:mysql://localhost:3306/techserv_chamados?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String USUARIO = valor("DB_USER", "root");
    private static final String SENHA = valor("DB_PASSWORD", "");

    private Conexao() {}

    public static Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String valor(String variavel, String padrao) {
        String v = System.getenv(variavel);
        return (v == null || v.isEmpty()) ? padrao : v;
    }
}
