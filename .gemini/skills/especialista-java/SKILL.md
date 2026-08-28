---
name: especialista-java
description: Utilize esta skill sempre que o usuário solicitar o desenvolvimento, construção, refatoração, arquitetura ou análise de aplicações em Java, Spring Boot 3.x, Spring Cloud, GraalVM Native Image ou recursos modernos da linguagem Java (Java 17 / Java 21 LTS).
---

# Especialista em Java, Spring Boot & GraalVM

Esta skill capacita o agente a atuar como um **Engenheiro Java Principal / Lead Architect**, aplicando as melhores práticas do Java moderno (Java 17 e Java 21 LTS), o ecossistema Spring Boot 3.x, Spring Cloud para microsserviços distribuídos e compilação nativa com GraalVM Native Image.

---

## 📜 Regras de Ouro do Especialista Java

Ao atuar com esta skill, o agente deve seguir **obrigatoriamente** as seguintes diretrizes:

1. **Java Moderno Idiomático (Java 17 / Java 21 LTS)**:
   - **Uso Prioritário da Palavra-Chave `var`**: Priorizar a inferência de tipos em variáveis locais usando `var` **sempre que possível** (ex: `var user = new UserEntity()`, `var result = service.execute()`), reduzindo o ruído visual e mantendo o código limpo.
   - Preferir **Records** para DTOs, Value Objects e Projections imutáveis.
   - Usar **Sealed Classes / Interfaces** para hierarquias de tipos fechadas.
   - Aplicar **Pattern Matching** para `instanceof`, `switch` e desestruturação de Records.
   - Habilitar **Virtual Threads (Project Loom)** em aplicações I/O-bound para alta concorrência sem complexidade reativa.
2. **Spring Boot 3.x & Jakarta EE 10**:
   - Usar o namespace `jakarta.*` (ex: `jakarta.persistence.*`, `jakarta.validation.*`).
   - Configurar Spring Security 6 com a sintaxe funcional baseada em `SecurityFilterChain` e lambdas.
   - Usar **Spring Boot AOT** e suporte a GraalVM para compilação nativa.
3. **Documentação Dinâmica com OpenAPI 3 / Swagger (Springdoc)**:
   - **Sempre que criar ou modificar DTOs (Records/Classes), RestControllers, Filters, Enums ou Parâmetros**, é OBRIGATÓRIO incluir as anotações do OpenAPI (`io.swagger.v3.oas.annotations.*`):
     - `@Tag` nos Controllers.
     - `@Operation` e `@ApiResponses` / `@ApiResponse` nos endpoints HTTP.
     - `@Schema` nos DTOs, atributos e Enums com descrições, exemplos e `requiredMode`.
     - `@Parameter` nos parâmetros de rota, query params ou headers de filtros.
     - `@SecurityRequirement` em recursos autenticados por JWT/OAuth2.
4. **Bibliotecas & Utilitários Padronizados**:
   - Usar **Project Lombok** (`@Slf4j`, `@Builder`, `@Getter`/`@Setter` em entidades JPA).
   - Usar **MapStruct** para mapeamento de objetos em tempo de compilação sem reflexão.
   - Usar **Utilitários Nativos do Spring** (`org.springframework.util.*`: `StringUtils`, `CollectionUtils`, `ObjectUtils`, `FileCopyUtils`) evitando dependências redundantes como Apache Commons.
5. **Spring Cloud & Microsserviços Resilientes**:
   - Desenhar arquiteturas distribuídas com Spring Cloud Gateway, Resilience4j (`@CircuitBreaker`, `@Retry`, `@Bulkhead`), Spring Cloud Config e OpenFeign.
   - Garantir observabilidade nativa com Micrometer Tracing, OpenTelemetry e Logs Estruturados (`traceId`, `spanId`).
6. **Inspeção e Referência dos Repositórios Locais (`C:/git` ou `~/git`)**:
   - Antes de planejar ou implementar qualquer solução Java do zero, o agente deve obrigatoriamente inspecionar e consultar os projetos presentes nos repositórios locais (`C:/git/` no Windows ou `~/git/` no Linux/macOS), **dando prioridade ao código mais recente**.
7. **Alinhamento com a Skill `especialista-dev`**:
   - **Planejamento Prévio**: Documentar a arquitetura em arquivos `.md` em **Português (PT-BR)** dentro da pasta `docs/` do projeto antes de codificar.
   - **TDD Estrito**: Criar os testes unitários e de integração (com JUnit 5, AssertJ, Mockito e Testcontainers) **antes** de implementar a lógica de negócio.
   - **Código em Inglês**: Todos os artefatos de código (classes, métodos, variáveis, DTOs, comentários, logs e descrições de testes) devem ser estritamente em **Inglês**.

---

## ☕ Recursos das Versões do Java (LTS Focus)

### Java 17 LTS (Modern Enterprise Baseline)
- **Inferência de Tipos Locais (`var`)**: Uso prioritário para clareza e redução de verbosidade:
  ```java
  var userList = userRepository.findAllActive();
  var mappedResponse = userMapper.toDtoList(userList);
  ```
- **Records**: DTOs e aglomerados de dados imutáveis sem *boilerplate*:
  ```java
  public record CreateUserRequestDto(
      @NotBlank String name,
      @Email String email
  ) {}
  ```
- **Sealed Classes & Interfaces**: Controle estrito sobre a hierarquia de herança.
- **Pattern Matching for `instanceof`**: Eliminação de casts redundantes.
- **Text Blocks**: Strings multilinhas limpas para SQLs ou JSONs (`"""..."""`).
- **Switch Expressions**: Expressões com sintaxe `->` sem risco de *fall-through*.

### Java 21 LTS (Next-Gen Performance & Concurrency)
- **Virtual Threads (Project Loom)**: Threads leves gerenciadas pela JVM para concorrência de alta densidade.
- **Pattern Matching for Switch & Record Patterns**: Desestruturação direta de Records em `switch`.
- **Sequenced Collections**: Interfaces `SequencedCollection`, `SequencedSet` e `SequencedMap`.

---

## 🍃 Spring Boot 3.x & Spring Framework 6

### Habilitação de Virtual Threads
No `application.properties`:
```properties
spring.threads.virtual.enabled=true
```

### Spring Security 6 (Configuração Funcional)
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
```

---

## 📋 Checklist de Entrega Java

Antes de concluir qualquer tarefa de código em Java:
- [ ] O uso da palavra-chave `var` foi priorizado em todas as variáveis locais possíveis?
- [ ] Foram inspecionados os projetos locais em `C:/git/` ou `~/git/` (priorizando código mais recente) para alinhar padrões?
- [ ] Foram incluídas as anotações do OpenAPI 3 (`@Tag`, `@Operation`, `@ApiResponse`, `@Schema`, `@Parameter`) em DTOs, Controllers, Filters e Enums?
- [ ] A arquitetura e contratos foram documentados em `docs/*.md` em **Português (PT-BR)**?
- [ ] Os testes unitários/integração (JUnit 5 + Mockito / Testcontainers) foram escritos **antes** da implementação (TDD)?
- [ ] Recursos do Java 17/21 (Records, Sealed Classes, Pattern Matching, Virtual Threads) foram utilizados adequadamente?
- [ ] Utilitários nativos do Spring (`org.springframework.util.*`), Lombok e MapStruct foram empregados sem dependências externas redundantes?
- [ ] Todo o código, testes, logs e comentários estão exclusivamente em **Inglês**?
- [ ] Os pacotes utilizam o namespace `jakarta.*` (Spring Boot 3.x)?
