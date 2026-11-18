# SIGA

Sistema Integrado de Gestão Acadêmica desenvolvido como projeto da FATEC em Spring Boot.

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
git checkout Henrique
```

Substitua `Henrique` pelo nome da sua branch. Para listar todas:

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
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Usuário: `sa`
  - Senha: (deixar em branco)
- **Swagger UI** (documentação da API): http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/v3/api-docs

## Build

Para gerar o JAR executável:

```bash
./mvnw clean package
```

O artefato final fica em `target/siga-0.0.1-SNAPSHOT.jar`. Para rodar o JAR:

```bash
java -jar target/siga-0.0.1-SNAPSHOT.jar
```

## Testes

```bash
./mvnw test
```

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

Adicione as seguintes propriedades em `application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/siga
spring.datasource.username=<usuario>
spring.datasource.password=<senha>
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

Em desenvolvimento, o H2 em memória é usado automaticamente sem configuração adicional.

## Fluxo de trabalho com Git

1. Sempre trabalhar na sua branch (`Henrique`, `Fulano`, etc.), nunca direto na `main`.
2. Antes de começar: `git pull origin main` para sincronizar com as últimas alterações.
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

## Licença

Projeto acadêmico desenvolvido na FATEC.
