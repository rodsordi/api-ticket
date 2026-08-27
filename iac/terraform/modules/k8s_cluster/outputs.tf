output "endpoint" {
  description = "Endpoint URL of the Kind local Kubernetes cluster"
  value       = kind_cluster.ticket_cluster.endpoint
}

output "kubeconfig_path" {
  description = "Path to the local kubeconfig file for ticket-cluster-local"
  value       = kind_cluster.ticket_cluster.kubeconfig_path
}

output "client_certificate" {
  description = "Client certificate data for K8s authentication"
  value       = kind_cluster.ticket_cluster.client_certificate
}

output "client_key" {
  description = "Client key data for K8s authentication"
  value       = kind_cluster.ticket_cluster.client_key
}

output "cluster_ca_certificate" {
  description = "Cluster CA certificate data for K8s authentication"
  value       = kind_cluster.ticket_cluster.cluster_ca_certificate
}

output "namespace" {
  description = "Kubernetes namespace name"
  value       = kubernetes_namespace.ticket.metadata[0].name
}

output "registry_ip" {
  description = "IP address of the local Docker registry"
  value       = docker_container.kind_registry.network_data[0].ip_address
}
