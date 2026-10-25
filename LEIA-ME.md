# Sistema de Controle de Chamados de TI — TechServ

Aplicação de terminal em **Java 17+** com banco **MySQL 8.0.16+** (JDBC).
Permite cadastrar, listar, consultar, filtrar e atualizar o status de chamados de suporte, além de mostrar o resumo por status.

## Requisitos
- JDK 17 ou superior
- MySQL 8.0.16 ou superior em execução (versões antigas ignoram o `CHECK`)
- Maven 3.8+ (ou o driver `mysql-connector-j` em `.jar`, se for compilar sem Maven)

## 1. Criar o banco
```
mysql -u root -p < sql/01_criar_banco.sql
```
O script pode ser executado mais de uma vez sem erro.

## 2. Configurar a conexão
Por padrão a aplicação usa `localhost:3306`, banco `techserv_chamados`, usuário `root` e senha vazia.
Para mudar, defina as variáveis de ambiente antes de executar:

| Variável | Exemplo |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/techserv_chamados?useSSL=false&allowPublicKeyRetrieval=true` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | `minha_senha` |

Windows (cmd): `set DB_PASSWORD=minha_senha` — Linux/macOS: `export DB_PASSWORD=minha_senha`

## 3. Executar com Maven
```
mvn compile exec:java
```
Teste automatizado (T01 a T10):
```
mvn compile exec:java -Dexec.mainClass=br.senai.chamados.TesteAutomatizado
```

## Alternativa: sem Maven
Baixe o `mysql-connector-j-8.x.jar` e, na pasta do projeto:
```
javac -encoding UTF-8 -d out src/main/java/br/senai/chamados/*.java
java -Dfile.encoding=UTF-8 -cp "out:mysql-connector-j-8.4.0.jar" br.senai.chamados.Main
```
(No Windows, troque `:` por `;` no `-cp`.)

> Se os acentos aparecerem errados no Windows, rode `chcp 65001` antes de executar.

## Menu
1 Cadastrar · 2 Listar · 3 Consultar por identificador · 4 Atualizar status · 5 Filtrar · 6 Resumo por status · 0 Sair

## Roteiro da demonstração (seção 6 da atividade)
1. Cadastre os quatro chamados (Ana, Bruno, Carla e Diego) com as prioridades indicadas.
2. Opção 4: chamado de Ana → `em atendimento`.
3. Opção 4: chamado de Bruno → `em atendimento` e depois `concluído`.
4. Opção 5: prioridade `alta`, status em branco.
5. Opção 6: resultado esperado — 2 abertos, 1 em atendimento, 1 concluído.

## Estrutura
```
sql/01_criar_banco.sql       script de criação do banco e da tabela
docs/modelo_de_dados.md      requisitos, menu e modelo da tabela
docs/relatorio_testes.md     relatório dos testes T01–T10
src/main/java/br/senai/chamados/
    Main                     menu e interação com o atendente
    ChamadoService           validações e regras de negócio
    ChamadoDAO               acesso ao banco (consultas parametrizadas)
    Conexao, Chamado, Status, Prioridade, Texto, RegraNegocioException
    TesteAutomatizado        executa T01–T10
```
