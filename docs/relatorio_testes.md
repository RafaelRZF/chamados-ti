# Relatório de testes

Ambiente da execução registrada: Java 21, MariaDB 10.11 (compatível com MySQL), driver JDBC, execução da classe `TesteAutomatizado` e do menu.
> Refaça os testes no seu computador (MySQL) e confira/atualize a tabela e as evidências antes de entregar.

| Teste | Resultado obtido | Aprovado / Reprovado | Evidência ou correção |
|---|---|---|---|
| T01 | Chamado criado com id 1, status Aberto e data preenchida | Aprovado | Saída do `TesteAutomatizado`; tela de cadastro do menu |
| T02 | Cadastro recusado: "Solicitante inválido: informe pelo menos 3 caracteres..." | Aprovado | `TesteAutomatizado`; validação em `ChamadoService.cadastrar` |
| T03 | Cadastro recusado ("curta" tem 5 caracteres) | Aprovado | `TesteAutomatizado` |
| T04 | Recusado: "Prioridade inválida. Use: baixa, média ou alta." | Aprovado | `TesteAutomatizado` |
| T05 | "Chamado #999 não encontrado." e o programa continua no menu | Aprovado | Execução do menu (opção 3 com id 999) |
| T06 | Aberto → Em atendimento | Aprovado | Menu opção 4 |
| T07 | "Alteração recusada: de Aberto para Concluído não é permitido. A próxima etapa permitida é: Em atendimento." | Aprovado | Menu opção 4 com o chamado 4 |
| T08 | Em atendimento → Concluído | Aprovado | Menu opção 4 |
| T09 | Após fechar e abrir o programa, o resumo continuou 2 / 1 / 1 | Aprovado | Segunda execução do programa mostrando o mesmo resumo |
| T10 | Filtro "alta" retornou os chamados 1 e 4; resumo: 2 abertos, 1 em atendimento, 1 concluído | Aprovado | Menu opções 5 e 6 |

## Falhas encontradas e correções
1. **Script SQL não repetível.** Na segunda execução de `01_criar_banco.sql` ocorreu `ERROR 1061: Duplicate key name 'idx_chamados_status'`, pois os `CREATE INDEX` ficavam fora do `CREATE TABLE ... IF NOT EXISTS`. **Correção:** os índices foram movidos para dentro do `CREATE TABLE`; o script rodou duas vezes seguidas sem erro.
2. **Demonstração com identificadores errados.** Num primeiro ensaio os ids não começavam em 1 porque o auto incremento já tinha sido usado pelos testes. A aplicação tratou corretamente ("Chamado #N não encontrado"), e o ensaio foi refeito com o banco recriado. Lição: os ids dependem do banco; conferir o id na listagem antes de atualizar.
