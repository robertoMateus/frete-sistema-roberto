# GW Gestão de Fretes

Sistema Java web para gestão de fretes, motoristas, veículos, clientes, ocorrências e relatórios.

## Visão geral

Este projeto é uma aplicação web Java EE com JSP/Servlets, conectada a um banco PostgreSQL. Ele suporta:

- autenticação de usuários
- cadastro, edição e listagem de motoristas
- cadastro, edição e listagem de fretes
- cadastro e gerenciamento de clientes
- cadastro e gerenciamento de veículos
- registro de ocorrências de frete
- controle de manutenções de veículos
- geração de relatórios em PDF (fretes em aberto e romaneio por motorista/data)

## Tecnologias usadas

- Java 8
- Servlet API 4.0
- JSP + JSTL
- PostgreSQL
- Apache DBCP2 para pool de conexões
- JasperReports para geração de PDF

## Estrutura do projeto

- `src/main/java/br/com/gwfrete/controller` — controladores HTTP (Servlets)
- `src/main/java/br/com/gwfrete/bo` — regras de negócio
- `src/main/java/br/com/gwfrete/dao` — acesso a banco de dados
- `src/main/java/br/com/gwfrete/model` — modelos de domínio
- `src/main/java/br/com/gwfrete/util` — utilitários de apoio
- `src/main/resources/db` — configurações e scripts de banco
- `src/main/webapp/WEB-INF/views` — páginas JSP 
- `src/main/webapp/WEB-INF/reports` — relatórios Jasper

## Rotas principais

| URL | Descrição |
|---|---|
| `/auth/login` | página de login |
| `/auth/logout` | logout |
| `/home` | dashboard principal |
| `/motoristas/listar` | lista de motoristas |
| `/motoristas/novo` | cadastro de motorista |
| `/fretes/listar` | lista de fretes |
| `/fretes/novo` | cadastro de frete |
| `/veiculos/listar` | lista de veículos |
| `/manutencoes/listar` | lista de manutenções |
| `/ocorrencias/*` | cadastro/lista de ocorrências |
| `/precosRota/*` | gestão de preços de rota |
| `/relatorios/fretes-em-aberto` | relatório PDF de fretes em aberto |
| `/relatorios/romaneio` | relatório PDF de romaneio por motorista e data |

## Relatórios

- `relatorios/fretes-em-aberto` gera um PDF com fretes que não estão cancelados.
- `relatorios/romaneio?idMotorista={id}&data={yyyy-MM-dd}` gera um romaneio de carga para um motorista na data escolhida.

### Exemplo de URL de romaneio

```text
/relatorios/romaneio?idMotorista=2&data=2026-05-10
```

## Manutenção de veículos

O sistema também mantém um controle de manutenção de veículos como parte do fluxo de operações.

- A rota principal é `/manutencoes/*`, com páginas de listagem, cadastro, edição, detalhe, conclusão e exclusão.
- Uma manutenção só pode ser registrada em um veículo existente e não pode ser criada se o veículo estiver em status `EM_VIAGEM`.
- Ao registrar uma manutenção, o veículo passa automaticamente para o status `EM_MANUTENCAO`.
- A conclusão de manutenção define a data de fim e, se não houver outra manutenção em aberto para o mesmo veículo, devolve o veículo ao status `DISPONIVEL`.
- Não é permitido editar ou excluir uma manutenção já concluída.

A lista de manutenções é usada para monitorar veículos que precisam de atendimento e evitar alocar um veículo em viagem enquanto ele está em manutenção.

## Configuração do banco de dados

As configurações estão em `src/main/resources/db/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/frete_gestao
db.username=postgres
db.password=123456789
db.driver=org.postgresql.Driver
```

### Scripts de criação e dados de exemplo

- `src/main/resources/db/sql/001_create_tables.sql`
- `src/main/resources/db/sql/002_constraints.sql`
- `src/main/resources/db/sql/003_indexes.sql`
- `src/main/resources/db/sql/004_inserts.sql`

Esses scripts criam as tabelas e inserem dados iniciais de clientes, motoristas, veículos e fretes.

## Usuário de teste

Dados de login presentes no script de inserção:

- `admin` / `admin`

## Dados de teste úteis

Exemplo de motorista válido para gerar o relatório:

- Motorista: `José Antônio Santos`
- `idMotorista`: `2`
- Data de emissão para o romaneio: `2026-05-10`
- Frete registrado: `FRT-2026-00002`

## Como rodar o projeto

1. Garanta que o PostgreSQL esteja rodando e o banco `frete_gestao` esteja criado.
2. Ajuste `src/main/resources/db/db.properties` se necessário.
3. Execute a criação das tabelas e inserts com os scripts SQL.
4. No diretório do projeto, execute:

```bash
gradle clean build
```

5. Implante o WAR gerado em `build/libs/frete-gestao.war` no Tomcat ou outro servidor Java EE.
6. Acesse a aplicação em `http://localhost:8080/frete-gestao`.

## Observações importantes

- A aplicação usa um `AuthFilter` que exige login para acessar todas as rotas, exceto `/auth/login` e `/auth/logout`.
- O `ConexaoPool` usa Apache DBCP2.
- O relatório de romaneio depende de `ID_MOTORISTA` e `DATA_ROMANEIO` com status de frete diferente de `CANCELADO`.
- Há validações de negócio importantes, como:
  - não permitir exclusão de motorista/veículo/cliente com fretes vinculados
  - não permitir edição de fretes quando o status não estiver em `EMITIDO`
