resource "kubernetes_service_account" "ticket_github_runner_sa" {
  depends_on = [kind_cluster.ticket_cluster]

  metadata {
    name      = "ticket-github-runner-sa"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }
}

resource "kubernetes_cluster_role_binding" "ticket_github_runner_admin" {
  depends_on = [kubernetes_service_account.ticket_github_runner_sa]

  metadata {
    name = "ticket-github-runner-admin-binding"
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "ClusterRole"
    name      = "cluster-admin"
  }

  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.ticket_github_runner_sa.metadata[0].name
    namespace = kubernetes_service_account.ticket_github_runner_sa.metadata[0].namespace
  }
}

resource "null_resource" "build_and_load_runner_image" {
  depends_on = [kind_cluster.ticket_cluster]

  triggers = {
    dockerfile_hash = filesha256("${path.module}/Dockerfile-runner")
  }

  provisioner "local-exec" {
    interpreter = ["bash", "-c"]

    command = <<-EOT
      set -e
      export MSYS_NO_PATHCONV=1
      docker build --network host -t ticket-runner:latest -f "${path.module}/Dockerfile-runner" "${path.module}"
      docker save -o ticket-runner.tar ticket-runner:latest
      docker cp ticket-runner.tar ${var.cluster_name}-control-plane:/ticket-runner.tar
      docker exec ${var.cluster_name}-control-plane ctr -n k8s.io images import /ticket-runner.tar
      rm -f ticket-runner.tar
    EOT
  }
}

resource "kubernetes_deployment" "ticket_github_runner" {
  depends_on = [
    null_resource.build_and_load_runner_image,
    kubernetes_cluster_role_binding.ticket_github_runner_admin
  ]

  metadata {
    name      = "ticket-github-runner"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    replicas = 2
    selector { match_labels = { app = "ticket-github-runner" } }

    template {
      metadata { labels = { app = "ticket-github-runner" } }
      spec {
        service_account_name = kubernetes_service_account.ticket_github_runner_sa.metadata[0].name

        host_aliases {
          ip        = docker_container.kind_registry.network_data[0].ip_address
          hostnames = ["kind-registry"]
        }

        container {
          name              = "ticket-github-runner"
          image             = "ticket-runner:latest"
          image_pull_policy = "Never"

          env {
            name  = "REPO_URL"
            value = var.github_repo_url
          }
          env {
            name = "RUNNER_NAME"
            value_from {
              field_ref {
                field_path = "metadata.name"
              }
            }
          }
          env {
            name  = "ACCESS_TOKEN"
            value = var.github_pat
          }
          env {
            name  = "LABELS"
            value = "self-hosted,local,ticket"
          }
          env {
            name  = "DOCKER_HOST"
            value = "tcp://localhost:2375"
          }
          env {
            name  = "RUN_AS_ROOT"
            value = "true"
          }
          env {
            name  = "SONAR_TOKEN"
            value = sensitive(trimspace(data.local_file.sonar_token.content))
          }
          env {
            name  = "NVD_API_KEY"
            value = var.nvd_api_key
          }

          volume_mount {
            name       = "owasp-cache"
            mount_path = "/root/.owasp/dependency-check/data"
          }

          volume_mount {
            name       = "containerd-sock"
            mount_path = "/run/containerd/containerd.sock"
          }

          security_context {
            privileged = true
          }
        }

        container {
          name  = "dind"
          image = "docker:27-dind"
          args = [
            "--host=tcp://0.0.0.0:2375",
            "--host=unix:///var/run/docker.sock",
            "--insecure-registry=kind-registry:5000"
          ]

          env {
            name  = "DOCKER_TLS_CERTDIR"
            value = ""
          }

          port { container_port = 2375 }

          security_context {
            privileged = true
          }
        }

        volume {
          name = "containerd-sock"
          host_path {
            path = "/run/containerd/containerd.sock"
            type = "Socket"
          }
        }

        volume {
          name = "owasp-cache"
          host_path {
            path = "/var/owasp-cache-in-node"
            type = "DirectoryOrCreate"
          }
        }
      }
    }
  }
}

