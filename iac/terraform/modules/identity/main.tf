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
    type     = "NodePort"
    selector = { app = "keycloak" }
    port {
      port        = 8080
      target_port = 8080
      node_port   = 30081
    }
  }
}

# --- CERT-MANAGER FOR RANCHER ---
resource "helm_release" "cert_manager" {
  name             = "cert-manager"
  repository       = "https://charts.jetstack.io"
  chart            = "cert-manager"
  namespace        = "cert-manager"
  create_namespace = true
  version          = "v1.14.4"

  set {
    name  = "installCRDs"
    value = "true"
  }
}

# --- RANCHER MANAGER UI ---
resource "helm_release" "rancher" {
  depends_on = [helm_release.cert_manager]

  name             = "rancher"
  repository       = "https://releases.rancher.com/server-charts/stable"
  chart            = "rancher"
  namespace        = "cattle-system"
  create_namespace = true
  version          = "2.8.5"

  set {
    name  = "hostname"
    value = "rancher.local.ticket"
  }
  set {
    name  = "bootstrapPassword"
    value = "admin"
  }
  set {
    name  = "replicas"
    value = "1"
  }
  set {
    name  = "ingress.tls.source"
    value = "rancher"
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
