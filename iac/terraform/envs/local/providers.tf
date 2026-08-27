terraform {
  required_version = ">= 1.5.0"

  required_providers {
    kind = {
      source  = "tehcyx/kind"
      version = "~> 0.6.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.32.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.15.0"
    }
    null = {
      source  = "hashicorp/null"
      version = "~> 3.2.0"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.0"
    }
    local = {
      source  = "hashicorp/local"
      version = "~> 2.5.0"
    }
  }
}

provider "kind" {}

provider "kubernetes" {
  host                   = module.cluster.endpoint
  client_certificate     = module.cluster.client_certificate
  client_key             = module.cluster.client_key
  cluster_ca_certificate = module.cluster.cluster_ca_certificate
}

provider "helm" {
  kubernetes {
    host                   = module.cluster.endpoint
    client_certificate     = module.cluster.client_certificate
    client_key             = module.cluster.client_key
    cluster_ca_certificate = module.cluster.cluster_ca_certificate
  }
}

provider "docker" {}
