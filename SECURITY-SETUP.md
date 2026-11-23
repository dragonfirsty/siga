# Configuração de usuários e segurança

## Responsável

Esta implementação corresponde ao trabalho do **Integrante 2 - Autenticação e segurança**, definido na seção 6 do `SIGA2_Plano_do_Projeto.pdf`.

## Objetivo entregue

- usuários e perfis persistidos por JPA;
- autenticação baseada no e-mail;
- senhas armazenadas com BCrypt;
- tela de login personalizada;
- autorização por perfil;
- telas iniciais distintas para administrador, professor e aluno;
- dados fictícios disponíveis na inicialização da aplicação;
- nenhum teste unitário mantido no projeto.

## Entidades já utilizadas

### Usuario

Arquivo: `src/main/java/com/fatec/siga/domain/Usuario.java`

Representa um usuário que pode entrar no sistema. Os principais campos são:

- `nome`: nome visível do usuário;
- `email`: identificador usado no login;
- `senha`: hash BCrypt da senha;
- `ativo`: informa se a conta pode autenticar;
- `perfis`: conjunto dos perfis associados ao usuário.

O e-mail é único no banco e o relacionamento com perfis é uma relação muitos para muitos.

### Perfil

Arquivo: `src/main/java/com/fatec/siga/domain/Perfil.java`

Representa os papéis definidos pelo projeto:

- `ALUNO`;
- `PROFESSOR`;
- `ADMIN`.

A tabela mantém os perfis persistidos e os associa aos usuários pela tabela `usuario_perfil`.

## Autenticação persistida

Arquivo: `src/main/java/com/fatec/siga/service/UsuarioDetailsService.java`

A classe implementa `UserDetailsService` e é descoberta automaticamente pelo Spring.

No login:

1. o Spring envia o valor digitado no campo `username`;
2. o serviço busca o usuário por `UsuarioRepository.findByEmail`;
3. cada perfil é convertido em uma authority `ROLE_<perfil>`;
4. `Usuario.isAtivo()` define se a conta está habilitada;
5. o Spring compara a senha digitada com o hash BCrypt persistido.

Exemplos de authorities:

- perfil `ADMIN` -> `ROLE_ADMIN`;
- perfil `PROFESSOR` -> `ROLE_PROFESSOR`;
- perfil `ALUNO` -> `ROLE_ALUNO`.

O serviço usa o e-mail como login. Os demais atributos da conta são mantidos habilitados.

## Carregamento dos dados iniciais

Arquivo: `src/main/java/com/fatec/siga/config/DadosIniciaisConfig.java`

A aplicação implementa `ApplicationRunner` para criar na inicialização os perfis e usuários fictícios que ainda não existam no banco.

Perfis criados:

- `ALUNO`;
- `PROFESSOR`;
- `ADMIN`.

Usuários criados:

| Perfil | E-mail | Senha inicial |
|---|---|---|
| Administrador | `admin@siga.local` | `admin123` |
| Professor | `professor@siga.local` | `professor123` |
| Aluno | `aluno@siga.local` | `aluno123` |

As senhas não são inseridas em texto puro. Antes de chamar `UsuarioRepository.save`, o configurador passa a senha por `PasswordEncoder.encode`.

O carregamento verifica `existsByEmail` antes de criar um usuário. Assim, executar o configurador novamente não cria duplicatas.

## Cadeia de segurança

Arquivo: `src/main/java/com/fatec/siga/config/SecurityConfig.java`

A configuração cria um `PasswordEncoder` BCrypt e uma `SecurityFilterChain`.

Rotas públicas:

- `/login`;
- `/error`;
- recursos estáticos;
- `/h2-console/**`.

Rotas protegidas:

| Rota | Perfil necessário |
|---|---|
| `/admin/**` | `ADMIN` |
| `/api/cursos/**` | `ADMIN` |
| `/professor/**` | `PROFESSOR` |
| `/aluno/**` | `ALUNO` |
| demais rotas | usuário autenticado |

O formulário envia `POST /login`. Em caso de falha, a aplicação volta para `/login?error`. O logout invalida a sessão, remove o cookie `JSESSIONID` e volta para `/login?logout`.

