# Planejamento e modelo de dados

## Requisitos funcionais
- RF01 Cadastrar chamado (solicitante, descrição, prioridade), com status inicial *aberto* e data de abertura automática.
- RF02 Listar chamados com identificador e status.
- RF03 Consultar chamado pelo identificador, com mensagem clara se não existir.
- RF04 Atualizar status respeitando aberto → em atendimento → concluído.
- RF05 Filtrar chamados por prioridade e/ou status.
- RF06 Apresentar resumo com a quantidade de chamados por status.

## Requisitos não funcionais / regras
- RN01 Solicitante obrigatório, mínimo de 3 caracteres após remover espaços das pontas.
- RN02 Descrição obrigatória, mínimo de 10 caracteres após remover espaços das pontas.
- RN03 Prioridade: baixa, média ou alta. Status: aberto, em atendimento ou concluído.
- RN04 Chamados concluídos continuam disponíveis para consulta (não há exclusão).
- RN05 Erros (id inexistente, entrada inválida, falha no banco) mostram mensagem e não encerram o programa.
- RN06 Dados persistidos em MySQL; consultas com dados do usuário usam parâmetros (`PreparedStatement`).

## Desenho do menu
```
===== TechServ - Controle de Chamados de TI =====
1 - Cadastrar chamado
2 - Listar chamados
3 - Consultar chamado por identificador
4 - Atualizar status
5 - Filtrar por prioridade e/ou status
6 - Resumo por status
0 - Sair
```

## Tabela `chamados`
| Campo | Tipo | Restrições |
|---|---|---|
| **id** | INT UNSIGNED | **PK**, AUTO_INCREMENT, NOT NULL |
| solicitante | VARCHAR(100) | NOT NULL; CHECK `CHAR_LENGTH(TRIM(solicitante)) >= 3` |
| descricao | VARCHAR(500) | NOT NULL; CHECK `CHAR_LENGTH(TRIM(descricao)) >= 10` |
| prioridade | ENUM('BAIXA','MEDIA','ALTA') | NOT NULL |
| status | ENUM('ABERTO','EM_ATENDIMENTO','CONCLUIDO') | NOT NULL, DEFAULT 'ABERTO' |
| data_abertura | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

Índices em `status` e `prioridade` para acelerar filtros e o resumo.

A regra de transição de status fica na camada de serviço (`ChamadoService`) e o `UPDATE` ainda confere o status anterior (`WHERE id = ? AND status = ?`).
