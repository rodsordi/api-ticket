variable "cluster_name" {
  type        = string
  description = "Name of the local Kind cluster"
}

variable "namespace_name" {
  type        = string
  description = "Target Kubernetes namespace"
}

variable "registry_ip" {
  type        = string
  description = "IP address of the local kind registry"
}

variable "github_repo_url" {
  type        = string
  description = "Target GitHub repository URL"
}

variable "github_pat" {
  type        = string
  sensitive   = true
  description = "GitHub Personal Access Token"
}

variable "sonar_admin_password" {
  type        = string
  sensitive   = true
  description = "Admin password for SonarQube"
}

variable "sonar_data_host_path" {
  type        = string
  description = "Host path for SonarQube data persistence"
}

variable "nvd_api_key" {
  type        = string
  sensitive   = true
  default     = ""
  description = "NVD API key for OWASP Dependency-Check scanner"
}
