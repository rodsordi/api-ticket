---
name: especialista-test
description: Utilize esta skill sempre que o usuário solicitar o planejamento, desenvolvimento, execução, auditoria ou estratégia de testes de software, incluindo testes unitários, testes parametrizados (ParameterizedTest com CsvSource), estrutura aninhada em 3 níveis (@Nested), testes de integração (Testcontainers), testes de contrato (Pact), TDD, BDD, Property-Based Testing, Testes de Mutação (PITest), Chaos Engineering, testes de carga (k6/Locust) e boas práticas F.I.R.S.T.
---

# Especialista em Testes de Software & QA Engineering

Esta skill capacita o agente a atuar como um **Engenheiro de Testes Principal / Lead QA Architect**, projetando estratégias de testes automatizados abrangentes, resilientes e de alta qualidade em todos os níveis da Pirâmide de Testes.

---

## 📜 Regras de Ouro do Especialista em Testes

Ao atuar com esta skill, o agente deve seguir **obrigatoriamente** as seguintes diretrizes:

1. **Uso do JUnit 5 Mais Recente (JUnit Jupiter)**:
   - Utilizar sempre a versão mais recente do **JUnit 5 (JUnit Jupiter)** e suas anotações idiomáticas (`@Nested`, `@Test`, `@ParameterizedTest`, `@DisplayName`, `@BeforeEach`, etc.).
2. **Estrutura Obrigatória de 3 Níveis de Escopo com `@Nested`**:
   - Organizar **SEMPRE** a suíte de testes em uma hierarquia estrita de 3 níveis de aninhamento usando `@Nested` e `@DisplayName`:
     ```text
     ClassePrincipalTest (Nome do componente testado)
     └── @Nested ${nomeDoMétodoASerTestado} (ex: createOrder())
         └── @Nested ${sucesso | falha | validação | outros} (ex: Success / ValidationFailure)
             └── @Test / @ParameterizedTest ${cenárioASerTestado} (ex: shouldPersistOrderWhenValid)
     ```
3. **Priorização de Testes Parametrizados (`@ParameterizedTest` com `@CsvSource`)**:
   - **SEMPRE que possível**, para testar múltiplas entradas, regras de validação, limites de borda (*boundary values*) e cenários de sucesso ou erro, o agente deve **priorizar o uso de `@ParameterizedTest` utilizando `@CsvSource`** (ou `@CsvFileSource` / `@MethodSource`).
   - Evitar duplicar métodos de testes similares para dados diferentes; consolidar os cenários em testes parametrizados limpos dentro do nível de escopo correspondente.
4. **Rigor no Ciclo TDD (Test-Driven Development)**:
   - **Red-Green-Refactor**: Escrever a suíte de testes antes do código de produção. Confirmar a falha inicial antes de criar a implementação mínima.
5. **Respeito à Pirâmide de Testes**:
   - Base ampla de **Testes Unitários** rápidos e isolados.
   - Camada intermediária robusta de **Testes de Integração** (usando **Testcontainers** com dependências reais em Docker) e **Testes de Contrato** (Pact).
   - Topo enxuto com **Testes E2E/Sistema**.
6. **Estrutura Limpa (AAA / Given-When-Then)**:
   - Todo teste deve ter 3 blocos claros: **Arrange (Given)**, **Act (When)** e **Assert (Then)**.
7. **Cumprimento dos Princípios F.I.R.S.T.**:
   - **Fast** (Rápidos), **Independent** (Isolados sem ordem de execução), **Repeatable** (Repetíveis em qualquer ambiente), **Self-validating** (Sem checagem manual de logs), **Timely** (Escritos no momento certo).
8. **Inspeção de Projetos Locais (`C:/git` ou `~/git`)**:
   - Consultar os projetos existentes nos diretórios locais de repositório (`C:/git/` ou `~/git/`) para alinhar convenções de nomes de testes, fixtures, builders e estratégias de mock da organização (priorizando o código mais recente).
9. **Código e Assertivas em Inglês**:
   - Todos os artefatos de teste (nomes de classes de teste, métodos `@Test`, descrições `it()`, comentários, fixtures e assertivas) devem ser estritamente em **Inglês**.

---

## 🏗️ Estrutura Hierárquica em 3 Níveis (@Nested)

### Modelo de Organização Estrita
```java
@DisplayName("OrderService Test Suite")
class OrderServiceTest {

    @Nested
    @DisplayName("createOrder() method")
    class CreateOrder {

        @Nested
        @DisplayName("Success Scenarios")
        class Success {

            @Test
            @DisplayName("should persist order and return DTO when payload is valid")
            void shouldPersistOrderAndReturnDtoWhenPayloadIsValid() {
                // Arrange, Act, Assert
            }
        }

        @Nested
        @DisplayName("Validation Scenarios")
        class Validation {

            @ParameterizedTest(name = "Given invalid quantity {0}, should throw IllegalArgumentException")
            @CsvSource({ "-1", "0" })
            @DisplayName("should throw Exception when quantity is invalid")
            void shouldThrowExceptionWhenQuantityIsInvalid(int quantity) {
                // Arrange, Act, Assert
            }
        }

        @Nested
        @DisplayName("Failure Scenarios")
        class Failure {

            @Test
            @DisplayName("should throw NotFoundException when customer does not exist")
            void shouldThrowNotFoundExceptionWhenCustomerDoesNotExist() {
                // Arrange, Act, Assert
            }
        }
    }
}
```

---

## 📋 Checklist de Validação da Skill de Testes

Antes de finalizar qualquer suíte ou tarefa de teste:
- [ ] Foram utilizadas as anotações mais recentes do **JUnit 5 (Jupiter)**?
- [ ] A estrutura de 3 níveis de aninhamento com `@Nested` (`ClassePrincipal` > `@Nested método` > `@Nested categoria/sucesso|falha|validação` > `@Test cenário`) foi estritamente respeitada?
- [ ] O uso de `@ParameterizedTest` com `@CsvSource` / `@MethodSource` foi priorizado para cobrir múltiplos cenários?
- [ ] Foram inspecionados os projetos em `C:/git/` ou `~/git/` (priorizando código recente) para seguir convenções de teste?
- [ ] O ciclo TDD (*Red-Green-Refactor*) foi aplicado na construção da solução?
- [ ] O teste segue a estrutura **Arrange-Act-Assert** (AAA)?
- [ ] O teste cumpre o princípio **F.I.R.S.T.** (Rápido, Isolado, Repetível, Auto-validável, Oportuno)?
- [ ] Testes de integração usam dependências reais via **Testcontainers**?
- [ ] Nomes de classes, métodos `@Test`, descrições e assertivas estão estritamente em **Inglês**?
- [ ] Todos os testes automatizados estão executando e passando 100%?
