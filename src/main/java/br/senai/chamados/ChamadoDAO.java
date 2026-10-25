package br.senai.chamados;

import java.sql.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Acesso ao banco. Todo valor vindo do usuário entra na consulta por parâmetro (?),
 * nunca por concatenação de texto - isso evita SQL Injection.
 */
public class ChamadoDAO {

    private static final String COLUNAS =
            "id, solicitante, descricao, prioridade, status, data_abertura";

    /** Grava um chamado novo (status ABERTO; data e id gerados pelo banco) e devolve o id. */
    public int inserir(String solicitante, String descricao, Prioridade prioridade) throws SQLException {
        String sql = "INSERT INTO chamados (solicitante, descricao, prioridade, status) VALUES (?, ?, ?, 'ABERTO')";
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, solicitante);
            ps.setString(2, descricao);
            ps.setString(3, prioridade.name());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                chaves.next();
                return chaves.getInt(1);
            }
        }
    }

    public Optional<Chamado> buscarPorId(int id) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM chamados WHERE id = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Chamado> listar() throws SQLException {
        return filtrar(null, null);
    }

    /** Filtra por prioridade e/ou status; um parâmetro null significa "sem filtro". */
    public List<Chamado> filtrar(Prioridade prioridade, Status status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT " + COLUNAS + " FROM chamados WHERE 1 = 1");
        if (prioridade != null) sql.append(" AND prioridade = ?");   // só trechos fixos são concatenados
        if (status != null)     sql.append(" AND status = ?");
        sql.append(" ORDER BY id");

        List<Chamado> resultado = new ArrayList<>();
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int posicao = 1;
            if (prioridade != null) ps.setString(posicao++, prioridade.name());
            if (status != null)     ps.setString(posicao, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    /** Troca o status só se o chamado ainda estiver no status esperado. Devolve true se atualizou. */
    public boolean atualizarStatus(int id, Status statusAtual, Status novoStatus) throws SQLException {
        String sql = "UPDATE chamados SET status = ? WHERE id = ? AND status = ?";
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, novoStatus.name());
            ps.setInt(2, id);
            ps.setString(3, statusAtual.name());
            return ps.executeUpdate() == 1;
        }
    }

    public Map<Status, Integer> contarPorStatus() throws SQLException {
        Map<Status, Integer> contagem = new EnumMap<>(Status.class);
        for (Status s : Status.values()) contagem.put(s, 0);
        String sql = "SELECT status, COUNT(*) AS total FROM chamados GROUP BY status";
        try (Connection con = Conexao.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) contagem.put(Status.valueOf(rs.getString("status")), rs.getInt("total"));
        }
        return contagem;
    }

    private Chamado mapear(ResultSet rs) throws SQLException {
        return new Chamado(
                rs.getInt("id"),
                rs.getString("solicitante"),
                rs.getString("descricao"),
                Prioridade.valueOf(rs.getString("prioridade")),
                Status.valueOf(rs.getString("status")),
                rs.getTimestamp("data_abertura").toLocalDateTime());
    }
}
