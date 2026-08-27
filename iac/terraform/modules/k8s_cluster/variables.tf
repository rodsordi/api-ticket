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
