# SIGA

Sistema Integrado de Gestão Acadêmica desenvolvido como projeto da FATEC em Spring Boot.

## Integrantes

- Leonardo Moya
- Vinicius Silvestre
- Henrique Ferreira

## Stack

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Thymeleaf (templates server-side)
- Bean Validation
- H2 Database (desenvolvimento)
- PostgreSQL (produção)
- Springdoc OpenAPI (Swagger UI)
- Maven Wrapper

## Pré-requisitos

- **JDK 21** instalado e configurado no `JAVA_HOME`
- **Git** para clonar o repositório
- (Opcional) IDE como IntelliJ IDEA, Eclipse ou VS Code com extensão Java

Não é necessário instalar o Maven — o projeto usa o Maven Wrapper (`mvnw`).

### Verificando o Java

```bash
java -version
```

A saída precisa mostrar algo como `openjdk version "21.x.x"`.

## Clonando o projeto

```bash
git clone <URL-do-repositorio>
cd siga
```

## Checkout da sua branch

O projeto usa branches por desenvolvedor para isolar o trabalho antes da integração na `main`.

```bash
git fetch --all
git checkout main
```

Para listar todas as branches:

```bash
git branch -a
```

## Rodando em modo desenvolvimento

No Windows (PowerShell ou CMD):

```bash
mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

A aplicação sobe por padrão em **http://localhost:8080**.

Se for a primeira vez, o Maven vai baixar todas as dependências. Pode levar alguns minutos.

## Acessos úteis

Com a aplicação rodando:

- **Aplicação**: http://localhost:8080
- **H2 Console** (banco em memória): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:siga`
  - Usuário: `sa`
  - Senha: (deixar em branco)
- **Swagger UI** (documentação da API): http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/v3/api-docs

## Aplicação publicada

- **Site**: https://siga2.onrender.com/
- **Login**: https://siga2.onrender.com/login
- **Swagger UI**: https://siga2.onrender.com/swagger-ui.html
- **API Docs JSON**: https://siga2.onrender.com/v3/api-docs

### Rotas web

| Rota | Perfil | Função |
|---|---|---|
| `/login` | Público | Autenticação |
| `/` | Autenticado | Redirecionamento conforme o perfil |
| `/inicio` | Público | Página demonstrativa |
| `/admin` | ADMIN | Área administrativa |
| `/admin/cursos` | ADMIN | CRUD web de cursos |
| `/admin/disciplinas` | ADMIN | CRUD web de disciplinas |
| `/admin/turmas` | ADMIN | CRUD web de turmas |
| `/professor/aulas` | PROFESSOR | Lista das aulas |
| `/professor/aulas/nova` | PROFESSOR | Criação de aula |
| `/professor/aulas/{id}/presenca` | PROFESSOR | Registro de presença |
| `/aluno/aulas` | ALUNO | Consulta das aulas |
| `/aluno/frequencia` | ALUNO | Consulta da frequência |

### API REST

| Recurso | Métodos | Rota base |
|---|---|---|
| Cursos | GET, POST, PUT, DELETE | `/api/cursos` |
| Disciplinas | GET, POST, PUT, DELETE | `/api/disciplinas` |
| Turmas | GET, POST, PUT, DELETE | `/api/turmas` |
| ViaCEP | GET | `/api/enderecos/{cep}` |

Os endpoints REST de cursos, disciplinas e turmas exigem autenticação como ADMIN.

## Build

Para gerar o JAR executável:

```bash
./mvnw clean package
```

O artefato final fica em `target/siga-0.0.1-SNAPSHOT.jar`. Para rodar o JAR:

```bash
java -jar target/siga-0.0.1-SNAPSHOT.jar
```

## Segurança

O projeto possui login persistido com usuários e perfis armazenados via JPA.
As credenciais fictícias de desenvolvimento e a configuração completa estão documentadas em
[`SECURITY-SETUP.md`](./SECURITY-SETUP.md).

## Estrutura do projeto

```
siga/
├── .mvn/wrapper/              Maven wrapper
├── src/
│   ├── main/
│   │   ├── java/com/fatec/siga/
│   │   │   └── SigaApplication.java    Ponto de entrada da aplicação
│   │   └── resources/
│   │       ├── application.properties  Configuração Spring
│   │       ├── static/                 Arquivos estáticos (CSS, JS, imagens)
│   │       └── templates/              Templates Thymeleaf
│   └── test/
│       └── java/com/fatec/siga/
│           └── SigaApplicationTests.java
├── mvnw                       Maven wrapper (Unix)
├── mvnw.cmd                   Maven wrapper (Windows)
├── pom.xml                    Dependências e build
└── README.md
```

## Perfis de execução

Para rodar com perfil específico (ex.: `dev`, `prod`):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Configuração do banco PostgreSQL (produção)

Configure as variáveis de ambiente e execute com o perfil `postgresql`:

```powershell
$env:POSTGRES_URL="jdbc:postgresql://localhost:5432/siga"
$env:POSTGRES_USER="siga"
$env:POSTGRES_PASSWORD="siga"
./mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgresql
```

No Linux ou macOS:

```bash
export POSTGRES_URL="jdbc:postgresql://localhost:5432/siga"
export POSTGRES_USER="siga"
export POSTGRES_PASSWORD="siga"
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgresql
```

Em desenvolvimento, o H2 em memória é usado automaticamente sem configuração adicional.

## Deploy no Render com Docker

O projeto inclui um `Dockerfile` com Java 21 e não depende de o Render oferecer Java como runtime.

No Render, crie o serviço web selecionando **Docker** e configure:

- Branch: `main`
- Dockerfile: `Dockerfile`
- `SPRING_PROFILES_ACTIVE=postgresql`
- `JDBC_DATABASE_URL=jdbc:postgresql://HOST:5432/BANCO`
- `POSTGRES_USER=USUARIO`
- `POSTGRES_PASSWORD=SENHA`

## Fluxo de trabalho com Git

1. Sempre trabalhar na sua branch de desenvolvimento ou na `dev`, nunca direto na `main`.
2. Antes de começar: `git pull origin dev` para sincronizar com as últimas alterações.
3. Commits pequenos e com mensagens claras no padrão `Tipo: descrição` (ex.: `Feat: tela de login`, `Fix: validação de CPF`).
4. Ao terminar uma feature, abrir um Pull Request para a `dev` ou `main` conforme combinado.

## Convenção de commits

- `Feat:` nova funcionalidade
- `Fix:` correção de bug
- `Refactor:` refatoração sem mudança de comportamento
- `Docs:` documentação
- `Test:` adição ou ajuste de testes
- `Chore:` tarefas de manutenção (build, dependências, config)

## Troubleshooting

### Porta 8080 já em uso

Altere a porta em `application.properties`:

```properties
server.port=8081
```

### Erro de versão do Java

Verifique que o `JAVA_HOME` aponta para o JDK 21:

```bash
echo $JAVA_HOME   # Linux/macOS
echo %JAVA_HOME%  # Windows
```

### Download de dependências travando

Limpe o cache local e tente novamente:

```bash
./mvnw dependency:purge-local-repository
./mvnw clean install
```
