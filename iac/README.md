# 🏗️ Infraestrutura como Código (IaC) - `api-ticket`

Este repositório contém a automação completa em **Terraform** para provisionamento e execução da infraestrutura local do projeto **`api-ticket`**.

A solução sobe localmente no Docker/Kubernetes um ecossistema pronto para desenvolvimento e esteira CI/CD contendo:
- **Cluster Kubernetes Local**: Kind (`ticket-cluster-local`) com Docker Registry local (`kind-registry:5000`).
- **GitHub Self-Hosted Runner**: Container Docker com Runner oficial, JDK 25, Maven, Helm, Kubectl e Docker-in-Docker (DinD).
- **Qualidade & Segurança**: SonarQube Community com automação de token via API e suporte a OWASP Dependency-Check.
- **Stack de Observabilidade**: Grafana (dashboards pré-configurados), Loki, Prometheus (com ingestão OTLP) e Jaeger.
- **Gestão Kubernetes**: Rancher Manager UI.
- **Bancos e Mensageria**: Cassandra 5.0, Redis 7, Kafka (KRaft) e Keycloak IAM.

---

## 📋 Pré-requisitos Globais

Antes de iniciar, certifique-se de que sua máquina possui instalados:
1. **Docker Desktop** (Windows/macOS) ou **Docker Engine** (Linux).
2. **Git** (`git --version`).
3. **Kind** (`kind --version`).
4. **Kubectl** (`kubectl version --client`).
5. **Helm** (`helm version`).
6. **Terraform** (`terraform --version` $\ge$ 1.5.0).

---

## 🛠️ Guia de Instalação do Terraform

### 🟦 Windows

#### Opção A: Via Chocolatey (Recomendado)
```powershell
choco install terraform
```

#### Opção B: Via Scoop
```powershell
scoop bucket add main
scoop install terraform
```

#### Opção C: Instalação Manual (Binário `.exe`)
1. Baixe o pacote zip do Terraform oficial para Windows x64 em: https://developer.hashicorp.com/terraform/install
2. Extraia o arquivo `terraform.exe` para uma pasta (ex: `C:\tools\terraform`).
3. Adicione `C:\tools\terraform` às Variáveis de Ambiente do Sistema (`Path`):
   - Abra o menu Iniciar $\rightarrow$ Digite *"Editar as variáveis de ambiente do sistema"*.
   - Clique em **Variáveis de Ambiente...** $\rightarrow$ Em **Variáveis do sistema**, selecione `Path` e clique em **Editar**.
   - Clique em **Novo** e cole `C:\tools\terraform`.
4. Abra um novo terminal do PowerShell e valide com: `terraform --version`.

---

### 🐧 Linux (Ubuntu / Debian / CentOS / Alpine / Arch)

#### Opção A: Ubuntu / Debian (Repositório Oficial HashiCorp)
```bash
sudo apt-get update && sudo apt-get install -y gnupg software-properties-common curl
wget -O- https://apt.releases.hashicorp.com/gpg | gpg --dearmor | sudo tee /usr/share/keyrings/hashicorp-archive-keyring.gpg > /dev/null
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
sudo apt-get update && sudo apt-get install terraform
```

#### Opção B: CentOS / RHEL / Fedora
```bash
sudo dnf install -y yum-utils
sudo yum-config-manager --add-repo https.rpm.releases.hashicorp.com/RHEL/hashicorp.repo
sudo dnf install terraform
```

#### Opção C: Arch Linux
```bash
sudo pacman -S terraform
```

#### Opção D: Instalação Manual via Binário Linux (Qualquer Distribuição)
```bash
TERRAFORM_VERSION="1.9.5"
wget "https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip"
unzip "terraform_${TERRAFORM_VERSION}_linux_amd64.zip"
sudo mv terraform /usr/local/bin/
rm "terraform_${TERRAFORM_VERSION}_linux_amd64.zip"
terraform --version
```

---

## 🔑 Configuração de Variáveis de Ambiente

O Terraform lê automaticamente variáveis de ambiente do sistema operacional prefixadas com `TF_VAR_`:

### 🪟 Windows (PowerShell)
```powershell
# Obrigatório: Seu Personal Access Token (PAT) do GitHub com escopos 'repo' e 'admin:org'
$env:TF_VAR_github_pat="ghp_seu_token_real_aqui"

# Opcionais (possuem valores padrão pré-configurados)
$env:TF_VAR_sonar_admin_password="Sonarqube@2026"
$env:TF_VAR_NVD_API_KEY="sua_chave_nvd_opcional"
```

### 🐧 Linux / WSL2 (Bash)
```bash
# Adicione ao seu ~/.bashrc ou exporte na sessão atual:
export TF_VAR_github_pat="ghp_seu_token_real_aqui"
export TF_VAR_sonar_admin_password="Sonarqube@2026"
export TF_VAR_NVD_API_KEY="sua_chave_nvd_opcional"
```