# --- SONARQUBE COMMUNITY ---
resource "kubernetes_deployment" "ticket_sonarqube" {
  metadata {
    name      = "ticket-sonarqube"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  lifecycle {
    ignore_changes = [metadata[0].annotations]
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "ticket-sonarqube" } }

    template {
      metadata { labels = { app = "ticket-sonarqube" } }
      spec {
        init_container {
          name    = "fix-volume-permissions"
          image   = "busybox:1.36"
          command = ["sh", "-c", "chown -R 1000:1000 /opt/sonarqube/data /opt/sonarqube/extensions"]

          security_context { run_as_user = 0 }

          volume_mount {
            name       = "sonarqube-data"
            mount_path = "/opt/sonarqube/data"
          }
          volume_mount {
            name       = "sonarqube-extensions"
            mount_path = "/opt/sonarqube/extensions"
          }
        }

        container {
          name  = "sonarqube"
          image = "sonarqube:community"

          port { container_port = 9000 }

          env {
            name  = "SONAR_ES_BOOTSTRAP_CHECKS_DISABLE"
            value = "true"
          }

          volume_mount {
            name       = "sonarqube-data"
            mount_path = "/opt/sonarqube/data"
          }
          volume_mount {
            name       = "sonarqube-extensions"
            mount_path = "/opt/sonarqube/extensions"
          }
        }

        volume {
          name = "sonarqube-data"
          host_path {
            path = "${var.sonar_data_host_path}/data"
            type = "DirectoryOrCreate"
          }
        }

        volume {
          name = "sonarqube-extensions"
          host_path {
            path = "${var.sonar_data_host_path}/extensions"
            type = "DirectoryOrCreate"
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "ticket_sonarqube" {
  metadata {
    name      = "ticket-sonarqube"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  lifecycle {
    ignore_changes = [metadata[0].annotations]
  }

  spec {
    selector = { app = "ticket-sonarqube" }
    port {
      port        = 9000
      target_port = 9000
      node_port   = 30900
    }
    type = "NodePort"
  }
}

resource "null_resource" "sonar_token_generator" {
  depends_on = [kubernetes_deployment.ticket_sonarqube, kubernetes_service.ticket_sonarqube]

  triggers = {
    password_hash = sha256(var.sonar_admin_password)
  }

  provisioner "local-exec" {
    interpreter = ["bash", "-c"]

    command = <<-EOT
      set -eo pipefail
      export MSYS_NO_PATHCONV=1
      export KUBECONFIG="${replace(kind_cluster.ticket_cluster.kubeconfig_path, "\\", "/")}"

      kubectl -n ${var.namespace_name} port-forward svc/ticket-sonarqube 19000:9000 >/dev/null 2>&1 &
      disown
      trap 'kill -9 $! 2>/dev/null || true' EXIT

      for i in {1..100}; do
        curl -s -m 5 --connect-timeout 2 http://127.0.0.1:19000/api/system/status | grep -q '"status":"UP"' && break
        kill -0 $! 2>/dev/null || { kubectl -n ${var.namespace_name} port-forward svc/ticket-sonarqube 19000:9000 >/dev/null 2>&1 & disown; }
        sleep 3
      done

      curl -s -m 10 --connect-timeout 3 -u admin:admin -X POST "http://127.0.0.1:19000/api/users/change_password?login=admin&previousPassword=admin&password=${var.sonar_admin_password}" >/dev/null || true
      curl -s -m 10 --connect-timeout 3 -u "admin:${var.sonar_admin_password}" -X POST "http://127.0.0.1:19000/api/user_tokens/revoke?name=terraform-token" >/dev/null || true

      TOKEN=$(curl -s -m 10 --connect-timeout 3 -u "admin:${var.sonar_admin_password}" -X POST "http://127.0.0.1:19000/api/user_tokens/generate?name=terraform-token" | grep -o '"token":"[^"]*' | cut -d'"' -f4 || true)
      kill -9 $! 2>/dev/null || true
      [ -n "$TOKEN" ] || { echo "Failed to generate SonarQube token" >&2; exit 1; }
      echo -n "$TOKEN" > "${path.module}/.sonar_token"
    EOT
  }
}

data "local_file" "sonar_token" {
  depends_on = [null_resource.sonar_token_generator]
  filename   = "${path.module}/.sonar_token"
}
