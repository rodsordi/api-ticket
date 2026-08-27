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
      role = "control-plane"

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
        container_port = 30000
        host_port      = 30000
        protocol       = "TCP"
      }
      extra_port_mappings {
        container_port = 30443
        host_port      = 30443
        protocol       = "TCP"
      }
    }

    node { role = "worker" }
    node { role = "worker" }
  }
}

resource "docker_network" "kind_network" {
  name = "kind"
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
    name = docker_network.kind_network.name
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
