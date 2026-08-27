variable "cluster_config" {
  type = object({
    name      = string
    namespace = string
  })
  default = {
    name      = "ticket-cluster-local"
    namespace = "ticket"
  }
  description = "Configurações do cluster Kubernetes Kind local"
}

variable "github_repo_url" {
  type        = string
  default     = "https://github.com/rodsordi/api-ticket"
  description = "URL do repositório GitHub para registro do Runner"

  validation {
    condition     = can(regex("^https://github\\.com/[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$", var.github_repo_url))
    error_message = "A variável github_repo_url deve ser uma URL HTTPS válida do GitHub (ex: https://github.com/usuario/repositorio)."
  }
}

variable "github_pat" {
  type        = string
  sensitive   = true
  default     = "ghp_mock_token_change_me_or_set_via_tf_vars"
  description = "GitHub Personal Access Token (PAT)"
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

variable "sonar_data_host_path" {
  type        = string
  default     = "/var/sonarqube-data-host"
  description = "Caminho do host para persistência do SonarQube"
}

variable "nvd_api_key" {
  type        = string
  sensitive   = true
  default     = ""
  description = "NVD API key para scanner OWASP Dependency-Check"
}
