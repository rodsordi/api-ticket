# 🏗️ Infraestrutura como Código (IaC) - `api-ticket`

Este diretório contém a automação completa em **Terraform** para provisionamento e execução da infraestrutura e ecossistema local do projeto **`api-ticket`** sobre **Kubernetes (Kind)**.

---

## 🧩 O que é Provisionado

A automação do Terraform sobe um ecossistema completo pronto para desenvolvimento, observabilidade e integração contínua (CI/CD):

- **Cluster Kubernetes (Kind)**: Cluster multi-nó (`ticket-cluster-local`) com registry local (`kind-registry:5000`).
- **Aplicação Principal (`api-ticket`)**: Deployment Kubernetes com 5 réplicas e suporte a autoscaling (HPA).
- **Segurança & Identidade**: Keycloak IAM com importação automática do Realm `ticket`.
- **Qualidade de Código**: SonarQube Community com geração automática de token de API.
- **GitHub Self-Hosted Runner**: Container Docker rodando Runner oficial com JDK 25, Maven, Helm, Kubectl e DinD.
- **Stack de Observabilidade**: Grafana (com 11 dashboards pré-carregados), Loki, Prometheus e Jaeger.
- **Bancos de Dados & Mensageria**: Cassandra 5.0, Redis 7 e Apache Kafka (KRaft).
- **Gestão Kubernetes**: Rancher Manager UI.

---

## 📋 Pré-requisitos Globais

Antes de iniciar o provisionamento, certifique-se de instalar as ferramentas necessárias:

1. **Docker Desktop** (Windows/macOS) ou **Docker Engine** (Linux).
2. **Git** (`git --version`).
3. **Kind** (`kind --version`).
4. **Kubectl** (`kubectl version --client`).
5. **Helm 3.x** (`helm version`).
6. **Terraform CLI** ($\ge$ 1.5.0).

---

## 🛠️ Passo a Passo: Construção do Ambiente IaC

### Passo 1: Instalação do Terraform (Caso não possua instalado)

#### 🟦 Windows (PowerShell):
```powershell
# Via Chocolatey (Recomendado)
choco install terraform

# Ou via Scoop
scoop install terraform
```

#### 🐧 Linux (Ubuntu / Debian):
```bash
sudo apt-get update && sudo apt-get install -y gnupg software-properties-common curl
wget -O- https://apt.releases.hashicorp.com/gpg | gpg --dearmor | sudo tee /usr/share/keyrings/hashicorp-archive-keyring.gpg > /dev/null
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
sudo apt-get update && sudo apt-get install terraform
```

---

### Passo 2: Configuração de Variáveis de Ambiente

O Terraform necessita de um Personal Access Token (PAT) do GitHub para registrar o Runner Self-Hosted no seu repositório:

#### 🪟 Windows (PowerShell):
```powershell
# Obrigatório: Seu Token PAT do GitHub (escopos: 'repo' e 'admin:org')
$env:TF_VAR_github_pat="ghp_seu_token_real_aqui"

# Opcional (Senha do SonarQube)
$env:TF_VAR_sonar_admin_password="Sonarqube@2026"
```

#### 🐧 Linux / macOS / WSL (Bash):
```bash
export TF_VAR_github_pat="ghp_seu_token_real_aqui"
export TF_VAR_sonar_admin_password="Sonarqube@2026"
```

---

### Passo 3: Execução do Provisionamento (Onboarding)

Você pode provisionar via **Script Automatizado** (recomendado) ou **Manualmente via CLI**:

#### 🚀 Método A: Via Scripts Automatizados (Mais Rápido)

Acesse a pasta `iac/` e execute o script correspondente ao seu sistema operacional:

- **Windows (PowerShell):**
  ```powershell
  cd iac
  .\scripts\deploy-iac.ps1
  ```

- **Linux / macOS / WSL (Bash):**
  ```bash
  cd iac
  chmod +x scripts/deploy-iac.sh
  ./scripts/deploy-iac.sh
  ```

---

#### 🛠️ Método B: Execução Manual via CLI do Terraform

1. Acesse a pasta do ambiente local:
   ```bash
   cd iac/terraform/envs/local
   ```

2. Inicialize os provedores e módulos:
   ```bash
   terraform init
   ```

