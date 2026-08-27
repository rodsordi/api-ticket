output "kind_cluster_endpoint" {
  description = "Endpoint URL of the Kind local Kubernetes cluster"
  value       = module.cluster.endpoint
}

output "kind_kubeconfig_path" {
  description = "Path to the local kubeconfig file for ticket-cluster-local"
  value       = module.cluster.kubeconfig_path
}

output "grafana_url" {
  description = "URL to access local Grafana dashboard"
  value       = module.observability.grafana_url
}

output "sonarqube_url" {
  description = "URL to access local SonarQube web dashboard"
  value       = module.ci_cd.sonarqube_url
}

output "rancher_url" {
  description = "URL to access local Rancher management UI"
  value       = module.identity.rancher_url
}

output "sonar_token" {
  description = "Generated API token for SonarQube authentication"
  value       = module.ci_cd.sonar_token
  sensitive   = true
}
