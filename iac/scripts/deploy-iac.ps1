<#
.SYNOPSIS
    Script de Implantação da Infraestrutura Local (IaC) para api-ticket no Windows (PowerShell).
.DESCRIPTION
    Verifica pré-requisitos, valida variáveis de ambiente (TF_VAR_github_pat) e executa o Terraform em iac/terraform/envs/local.
#>

$ErrorActionPreference = "Stop"

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "     🚀 api-ticket - Implantação de Infraestrutura Local (IaC)    " -ForegroundColor Cyan
Write-Host "=================================================================" -ForegroundColor Cyan

# 1. Checagem de ferramentas pré-instaladas
$tools = @("docker", "terraform", "kubectl", "helm")
foreach ($tool in $tools) {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
        Write-Host "❌ Erro: A ferramenta '$tool' não foi encontrada no PATH do sistema." -ForegroundColor Red
        Write-Host "Por favor, siga o tutorial de instalação no arquivo iac/README.md." -ForegroundColor Yellow
        exit 1
    }
}
Write-Host "✅ Todas as ferramentas pré-requisito foram encontradas ($($tools -join ', '))." -ForegroundColor Green

# 2. Verificação do Docker Daemon
try {
    docker info > $null 2>&1
    Write-Host "✅ Docker Daemon está em execução." -ForegroundColor Green
} catch {
    Write-Host "❌ Erro: O Docker Daemon não está em execução. Inicie o Docker Desktop e tente novamente." -ForegroundColor Red
    exit 1
}

# 3. Verificação do Token do GitHub
if (-not $env:TF_VAR_github_pat -or $env:TF_VAR_github_pat -eq "ghp_mock_token_change_me_or_set_via_tf_vars") {
    Write-Host "⚠️ Variável TF_VAR_github_pat não definida ou contendo o valor padrão." -ForegroundColor Yellow
    $userInputToken = Read-Host "Insira seu GitHub Personal Access Token (PAT)"
    if ([string]::IsNullOrWhiteSpace($userInputToken)) {
        Write-Host "❌ Erro: Token do GitHub é obrigatório para registrar o Runner." -ForegroundColor Red
        exit 1
    }
    $env:TF_VAR_github_pat = $userInputToken
}
Write-Host "✅ Token do GitHub configurado." -ForegroundColor Green

# 4. Execução do Terraform no ambiente local
$envLocalDir = Join-Path $PSScriptRoot "..\terraform\envs\local"
Push-Location $envLocalDir

try {
    Write-Host "`n📦 Inicializando o Terraform no ambiente local..." -ForegroundColor Cyan
    terraform init

    Write-Host "`n🔨 Aplicando a infraestrutura no Kubernetes/Docker..." -ForegroundColor Cyan
    terraform apply -auto-approve

    Write-Host "`n🎉 Infraestrutura provisionada com sucesso!" -ForegroundColor Green
    Write-Host "=================================================================" -ForegroundColor Cyan
    Write-Host " 📊 Grafana Dashboard:   http://localhost:30030" -ForegroundColor White
    Write-Host " 🔍 SonarQube:           http://localhost:30900" -ForegroundColor White
    Write-Host " 🐮 Rancher Manager:     https://localhost:30443" -ForegroundColor White
    Write-Host "=================================================================" -ForegroundColor Cyan
} finally {
    Pop-Location
}