3. Valide o código HCL:
   ```bash
   terraform validate
   ```

4. Aplique a infraestrutura:
   ```bash
   terraform apply -auto-approve
   ```

5. Exporte a configuração do Kubernetes (`KUBECONFIG`):
   ```bash
   # Windows (PowerShell):
   $env:KUBECONFIG = (kind get kubeconfig-path --name "ticket-cluster-local")

   # Linux/macOS:
   export KUBECONFIG="$(kind get kubeconfig-path --name ticket-cluster-local)"

   # Testar acesso ao cluster:
   kubectl get nodes
   kubectl get pods -n ticket
   ```

---

## 🌐 Tabela de Serviços & Endpoints Expostos

Após a conclusão da implantação, os seguintes serviços estarão acessíveis no seu host local:

| Serviço | Porta / Endpoint (Host Local) | Credenciais Padronizadas | Descrição |
| :--- | :--- | :--- | :--- |
| **`api-ticket`** | `http://localhost:8080` | N/A | API Principal de Ingressos |
| **Keycloak IAM** | `http://localhost:8081` | `admin` / `admin` | Provider OAuth2 / Identity |
| **Grafana** | `http://localhost:3000` | Login Anônimo (Admin) | Painel com 11 Dashboards de Observabilidade |
| **SonarQube** | `http://localhost:30900` | `admin` / `Sonarqube@2026` | Análise estática de código |
| **Rancher Manager**| `https://localhost:30443` | `admin` / `admin` | Gestor visual do Cluster Kubernetes |
| **Prometheus** | `http://localhost:9090` | N/A | Coletor de Métricas |
| **Loki Logs** | `http://localhost:3100` | N/A | Agregador de Logs |
| **Jaeger UI** | `http://localhost:16686` | N/A | Tracing Distribuído |
| **Cassandra** | `localhost:9042` | N/A | Banco de dados CQL |
| **Kafka Broker** | `localhost:9092` | N/A | Mensageria (KRaft) |
| **Redis Cache** | `localhost:6379` | N/A | Cache em memória |
| **Kind Registry** | `http://localhost:5001` | N/A | Registro de imagens Docker local |

---

## ⏸️ Como Gerenciar as Instâncias sem Destruir a Infraestrutura

Para economizar CPU/Memória da sua máquina local **sem precisar rodar `terraform destroy`**:

### Opção 1: Pausar/Desativar apenas as Pods (Manter o Cluster Intacto)
Você pode zerar o número de réplicas de todas as aplicações no Kubernetes:

```bash
# Desativar (escalar para 0 réplicas):
kubectl scale deployment --all --replicas=0 -n ticket

# Para reativar todas as aplicações mais tarde:
kubectl scale deployment --all --replicas=1 -n ticket
```

### Opção 2: Pausar os Containers Docker do Kind
```bash
# Pausar os nó do cluster Kind no Docker:
docker stop $(docker ps -q -f name=ticket)

# Reativar o cluster no ponto em que parou:
docker start $(docker ps -a -q -f name=ticket)
```

---

## 🧹 Como Destruir Toda a Infraestrutura

Se você desejar apagar completamente o cluster Kind e liberar todo o espaço:

#### Windows (PowerShell):
```powershell
cd iac
.\scripts\destroy-iac.ps1
```

#### Linux / macOS (Bash):
```bash
cd iac
./scripts/destroy-iac.sh
```

---

## ❓ Solução de Problemas (Troubleshooting)

### 1. `Error: Docker daemon is not running`
- **Causa**: O serviço Docker Desktop não está iniciado.
- **Solução**: Abra o Docker Desktop e aguarde a inicialização completa.

### 2. `Erro de Conflito de Portas (ex: 8080, 8081, 3000)`
- **Causa**: O ambiente Docker Compose (ou outra aplicação) está rodando e ocupando as mesmas portas no host.
- **Solução**: Execute `docker compose down` no diretório raiz do projeto antes de subir o Terraform/Kind.

### 3. `Runner Fica Offline ou Não Registra no GitHub`
- **Causa**: O token `$env:TF_VAR_github_pat` expirou ou não possui as permissões `repo` e `admin:org`.
- **Solução**: Gere um novo Personal Access Token (Classic) no GitHub e execute `terraform apply`.
