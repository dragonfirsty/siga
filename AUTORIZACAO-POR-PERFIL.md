# Autorização por perfil nas rotas

## Escopo

Este documento descreve a política de autorização do Integrante 2 para proteger as rotas do SIGA conforme os perfis `ADMIN`, `PROFESSOR` e `ALUNO`.

## Ponto central da configuração

Arquivo: `src/main/java/com/fatec/siga/config/SecurityConfig.java`

A aplicação aplica as regras no `SecurityFilterChain`, antes de encaminhar cada requisição aos controllers. Assim, a proteção abrange todos os métodos HTTP da rota, incluindo `GET`, `POST`, `PUT` e `DELETE`, e não depende da implementação visual da página.

## Matriz de acesso

| Grupo de rotas | Acesso |
|---|---|
| `/login`, `/error`, `/css/**`, `/js/**`, `/images/**`, `/webjars/**`, `/favicon.ico` | Público |
| `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**` | Público |
| `/admin`, `/admin/**` | `ADMIN` |
| `/api/cursos`, `/api/cursos/**` | `ADMIN` |
| `/api/disciplinas`, `/api/disciplinas/**` | `ADMIN` |
| `/api/turmas`, `/api/turmas/**` | `ADMIN` |
| `/api/enderecos`, `/api/enderecos/**` | `ADMIN` |
| `/h2-console`, `/h2-console/**` | `ADMIN` |
| `/professor`, `/professor/**` | `PROFESSOR` |
| `/aluno`, `/aluno/**` | `ALUNO` |
| `/`, `/inicio` | Qualquer usuário autenticado |
| Qualquer outro caminho | Negado por padrão |

O console do H2 continua com a exceção CSRF já existente para suportar seu formulário próprio. O acesso foi restringido a administradores; CSRF permanece ativo para as demais rotas da aplicação.

## Como os nomes de perfil são verificados

O `UsuarioDetailsService` transforma os perfis persistidos nas authorities `ROLE_ADMIN`, `ROLE_PROFESSOR` e `ROLE_ALUNO`. O método `hasRole("ADMIN")` do Spring Security verifica a authority `ROLE_ADMIN` automaticamente. Portanto, as regras do `SecurityConfig` devem continuar usando o nome sem o prefixo `ROLE_`.

## Resultado esperado por cenário

- visitante não autenticado solicita rota protegida: o fluxo de formulário encaminha para `/login`;
- usuário autenticado com o papel correto solicita a rota: a requisição continua para o controller;
- usuário autenticado sem o papel exigido solicita a rota: o Spring Security responde com acesso negado (`403`);
- caminho que não foi declarado na política: acesso negado por padrão, até que uma regra explícita seja definida.

## Relação com os controllers

As regras atuais cobrem os controllers existentes:

- páginas administrativas: `src/main/java/com/fatec/siga/web/CursoController.java`, `DisciplinaController.java` e `TurmaController.java`;
- APIs administrativas: `src/main/java/com/fatec/siga/web/CursoRestController.java`, `DisciplinaRestController.java` e `TurmaRestController.java`;
- consulta auxiliar de CEP: `src/main/java/com/fatec/siga/controller/EnderecoController.java`;
- operações do professor: `src/main/java/com/fatec/siga/controller/ProfessorController.java`;
- consultas do aluno: `src/main/java/com/fatec/siga/controller/AlunoController.java`;
- páginas iniciais por perfil: `src/main/java/com/fatec/siga/web/HomeController.java`.

O endpoint de CEP foi agrupado como administrativo por ser recurso de apoio aos cadastros administrativos, não uma consulta de dados acadêmicos do aluno ou professor.

## Incluindo uma nova rota

Ao adicionar um controller:

1. escolha o perfil proprietário da funcionalidade;
2. crie uma regra específica em `SecurityConfig` antes da regra final;
3. inclua o caminho na matriz deste documento;
4. se a rota tiver de ser pública, declare-a explicitamente como `permitAll`;
5. não amplie `anyRequest()` para liberar caminhos desconhecidos.

## Validação

A compilação do projeto foi executada sem rodar testes unitários, conforme solicitado.
As verificações manuais recomendadas são entrar com cada perfil e tentar abrir as áreas administrativas, do professor e do aluno, incluindo uma requisição não autenticada e uma rota inexistente.
