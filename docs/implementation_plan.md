# Plano de Implementação: Refatoração, Organização e Melhoria da IaC do `api-ticket`

## 🎯 Objetivo
Melhorar a organização de pastas, convenções de nomenclatura (Naming Standards), modularidade, legibilidade e segurança da Infraestrutura como Código (IaC Terraform) baseada em `15SOAT-TechChallenge` e adaptada para o `api-ticket`.

---

## 📐 Diretrizes de Organização & Nomenclatura Refatorada

### 1. Organização dos Arquivos Terraform (`iac/`)

Em vez de arquivos genéricos sem ordenação clara, adotaremos a numeração por ordem de dependência e responsabilidade única:

```
api-ticket/
└── iac/
    ├── Dockerfile-runner                    # Imagem customizada do GitHub Runner local
    ├── providers.tf                         # Definição e trava de versões dos provedores (Kind, K8s, Helm, Docker)
    ├── variables.tf                         # Variáveis de entrada fortemente tipadas com descrições claras
    ├── outputs.tf                           # Saídas de URLs (Grafana, SonarQube, Rancher) e credenciais
    ├── 01-cluster-k8s.tf                    # Provisionamento do Cluster Kind ('ticket-cluster-local') e Namespace 'ticket'
    ├── 02-metrics-server.tf                 # Metrics Server K8s (HPA requirement)
    ├── 03-stack-observability.tf            # Grafana, Loki, Prometheus (OTLP) e Jaeger
    ├── 04-stack-ci-cd.tf                    # GitHub Runner Self-Hosted (DinD) + SonarQube Community + Automação de Token
    ├── 05-apps-core.tf                      # Componentes de infraestrutura da aplicação (Cassandra, Redis, Kafka, Keycloak)
    ├── 06-rancher.tf                        # Painel de gestão visual Rancher
    ├── use-kubeconfig.sh                    # Script auxiliar para exportação do KUBECONFIG
    ├── grafana/                             # Datasources e Dashboards automatizados
    │   └── provisioning/
    │       ├── dashboards/
    │       └── datasources/
    └── prometheus/                          # Configurações do Prometheus
        └── prometheus.yml
```

---

### 2. Tabela de Melhoria de Nomenclatura (Renomeação Padrão)

| Recurso / Item | Nome Original (Legado) | Novo Nome Padronizado (`api-ticket`) | Racional / Melhoria |
| :--- | :--- | :--- | :--- |
| **Cluster Kind** | `cluster-local-dev` / `garage_cluster` | `ticket-cluster-local` | Alinhado ao contexto da aplicação `ticket` |
| **Namespace Kubernetes** | `garage` | `ticket` | Namespace isolado e sem conflito |
| **ServiceAccount Runner** | `github-runner-sa` | `ticket-github-runner-sa` | Prefixado para evitar colisão de nomes RBAC |
| **ClusterRoleBinding** | `github-runner-admin-binding` | `ticket-github-runner-admin-binding` | Nomenclatura explícita de escopo admin |
| **Imagem Docker Runner** | `custom-runner:latest` | `ticket-runner:latest` | Imagem identificada pelo projeto |
| **Deployment Runner** | `github-runner` | `ticket-github-runner` | Nome de deployment descritivo |
| **Labels do Runner** | `local,k8s,kind` | `self-hosted,local,ticket` | Roteamento preciso nos Workflows do GitHub |
| **Repositório Padrão** | `15SOAT-TechChallenge` | `https://github.com/rodsordi/api-ticket` | Apontamento automático para o repo da API |
| **Deployment SonarQube** | `sonarqube` | `ticket-sonarqube` | Recurso isolado no namespace `ticket` |

---

## 🛡️ Melhorias de Segurança e Boas Práticas Terraform

1. **Camada de Variáveis Sensíveis**:
   - `github_pat`, `sonar_admin_password` e `nvd_api_key` marcadas obrigatoriamente com `sensitive = true`.
2. **Ciclo de Vida Limpo dos Segredos**:
   - Geração automática e renovação do token do SonarQube em arquivo `.sonar_token` ignorado pelo Git (`.gitignore`).
3. **Gerenciamento de Dependências (`depends_on`)**:
   - Ordenação estrita garantindo que o Cluster Kind e o Namespace `ticket` estejam 100% prontos antes de provisionar a stack de CI/CD ou Observabilidade.

---

## 📋 Passos de Execução

1. Adicionar `iac/.sonar_token` ao `.gitignore`.
2. Criar a estrutura completa em `iac/` com todos os arquivos refatorados e renomeados.
3. Copiar e estruturar a pasta `iac/grafana/` e `iac/prometheus/`.
4. Validar os arquivos com `terraform fmt` e `terraform validate`.

---

## 🧪 Plano de Verificação

- Executar `terraform validate` dentro da pasta `iac/`.
- Confirmar que todos os 10 arquivos principais e estruturas de suporte foram gerados sem erros de sintaxe.
