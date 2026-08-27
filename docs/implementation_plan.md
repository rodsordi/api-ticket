# Plano de Arquitetura: Avaliação & Planejamento da Organização de Variáveis, Pastas e Componentes da IaC (`api-ticket`)

## 🎯 Objetivo
Apresentar uma avaliação arquitetural abrangente e um **plano detalhado de evolução** para o Terraform em `iac/`, focando na **organização das pastas**, **reestruturação dos componentes** e **governança/tipagem avançada de variáveis**, sem executar nenhuma alteração no momento.

---

## 🔍 1. Avaliação & Melhoria na Organização das Variáveis

### ⚠️ Situação Atual:
As variáveis são declaradas individualmente como tipos primitivos (`string`), com poucos blocos de validação de formato e sem agrupamento lógico por domínio.

### 💡 Proposta de Evolução das Variáveis:

#### A) Agrupamento em Objetos Tipados (`type = object(...)`)
Em vez de declarar 10+ variáveis dispersas (`github_repo_url`, `github_pat`, `sonar_admin_password`, `sonar_data_host_path`, `nvd_api_key`), agrupar por domínio funcional:

```hcl
# iac/terraform/envs/local/variables.tf

variable "github_config" {
  type = object({
    repo_url = string
    pat      = string
    labels   = list(string)
  })
  sensitive   = true
  description = "Configurações de integração e autenticação com o GitHub Actions"
}

variable "sonar_config" {
  type = object({
    admin_password = string
    host_data_path = string
  })
  sensitive   = true
  description = "Configurações do SonarQube Community"
}

variable "cluster_config" {
  type = object({
    name      = string
    namespace = string
    k8s_ver   = string
  })
  default = {
    name      = "ticket-cluster-local"
    namespace = "ticket"
    k8s_ver   = "v1.28.15"
  }
  description = "Parâmetros do cluster Kubernetes Kind"
}
```

#### B) Governança com Bloco `validation {}`
Adicionar regras formais de validação no Terraform para impedir a execução com variáveis inválidas ou malformatadas:

```hcl
variable "github_repo_url" {
  type        = string
  default     = "https://github.com/rodsordi/api-ticket"
  description = "URL do repositório GitHub para registro do Runner"

  validation {
    condition     = can(regex("^https://github\\.com/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$", var.github_repo_url))
    error_message = "A variável github_repo_url deve ser uma URL HTTPS válida do GitHub (ex: https://github.com/usuario/repositorio)."
  }
}

variable "sonar_admin_password" {
  type        = string
  sensitive   = true
  default     = "Sonarqube@2026"
  description = "Senha do administrador do SonarQube"

  validation {
    condition     = length(var.sonar_admin_password) >= 8
    error_message = "A senha do SonarQube deve conter no mínimo 8 caracteres."
  }
}
```

---

## 🏗️ 2. Avaliação & Melhoria no Agrupamento de Componentes (SRP)

### ⚠️ Situação Atual:
O módulo `services` combina em um único arquivo de mais de 300 linhas serviços de responsabilidades totalmente distintas: banco de dados Cassandra, cache Redis, mensageria Kafka, identity provider Keycloak e gerenciador Rancher.

### 💡 Proposta de Desacoplamento por Responsabilidade Única (SRP):

Dividir o módulo monolítico `services` em módulos especializados:
- **`modules/persistence/`**: Responsável exclusivamente por Cassandra e Redis.
- **`modules/messaging/`**: Responsável exclusivamente pelo broker Apache Kafka (KRaft).
- **`modules/identity/`**: Responsável pelo Keycloak IAM.
- **`modules/management/`**: Responsável pelo Rancher Manager UI.

#### 🟢 Benefício:
Permite que o desenvolvedor ligue ou desligue apenas o Kafka ou apenas o Keycloak alterando uma flag `enabled = true/false` no módulo root sem impactar o banco de dados.

---

## 📂 3. Avaliação & Melhoria na Estrutura de Pastas

### 💡 Proposta de Hierarquia Definitiva (`iac/`):

```
api-ticket/
└── iac/
    ├── README.md                          # Guia visual de onboarding e arquitetura
    ├── Dockerfile-runner                  # Imagem Docker do GitHub Runner
    ├── use-kubeconfig.sh                  # Script utilitário KUBECONFIG
    ├── scripts/                           # Scripts automatizados executáveis
    │   ├── deploy-iac.ps1                 # PowerShell (Windows)
    │   ├── deploy-iac.sh                  # Bash (Linux/WSL2)
    │   ├── destroy-iac.ps1
    │   └── destroy-iac.sh
    └── terraform/                         # Código Terraform isolado
        ├── envs/                          # Camada de ambientes
        │   └── local/                     # Ambiente Local Dev (Kind/Docker)
        │       ├── main.tf                # Instanciação declarativa dos módulos
        │       ├── variables.tf           # Declaração das variáveis com validação
        │       ├── outputs.tf             # Saídas consolidadas
        │       ├── providers.tf           # Provider locks
        │       └── terraform.tfvars.example
        └── modules/                       # Módulos desacoplados reutilizáveis
            ├── k8s_cluster/               # Kind Cluster + Local Registry + Namespace
            ├── ci_cd/                     # GitHub Runner + SonarQube Community
            ├── observability/             # Stack LGTM (Grafana, Loki, Prometheus, Jaeger)
            ├── persistence/               # Cassandra + Redis
            ├── messaging/                 # Apache Kafka
            └── identity/                  # Keycloak IAM
```

---

## 📋 Resumo Comparativo das Melhorias Planejadas

| Aspeto | Estrutura Atual | Estrutura Proposta no Plano |
| :--- | :--- | :--- |
| **Tipagem de Variáveis** | Variáveis primitivas soltas | Objetos tipados (`type = object(...)`) + `validation {}` |
| **Arquivos `.tf`** | Na raiz da pasta `iac/` | Isolados dentro de `iac/terraform/envs/local/` |
| **Módulos de Serviços** | Módulo `services` monolítico | Módulos especializados (`persistence`, `messaging`, `identity`) |
| **Multi-ambiente** | Suporta apenas local | Preparado para adicionar `envs/staging` e `envs/prod` |

---

> ℹ️ **Nota**: Nenhuma alteração de código ou comando foi executada. Este plano aguarda sua análise e aprovação.
