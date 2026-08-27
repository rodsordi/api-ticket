output "kind_cluster_endpoint" {
  description = "Endpoint URL of the Kind local Kubernetes cluster"
  value       = kind_cluster.ticket_cluster.endpoint
}

output "kind_kubeconfig_path" {
  description = "Path to the local kubeconfig file for ticket-cluster-local"
  value       = kind_cluster.ticket_cluster.kubeconfig_path
}

output "grafana_url" {
  description = "URL to access local Grafana dashboard"
  value       = "http://localhost:30000"
}

output "sonarqube_url" {
  description = "URL to access local SonarQube web dashboard"
  value       = "http://localhost:30900"
}

output "rancher_url" {
  description = "URL to access local Rancher management UI"
  value       = "https://localhost:30443"
}

output "sonar_token" {
  description = "Generated API token for SonarQube authentication"
  value       = trimspace(data.local_file.sonar_token.content)
  sensitive   = true
}
