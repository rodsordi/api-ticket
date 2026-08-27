# --- KEYCLOAK IDENTITY PROVIDER ---
resource "kubernetes_deployment" "keycloak" {
  metadata {
    name      = "keycloak"
    namespace = var.namespace_name
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "keycloak" } }

    template {
      metadata { labels = { app = "keycloak" } }
      spec {
        container {
          name  = "keycloak"
          image = "quay.io/keycloak/keycloak:26.1.0"
          args  = ["start-dev", "--import-realm"]

          env {
            name  = "KEYCLOAK_ADMIN"
            value = "admin"
          }
          env {
            name  = "KEYCLOAK_ADMIN_PASSWORD"
            value = "admin"
          }

          port { container_port = 8080 }

          resources {
            limits = {
              cpu    = "1000m"
              memory = "1024Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "512Mi"
            }
          }

          readiness_probe {
            http_get {
              path = "/realms/master"
              port = 8080
            }
            initial_delay_seconds = 30
            period_seconds        = 10
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "keycloak" {
  metadata {
    name      = "keycloak"
    namespace = var.namespace_name
  }

  spec {
    selector = { app = "keycloak" }
    port {
      port        = 8080
      target_port = 8080
    }
  }
}

# --- RANCHER MANAGER UI ---
resource "helm_release" "rancher" {
  name             = "rancher"
  repository       = "https://releases.rancher.com/server-charts/stable"
  chart            = "rancher"
  namespace        = "cattle-system"
  create_namespace = true
  version          = "2.8.5"

  set {
    name  = "replicas"
    value = "1"
  }
  set {
    name  = "ingress.tls.source"
    value = "secret"
  }
  set {
    name  = "hostname"
    value = "rancher.local.ticket"
  }
  set {
    name  = "bootstrapPassword"
    value = "admin"
  }
  set {
    name  = "service.type"
    value = "NodePort"
  }
  set {
    name  = "service.nodePort"
    value = "30443"
  }
}
