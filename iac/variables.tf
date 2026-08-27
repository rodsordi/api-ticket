variable "cluster_name" {
  type        = string
  default     = "ticket-cluster-local"
  description = "Name of the local Kind Kubernetes cluster"
}

variable "namespace_name" {
  type        = string
  default     = "ticket"
  description = "Kubernetes namespace for the api-ticket application"
}

variable "github_repo_url" {
  type        = string
  default     = "https://github.com/rodsordi/api-ticket"
  description = "URL of the target GitHub repository for the self-hosted runner"
}

variable "github_pat" {
  type        = string
  sensitive   = true
  default     = "ghp_mock_token_change_me_or_set_via_tf_vars"
  description = "GitHub Personal Access Token with repo and admin:org permissions"
}

variable "sonar_admin_password" {
  type        = string
  sensitive   = true
  default     = "AdminPass123!"
  description = "Admin password for local SonarQube instance"
}

variable "sonar_data_host_path" {
  type        = string
  default     = "/var/sonarqube-data-host"
  description = "Host path for SonarQube persistence volume"
}

variable "nvd_api_key" {
  type        = string
  sensitive   = true
  default     = ""
  description = "NVD API key for OWASP Dependency-Check scanner"
}
