package br.senai.chamados;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Regras de negócio: valida os dados antes de qualquer acesso ao banco. */
public class ChamadoService {
    static final int MIN_SOLICITANTE = 3;
    static final int MIN_DESCRICAO = 10;

    private final ChamadoDAO dao;

    public ChamadoService(ChamadoDAO dao) { this.dao = dao; }

    public int cadastrar(String solicitante, String descricao, String prioridadeTexto) throws SQLException {
        String sol = solicitante == null ? "" : solicitante.trim();
        String desc = descricao == null ? "" : descricao.trim();

        if (sol.length() < MIN_SOLICITANTE)
            throw new RegraNegocioException(
                    "Solicitante inválido: informe pelo menos " + MIN_SOLICITANTE + " caracteres (sem contar espaços nas pontas).");
        if (desc.length() < MIN_DESCRICAO)
            throw new RegraNegocioException(
                    "Descrição inválida: informe pelo menos " + MIN_DESCRICAO + " caracteres (sem contar espaços nas pontas).");
        Prioridade prioridade = Prioridade.interpretar(prioridadeTexto);

        return dao.inserir(sol, desc, prioridade);
    }

    public Chamado consultar(int id) throws SQLException {
        return dao.buscarPorId(id).orElseThrow(() ->
                new RegraNegocioException("Chamado #" + id + " não encontrado."));
    }

    public List<Chamado> listar() throws SQLException {
        return dao.listar();
    }

    /** Aplica a regra aberto -> em atendimento -> concluído (sem pular etapas nem voltar). */
    public Chamado atualizarStatus(int id, String novoStatusTexto) throws SQLException {
        Chamado chamado = consultar(id);
        Status novo = Status.interpretar(novoStatusTexto);
        Status atual = chamado.getStatus();

        if (novo != atual.proximo()) {
            String permitido = atual.proximo() == null
                    ? "O chamado já está concluído e não pode mais ser alterado."
                    : "A próxima etapa permitida é: " + atual.proximo().getRotulo() + ".";
            throw new RegraNegocioException("Alteração recusada: de \"" + atual.getRotulo()
                    + "\" para \"" + novo.getRotulo() + "\" não é permitido. " + permitido);
        }
        if (!dao.atualizarStatus(id, atual, novo))
            throw new RegraNegocioException("O chamado #" + id + " foi alterado por outra pessoa. Consulte novamente.");
        return consultar(id);
    }

    /** Filtra por prioridade e/ou status; texto vazio significa "sem filtro". */
    public List<Chamado> filtrar(String prioridadeTexto, String statusTexto) throws SQLException {
        Prioridade p = vazio(prioridadeTexto) ? null : Prioridade.interpretar(prioridadeTexto);
        Status s = vazio(statusTexto) ? null : Status.interpretar(statusTexto);
        return dao.filtrar(p, s);
    }

    public Map<Status, Integer> resumoPorStatus() throws SQLException {
        return dao.contarPorStatus();
    }

    private boolean vazio(String t) { return t == null || t.trim().isEmpty(); }
}
