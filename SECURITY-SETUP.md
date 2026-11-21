# Configuração inicial de segurança

## Contexto

Este documento registra o trabalho do **Integrante 2 - Autenticação e segurança**, conforme a seção "Divisão do trabalho" do `SIGA2_Plano_do_Projeto.pdf`.

O PDF atribui ao integrante:

- configuração base do Spring Security;
- usuários e perfis persistidos;
- tela de login personalizada;
- autorização por perfil nas rotas.

O domínio de `Usuario` e `Perfil` está atribuído ao Integrante 1 no mesmo documento e ainda não existe neste estado do projeto. Por isso, esta etapa prepara a infraestrutura de segurança sem criar entidades duplicadas. A integração com o banco deve ser feita quando o domínio do Integrante 1 estiver disponível.

## Modificações realizadas

### 1. Configuração da cadeia de filtros

Arquivo: `src/main/java/com/fatec/siga/config/SecurityConfig.java`

Foi criada a configuração `SecurityConfig`, que define um `SecurityFilterChain` com:

- `/login` e recursos estáticos públicos;
- `/admin/**` restrito ao papel `ADMIN`;
- `/professor/**` restrito ao papel `PROFESSOR`;
- `/aluno/**` restrito ao papel `ALUNO`;
- qualquer outra rota exigindo autenticação.

As regras usam `hasRole`, portanto o Spring Security espera as authorities com o prefixo interno `ROLE_` (por exemplo, o perfil `ADMIN` deve ser convertido em `ROLE_ADMIN` pelo serviço de usuários).

Foi mantida a proteção CSRF padrão. Ela é necessária para proteger o formulário de login e futuras operações de alteração feitas pela aplicação web.

### 2. Codificação de senhas

Foi registrado um `BCryptPasswordEncoder` como bean. Senhas não devem ser armazenadas em texto puro; ao persistir usuários, o serviço responsável deve salvar o resultado de `passwordEncoder.encode(senha)`.

A senha recebida no login será comparada pelo Spring Security com o hash BCrypt armazenado, sem que a aplicação precise implementar essa comparação manualmente.

### 3. Fluxo de login personalizado

Arquivo: `src/main/java/com/fatec/siga/web/LoginController.java`

O controller disponibiliza `GET /login`, que renderiza a página própria do sistema. O processamento do formulário continua sendo realizado pelo filtro do Spring Security em `POST /login`; não foi criado um endpoint manual para receber ou tratar senhas.

Arquivo: `src/main/resources/templates/login.html`

Foi adicionada uma tela Thymeleaf com:

- campos `username` e `password` nos nomes esperados pelo Spring Security;
- envio para `POST /login`;
- mensagens para falha (`?error`) e logout (`?logout`);
- atributos de acessibilidade básicos nos campos e mensagens.

### 4. Logout

O logout redireciona para `/login?logout`, invalida a sessão HTTP e remove o cookie `JSESSIONID`.

## Próxima integração: usuários e perfis persistidos

Quando o Integrante 1 entregar as entidades e o repositório, o próximo passo é:

1. criar um serviço que implemente `UserDetailsService`;
2. buscar o usuário pelo identificador definido no domínio;
3. transformar cada `Perfil` em uma authority `ROLE_ADMIN`, `ROLE_PROFESSOR` ou `ROLE_ALUNO`;
4. injetar esse serviço no contexto do Spring Security;
5. garantir que as senhas persistidas sejam hashes BCrypt;
6. adicionar testes de autenticação e autorização usando usuários reais do banco.

Não foi criado um usuário de demonstração nesta etapa, pois isso mascararia a integração com o banco e poderia deixar credenciais fixas no projeto.

## Como continuar

O ponto de entrada da configuração é `SecurityConfig`. O integrante responsável pela persistência deve manter as rotas e os papéis definidos aqui ou atualizar este documento e os testes caso o domínio adote outros nomes.

Rotas destinadas às funcionalidades futuras:

| Rota | Papel exigido |
|---|---|
| `/admin/**` | `ADMIN` |
| `/professor/**` | `PROFESSOR` |
| `/aluno/**` | `ALUNO` |
| demais rotas | usuário autenticado |

As dependências de `spring-boot-starter-security` e `thymeleaf-extras-springsecurity6` já estavam presentes no `pom.xml`; por isso não houve alteração de dependências.

## Validação desta etapa

- `.\mvnw.cmd -DskipTests compile`: **passou**.
- `.\mvnw.cmd test`: foi executado, mas não conseguiu iniciar os testes porque o ambiente disponível usa Java 8. O projeto declara Java 21 e o JUnit Platform utilizado exige uma versão mais recente da JVM. É necessário executar os testes com um JDK 21 configurado no `JAVA_HOME`.