---

## 🚀 Passo a Passo de Implantação (Onboarding)

### Método 1: Via Scripts Automatizados (Mais Rápido)

#### 🪟 No Windows (PowerShell):
```powershell
cd iac
.\scripts\deploy-iac.ps1
```

#### 🐧 No Linux / WSL2 (Bash):
```bash
cd iac
chmod +x scripts/deploy-iac.sh
./scripts/deploy-iac.sh
```

---

### Método 2: Execução Manual via CLI do Terraform

1. Acesse o diretório `iac/terraform/envs/local/`:
   ```bash
   cd iac/terraform/envs/local
   ```

2. Inicialize o Terraform:
   ```bash
   terraform init
   ```

3. Valide os arquivos de configuração:
   ```bash
   terraform validate
   ```

4. Aplique a infraestrutura:
   ```bash
   terraform apply -auto-approve
   ```

5. Exporte a configuração do Kubernetes (`KUBECONFIG`):
   ```bash
   # No Linux/WSL:
   source use-kubeconfig.sh

   # No Windows (PowerShell):
   $env:KUBECONFIG = (kind get kubeconfig-path --name "ticket-cluster-local")
   kubectl get nodes
   ```

---

## 🌐 Tabela de Serviços & Endpoints Expostos

Após a execução, os seguintes painéis e serviços estarão acessíveis na sua máquina local:

| Serviço | Porta / Endpoint (Host Local) | Credenciais / Notas | Descrição |
| :--- | :--- | :--- | :--- |
| **`api-ticket`** | `http://localhost:8080` | N/A | API principal de Ingressos |
| **`keycloak`** | `http://localhost:8081` | `admin` / `admin` | Provider OAuth2 / Identity |
| **Grafana** | `http://localhost:30030` | Anonymous (Admin) | Painel com Dashboards de Observabilidade |
| **SonarQube** | `http://localhost:30900` | `admin` / `Sonarqube@2026` | Análise estática de código |
| **Rancher UI** | `https://localhost:30443` | `admin` / `admin` | Gestor visual do Cluster Kubernetes |
| **Cassandra** | `localhost:9042` | N/A | Banco de dados CQL |
| **Kafka** | `localhost:9092` | N/A | Broker de Mensageria |
| **Redis** | `localhost:6379` | N/A | Cache em memória |
| **Prometheus** | `http://localhost:9090` | N/A | Métricas |
| **Loki** | `http://localhost:3100` | N/A | Agregador de Logs |
| **Jaeger** | `http://localhost:16686` | N/A | Tracing Distribuído |
| **Kind Registry** | `http://localhost:5001` | N/A | Registro Docker local |

---

## 🤖 Como Funciona o GitHub Self-Hosted Runner

1. O Terraform compila a imagem customizada [`Dockerfile-runner`](file:///c:/git/interviews/api-ticket/iac/Dockerfile-runner) e importa para o cluster Kind.
2. É criado um Deployment Kubernetes `ticket-github-runner` com 2 réplicas rodando um sidecar Docker-in-Docker (`dind`).
3. O Runner registra-se automaticamente no repositório `https://github.com/rodsordi/api-ticket` com as labels `self-hosted,local,ticket`.
4. Ao fazer qualquer `git push` nas branches `develop` ou `main`, os Workflows em `.github/workflows/` são capturados pelo runner local e executados na sua própria máquina.

---

## 🧹 Como Destruir a Infraestrutura Local

Quando desejar encerrar os containers e liberar recursos da máquina:

#### Windows (PowerShell):
```powershell
cd iac
.\scripts\destroy-iac.ps1
```

#### Linux / WSL2 (Bash):
```bash
cd iac
./scripts/destroy-iac.sh
```

---

## ❓ Solução de Problemas (Troubleshooting)

### 1. `Error: Docker daemon is not running`
- **Solução**: Certifique-se de que o Docker Desktop ou serviço Docker está ativo. No Linux, verifique as permissões do socket rodando `sudo usermod -aG docker $USER` e reinicie a sessão.

### 2. `Runner Fica Offline ou Não Registra no GitHub`
- **Causa**: O token `TF_VAR_github_pat` expirou ou não possui as permissões necessárias.
- **Solução**: Crie um novo Personal Access Token (Classic) com permissão `repo` e atualize a variável `$env:TF_VAR_github_pat`. Em seguida, execute `terraform apply` novamente.

### 3. `Erro de Conflito de Portas (ex: 80, 443, 30000)`
- **Causa**: Outra aplicação (ex: IIS, NGINX local ou outro container) está usando as portas reservadas.
- **Solução**: Encerre o processo conflitante antes de rodar o `terraform apply`.
