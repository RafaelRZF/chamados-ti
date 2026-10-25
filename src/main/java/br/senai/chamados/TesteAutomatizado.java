package br.senai.chamados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Executa os testes T01 a T10 contra o banco configurado e imprime APROVADO/REPROVADO.
 * Não apaga dados existentes: remove apenas os chamados que ele mesmo criou.
 * (T09 aqui só simula o reinício; faça também o teste manual fechando e abrindo o programa.)
 */
public class TesteAutomatizado {
    private static final ChamadoService service = new ChamadoService(new ChamadoDAO());
    private static final List<Integer> criados = new ArrayList<>();
    private static int reprovados = 0;

    public static void main(String[] args) throws Exception {
        try {
            int id = 0;

            // T01
            id = service.cadastrar("Ana Souza", "Computador não inicia ao pressionar o botão.", "Alta");
            criados.add(id);
            Chamado c = service.consultar(id);
            resultado("T01 Cadastro válido", c.getStatus() == Status.ABERTO && c.getDataAbertura() != null && id > 0,
                    "id=" + id + ", status=" + c.getStatus().getRotulo() + ", data=" + c.getDataAbertura());

            // T02
            resultado("T02 Solicitante só com espaços", recusa(() -> service.cadastrar("     ", "Descrição suficientemente longa", "alta")), "");
            // T03
            resultado("T03 Descrição com menos de 10 caracteres", recusa(() -> service.cadastrar("Bruno Lima", "curta", "alta")), "");
            // T04
            resultado("T04 Prioridade não permitida", recusa(() -> service.cadastrar("Bruno Lima", "Impressora do setor não imprime.", "urgente")), "");
            // T05
            String msg5 = mensagemDeRecusa(() -> service.consultar(999999));
            resultado("T05 Identificador inexistente", msg5 != null && msg5.contains("não encontrado"), msg5);

            // T06
            Chamado c6 = service.atualizarStatus(id, "em atendimento");
            resultado("T06 Aberto -> Em atendimento", c6.getStatus() == Status.EM_ATENDIMENTO, c6.getStatus().getRotulo());

            // T07 (usa outro chamado, ainda aberto)
            int id2 = service.cadastrar("Bruno Lima", "Impressora do setor não imprime documentos.", "Média");
            criados.add(id2);
            boolean recusou = recusa(() -> service.atualizarStatus(id2, "concluído"));
            resultado("T07 Aberto -> Concluído direto", recusou && service.consultar(id2).getStatus() == Status.ABERTO,
                    "status continua " + service.consultar(id2).getStatus().getRotulo());

            // T08
            Chamado c8 = service.atualizarStatus(id, "concluído");
            resultado("T08 Em atendimento -> Concluído", c8.getStatus() == Status.CONCLUIDO, c8.getStatus().getRotulo());

            // T09 (simulação: nova instância de DAO/serviço lendo do banco)
            Chamado c9 = new ChamadoService(new ChamadoDAO()).consultar(id);
            resultado("T09 Persistência (simulada)", c9.getStatus() == Status.CONCLUIDO && c9.getSolicitante().equals("Ana Souza"),
                    "registro #" + id + " lido novamente do banco");

            // T10
            int id3 = service.cadastrar("Carla Santos", "Solicitação de instalação de editor de texto.", "Baixa");
            criados.add(id3);
            List<Chamado> todos = service.listar();
            Map<Status, Integer> resumo = service.resumoPorStatus();
            boolean contagemOk = true;
            for (Status s : Status.values()) {
                int esperado = (int) todos.stream().filter(x -> x.getStatus() == s).count();
                contagemOk &= resumo.get(s) == esperado;
            }
            long altas = todos.stream().filter(x -> x.getPrioridade() == Prioridade.ALTA).count();
            boolean filtroOk = service.filtrar("alta", "").size() == altas
                    && service.filtrar("", "aberto").stream().allMatch(x -> x.getStatus() == Status.ABERTO);
            resultado("T10 Filtro e resumo", contagemOk && filtroOk, "resumo=" + resumo + ", altas=" + altas);
        } finally {
            limpar();
        }
        System.out.println(reprovados == 0 ? "\nTodos os testes foram APROVADOS." : "\n" + reprovados + " teste(s) REPROVADO(S).");
        System.exit(reprovados == 0 ? 0 : 1);
    }

    private static boolean recusa(Callable<?> acao) {
        return mensagemDeRecusa(acao) != null;
    }

    /** Devolve a mensagem da RegraNegocioException, ou null se a ação não foi recusada. */
    private static String mensagemDeRecusa(Callable<?> acao) {
        try {
            acao.call();
            return null;
        } catch (RegraNegocioException e) {
            return e.getMessage();
        } catch (Exception e) {
            return null;
        }
    }

    private static void resultado(String nome, boolean ok, String detalhe) {
        if (!ok) reprovados++;
        System.out.printf("%-42s %s  %s%n", nome, ok ? "APROVADO " : "REPROVADO", detalhe);
    }

    private static void limpar() throws SQLException {
        try (Connection con = Conexao.abrir(); PreparedStatement ps = con.prepareStatement("DELETE FROM chamados WHERE id = ?")) {
            for (int id : criados) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }
        }
    }
}