A proteção CSRF permanece ativa para as rotas da aplicação. Ela foi ignorada apenas no caminho do console do H2 para permitir o uso desse console durante o desenvolvimento.

## Tela de login

Arquivo: `src/main/java/com/fatec/siga/web/LoginController.java`

Disponibiliza `GET /login`.

Arquivo: `src/main/resources/templates/login.html`

Possui:

- campo de e-mail;
- campo de senha;
- envio automático para `POST /login`;
- mensagem de credenciais inválidas;
- mensagem de logout concluído.

## Telas iniciais por perfil

Arquivo: `src/main/java/com/fatec/siga/web/HomeController.java`

Após o login, `GET /` verifica as authorities da autenticação e encaminha o usuário:

- `ROLE_ADMIN` -> `/admin`;
- `ROLE_PROFESSOR` -> `/professor`;
- `ROLE_ALUNO` -> `/aluno`.

Os destinos são:

- `src/main/resources/templates/admin/inicio.html`;
- `src/main/resources/templates/professor/inicio.html`;
- `src/main/resources/templates/aluno/inicio.html`.

Cada tela mostra o e-mail autenticado e um formulário de logout.

## Repositórios

`src/main/java/com/fatec/siga/repository/UsuarioRepository.java`

- `findByEmail(String email)` usado pelo `UserDetailsService`;
- `existsByEmail(String email)` usado para evitar duplicidade no carregamento inicial.

`src/main/java/com/fatec/siga/repository/PerfilRepository.java`

- `findByNome(NomePerfil nome)` usado para localizar ou criar perfis na inicialização.

## Configuração do banco

Arquivo: `src/main/resources/application.properties`

Foi configurado um banco H2 em memória para desenvolvimento:

- URL: `jdbc:h2:mem:siga`;
- console: `http://localhost:8080/h2-console`;
- usuário: `sa`;
- senha vazia;
- Hibernate com `ddl-auto=create-drop`.

A definição `create-drop` recria o esquema ao iniciar e removê-lo ao encerrar o processo. Os perfis e usuários demonstrativos são recriados pelo `DadosIniciaisConfig` em cada inicialização.

Para PostgreSQL, mantenha as entidades e repositórios existentes e ajuste o datasource no perfil de produção sem remover o `UserDetailsService`.

## Integração com os repositórios existentes

`Aluno` e `Professor` podem estar associados ao mesmo `Usuario`. Os relacionamentos já disponíveis em:

- `src/main/java/com/fatec/siga/domain/Aluno.java`;
- `src/main/java/com/fatec/siga/domain/Professor.java`.

Esse vínculo permite localizar os dados específicos do usuário sem duplicar e-mail ou senha.

## Testes

O arquivo `src/test/java/com/fatec/siga/SigaApplicationTests.java` foi removido conforme a solicitação.
Não há arquivos unitários em `src/test` neste estado do projeto.

## Como validar manualmente

1. execute `.\mvnw.cmd spring-boot:run`;
2. acesse `http://localhost:8080`;
3. faça login com um dos usuários da tabela de dados iniciais;
4. confirme o redirecionamento para a tela correspondente ao perfil;
5. tente acessar uma área pertencente a outro perfil;
6. confirme que o acesso é negado;
7. faça logout e confirme o retorno para `/login?logout`.

A aplicação foi compilada com JDK 21 e iniciada com sucesso na porta 8080.
O login persistido do usuário administrador também foi validado manualmente, com redirecionamento para `/admin`.

Credenciais para testes locais:

- administrador: `admin@siga.local` / `admin123`;
- professor: `professor@siga.local` / `professor123`;
- aluno: `aluno@siga.local` / `aluno123`.

Essas credenciais são apenas dados fictícios de desenvolvimento.

## Próximos passos

- adicionar CRUD administrativo de disciplinas e turmas;
- adicionar criação de aulas na área do professor;
- adicionar consulta de aulas e frequência na área do aluno;
- usar perfis persistidos como base das novas regras de acesso;
- configurar um perfil PostgreSQL para ambiente de produção;
