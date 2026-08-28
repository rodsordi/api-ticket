#!/usr/bin/env bash
# =================================================================
# Script de Implantação da Infraestrutura Local (IaC) - Linux / WSL2
# =================================================================
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}=================================================================${NC}"
echo -e "${CYAN}     🚀 api-ticket - Implantação de Infraestrutura Local (IaC)    ${NC}"
echo -e "${CYAN}=================================================================${NC}"

# 1. Checagem de ferramentas pré-instaladas
TOOLS=("docker" "terraform" "kubectl" "helm")
for tool in "${TOOLS[@]}"; do
    if ! command -v "$tool" &> /dev/null; then
        echo -e "${RED}❌ Erro: A ferramenta '$tool' não foi encontrada no PATH.${NC}"
        echo -e "${YELLOW}Por favor, consulte o guia de instalação em iac/README.md.${NC}"
        exit 1
    fi
done
echo -e "${GREEN}✅ Todas as ferramentas foram encontradas (${TOOLS[*]}).${NC}"

# 2. Verificação do Docker Daemon
if ! docker info &> /dev/null; then
    echo -e "${RED}❌ Erro: O Docker Daemon não está rodando ou o usuário não tem permissão.${NC}"
    echo -e "${YELLOW}Dica: Execute 'sudo systemctl start docker' ou adicione seu usuário ao grupo docker ('sudo usermod -aG docker $USER').${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Docker Daemon ativo.${NC}"

# 3. Verificação do Token do GitHub
if [ -z "${TF_VAR_github_pat:-}" ] || [ "${TF_VAR_github_pat}" = "ghp_mock_token_change_me_or_set_via_tf_vars" ]; then
    echo -e "${YELLOW}⚠️ Variável TF_VAR_github_pat não definida.${NC}"
    read -sp "Insira seu GitHub Personal Access Token (PAT): " USER_TOKEN
    echo ""
    if [ -z "$USER_TOKEN" ]; then
        echo -e "${RED}❌ Erro: Token do GitHub é obrigatório.${NC}"
        exit 1
    fi
    export TF_VAR_github_pat="$USER_TOKEN"
fi
echo -e "${GREEN}✅ Token do GitHub configurado.${NC}"

# 4. Execução do Terraform
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../terraform/envs/local"

echo -e "\n${CYAN}📦 Inicializando o Terraform no ambiente local...${NC}"
terraform init

echo -e "\n${CYAN}🔨 Aplicando a infraestrutura no Kubernetes/Docker...${NC}"
terraform apply -auto-approve

echo -e "\n${GREEN}🎉 Infraestrutura provisionada com sucesso!${NC}"
echo -e "${CYAN}=================================================================${NC}"
echo -e " 📊 Grafana Dashboard:   http://localhost:3000"
echo -e " 🔍 SonarQube:           http://localhost:30900"
echo -e " 🐮 Rancher Manager:     https://localhost:30443"
echo -e "${CYAN}=================================================================${NC}"
