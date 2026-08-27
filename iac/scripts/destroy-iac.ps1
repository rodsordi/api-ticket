# Script de Destruição da Infraestrutura Local (PowerShell)
$ErrorActionPreference = "Stop"
$envLocalDir = Join-Path $PSScriptRoot "..\terraform\envs\local"
Push-Location $envLocalDir
try {
    Write-Host "🗑️ Destruindo a infraestrutura local (Terraform)..." -ForegroundColor Yellow
    terraform destroy -auto-approve
    Write-Host "✅ Infraestrutura removida com sucesso!" -ForegroundColor Green
} finally {
    Pop-Location
}
