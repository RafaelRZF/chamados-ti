package br.senai.chamados;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Interface de terminal usada pelo atendente de TI. */
public class Main {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ChamadoService service = new ChamadoService(new ChamadoDAO());
    private final Scanner entrada = new Scanner(System.in, "UTF-8");

    public static void main(String[] args) {
        new Main().executar();
    }

    private void executar() {
        boolean continuar = true;
        while (continuar) {
            exibirMenu();
            String opcao = ler("Escolha uma opção: ");
            try {
                switch (opcao) {
                    case "1": cadastrar(); break;
                    case "2": listar(); break;
                    case "3": consultar(); break;
                    case "4": atualizarStatus(); break;
                    case "5": filtrar(); break;
                    case "6": resumo(); break;
                    case "0": continuar = false; System.out.println("Até logo!"); break;
                    default:  System.out.println("Opção inválida. Digite um número de 0 a 6.");
                }
            } catch (RegraNegocioException e) {
                System.out.println("\n[ATENÇÃO] " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("\n[ERRO NO BANCO] Não foi possível concluir a operação: " + e.getMessage());
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n===== TechServ - Controle de Chamados de TI =====");
        System.out.println("1 - Cadastrar chamado");
        System.out.println("2 - Listar chamados");
        System.out.println("3 - Consultar chamado por identificador");
        System.out.println("4 - Atualizar status");
        System.out.println("5 - Filtrar por prioridade e/ou status");
        System.out.println("6 - Resumo por status");
        System.out.println("0 - Sair");
    }

    private void cadastrar() throws SQLException {
        String solicitante = ler("Solicitante: ");
        String descricao = ler("Descrição: ");
        String prioridade = ler("Prioridade (baixa, média ou alta): ");
        int id = service.cadastrar(solicitante, descricao, prioridade);
        System.out.println("\nChamado cadastrado com sucesso! Identificador: " + id);
        exibirDetalhe(service.consultar(id));
    }

    private void listar() throws SQLException {
        exibirTabela(service.listar());
    }

    private void consultar() throws SQLException {
        exibirDetalhe(service.consultar(lerId()));
    }

    private void atualizarStatus() throws SQLException {
        int id = lerId();
        Chamado atual = service.consultar(id);
        System.out.println("Status atual: " + atual.getStatus().getRotulo());
        String novo = ler("Novo status (aberto, em atendimento ou concluído): ");
        Chamado atualizado = service.atualizarStatus(id, novo);
        System.out.println("\nStatus atualizado para: " + atualizado.getStatus().getRotulo());
    }

    private void filtrar() throws SQLException {
        System.out.println("Deixe em branco o filtro que não quiser usar.");
        String prioridade = ler("Prioridade (baixa, média ou alta): ");
        String status = ler("Status (aberto, em atendimento ou concluído): ");
        exibirTabela(service.filtrar(prioridade, status));
    }

    private void resumo() throws SQLException {
        Map<Status, Integer> contagem = service.resumoPorStatus();
        System.out.println("\n--- Resumo por status ---");
        for (Status s : Status.values())
            System.out.printf("%-15s %d%n", s.getRotulo(), contagem.get(s));
    }

    // ---------- apoio ----------

    private String ler(String pergunta) {
        System.out.print(pergunta);
        return entrada.hasNextLine() ? entrada.nextLine() : "";
    }

    private int lerId() {
        String texto = ler("Identificador do chamado: ").trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new RegraNegocioException("Identificador inválido: digite apenas números.");
        }
    }

    private void exibirTabela(List<Chamado> chamados) {
        if (chamados.isEmpty()) {
            System.out.println("\nNenhum chamado encontrado.");
            return;
        }
        System.out.printf("%n%-4s %-15s %-10s %-15s %-17s %s%n",
                "ID", "Solicitante", "Prioridade", "Status", "Abertura", "Descrição");
        for (Chamado c : chamados) {
            System.out.printf("%-4d %-15s %-10s %-15s %-17s %s%n",
                    c.getId(), cortar(c.getSolicitante(), 15), c.getPrioridade().getRotulo(),
                    c.getStatus().getRotulo(), c.getDataAbertura().format(DATA), cortar(c.getDescricao(), 50));
        }
        System.out.println("Total: " + chamados.size());
    }

    private void exibirDetalhe(Chamado c) {
        System.out.println("\n--- Chamado #" + c.getId() + " ---");
        System.out.println("Solicitante : " + c.getSolicitante());
        System.out.println("Descrição   : " + c.getDescricao());
        System.out.println("Prioridade  : " + c.getPrioridade().getRotulo());
        System.out.println("Status      : " + c.getStatus().getRotulo());
        System.out.println("Abertura    : " + c.getDataAbertura().format(DATA));
    }

    private String cortar(String texto, int limite) {
        return texto.length() <= limite ? texto : texto.substring(0, limite - 3) + "...";
    }
}
