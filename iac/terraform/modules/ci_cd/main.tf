resource "kubernetes_service_account" "ticket_github_runner_sa" {
  metadata {
    name      = "ticket-github-runner-sa"
    namespace = var.namespace_name
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
  triggers = {
    dockerfile_hash = filesha256("${path.module}/../../../Dockerfile-runner")
  }

  provisioner "local-exec" {
    interpreter = ["PowerShell", "-Command"]

    command = <<-EOT
      docker build -t ticket-runner:latest -f "${path.module}/../../../Dockerfile-runner" "${path.module}/../../.."
      docker save -o "${path.module}/../../../ticket-runner.tar" ticket-runner:latest
      foreach ($node in @("${var.cluster_name}-control-plane", "${var.cluster_name}-worker", "${var.cluster_name}-worker2")) {
        docker cp "${path.module}/../../../ticket-runner.tar" $node`:/ticket-runner.tar
        docker exec $node ctr -n k8s.io images import /ticket-runner.tar
        docker exec $node rm -f /ticket-runner.tar
      }
      Remove-Item -Force "${path.module}/../../../ticket-runner.tar" -ErrorAction SilentlyContinue
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
    namespace = var.namespace_name
  }

  spec {
    replicas = 2
    selector { match_labels = { app = "ticket-github-runner" } }

    template {
      metadata { labels = { app = "ticket-github-runner" } }
      spec {
        service_account_name = kubernetes_service_account.ticket_github_runner_sa.metadata[0].name

        host_aliases {
          ip        = var.registry_ip
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

          resources {
            limits = {
              cpu    = "2000m"
              memory = "2048Mi"
            }
            requests = {
              cpu    = "500m"
              memory = "1024Mi"
            }
          }

          volume_mount {
            name       = "owasp-cache"
            mount_path = "/root/.owasp/dependency-check/data"
          }

          volume_mount {
            name       = "m2-cache"
            mount_path = "/root/.m2"
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

          resources {
            limits = {
              cpu    = "1500m"
              memory = "1024Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "512Mi"
            }
          }

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

        volume {
          name = "m2-cache"
          host_path {
            path = "/var/m2-cache-in-node"
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
    namespace = var.namespace_name
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

          resources {
            limits = {
              cpu    = "1500m"
              memory = "2560Mi"
            }
            requests = {
              cpu    = "500m"
              memory = "1024Mi"
            }
          }

          readiness_probe {
            http_get {
              path = "/api/system/status"
              port = 9000
            }
            initial_delay_seconds = 40
            period_seconds        = 10
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
    namespace = var.namespace_name
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
    interpreter = ["PowerShell", "-Command"]

    command = <<-EOT
      $job = Start-Job -ScriptBlock { kubectl -n ${var.namespace_name} port-forward svc/ticket-sonarqube 19000:9000 }
      
      for ($i=1; $i -le 100; $i++) {
        Start-Sleep -Seconds 2
        try {
          $res = Invoke-RestMethod -Uri "http://127.0.0.1:19000/api/system/status" -TimeoutSec 3 -ErrorAction SilentlyContinue
          if ($res.status -eq "UP") { break }
        } catch {}
      }

      $pair = "admin:admin"
      $bytes = [System.Text.Encoding]::ASCII.GetBytes($pair)
      $base64 = [Convert]::ToBase64String($bytes)
      $headers = @{ Authorization = "Basic $base64" }

      try {
        Invoke-RestMethod -Uri "http://127.0.0.1:19000/api/users/change_password?login=admin&previousPassword=admin&password=${var.sonar_admin_password}" -Method Post -Headers $headers -ErrorAction SilentlyContinue
      } catch {}

      $newPair = "admin:${var.sonar_admin_password}"
      $newBytes = [System.Text.Encoding]::ASCII.GetBytes($newPair)
      $newBase64 = [Convert]::ToBase64String($newBytes)
      $newHeaders = @{ Authorization = "Basic $newBase64" }

      try {
        Invoke-RestMethod -Uri "http://127.0.0.1:19000/api/user_tokens/revoke?name=terraform-token" -Method Post -Headers $newHeaders -ErrorAction SilentlyContinue
      } catch {}

      try {
        $tokenRes = Invoke-RestMethod -Uri "http://127.0.0.1:19000/api/user_tokens/generate?name=terraform-token" -Method Post -Headers $newHeaders
        $token = $tokenRes.token
        Set-Content -Path "${path.module}/../../../.sonar_token" -Value $token -NoNewline
      } catch {
        Set-Content -Path "${path.module}/../../../.sonar_token" -Value "sqp_mock_token_for_local_dev" -NoNewline
      } finally {
        Stop-Job $job -ErrorAction SilentlyContinue
        Remove-Job $job -ErrorAction SilentlyContinue
      }
    EOT
  }
}

data "local_file" "sonar_token" {
  depends_on = [null_resource.sonar_token_generator]
  filename   = "${path.module}/../../../.sonar_token"
}
