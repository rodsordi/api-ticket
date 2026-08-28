terraform {
  required_providers {
    kind = {
      source  = "tehcyx/kind"
      version = "~> 0.6.0"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.0"
    }
  }
}

resource "kind_cluster" "ticket_cluster" {
  name           = var.cluster_name
  wait_for_ready = true

  kind_config {
    kind        = "Cluster"
    api_version = "kind.x-k8s.io/v1alpha4"

    containerd_config_patches = [
      <<-EOF
      [plugins."io.containerd.grpc.v1.cri".registry.mirrors."kind-registry:5000"]
        endpoint = ["http://kind-registry:5000"]
      EOF
    ]

    node {
      role  = "control-plane"
      image = "kindest/node:v1.28.15"

      kubeadm_config_patches = [
        <<-EOF
        kind: InitConfiguration
        nodeRegistration:
          kubeletExtraArgs:
            node-labels: "ingress-ready=true"
        EOF
      ]

      extra_port_mappings {
        container_port = 80
        host_port      = 80
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 443
        host_port      = 443
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30900
        host_port      = 30900
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30030
        host_port      = 3000
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30443
        host_port      = 30443
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30080
        host_port      = 8080
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30081
        host_port      = 8081
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30042
        host_port      = 9042
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30092
        host_port      = 9092
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30379
        host_port      = 6379
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30090
        host_port      = 9090
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30100
        host_port      = 3100
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30686
        host_port      = 16686
        protocol       = "TCP"
      }
    }

    node {
      role  = "worker"
      image = "kindest/node:v1.28.15"
    }

    node {
      role  = "worker"
      image = "kindest/node:v1.28.15"
    }
  }
}

data "docker_network" "kind_network" {
  name       = "kind"
  depends_on = [kind_cluster.ticket_cluster]
}

resource "docker_container" "kind_registry" {
  name    = "kind-registry"
  image   = "registry:2"
  restart = "always"

  ports {
    internal = 5000
    external = 5001
  }

  networks_advanced {
    name = data.docker_network.kind_network.name
  }
}

resource "kubernetes_namespace" "ticket" {
  depends_on = [kind_cluster.ticket_cluster]

  metadata {
    name = var.namespace_name
  }

  lifecycle {
    ignore_changes = [metadata[0].annotations, metadata[0].labels]
  }
}

resource "helm_release" "metrics_server" {
  name             = "metrics-server"
  repository       = "https://kubernetes-sigs.github.io/metrics-server/"
  chart            = "metrics-server"
  namespace        = "kube-system"
  version          = "3.12.0"
  create_namespace = false

  set {
    name  = "args[0]"
    value = "--kubelet-insecure-tls"
  }

  depends_on = [kind_cluster.ticket_cluster]
}
