# --- CASSANDRA DATABASE ---
resource "kubernetes_deployment" "cassandra" {
  metadata {
    name      = "cassandra"
    namespace = var.namespace_name
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "cassandra" } }

    template {
      metadata { labels = { app = "cassandra" } }
      spec {
        container {
          name  = "cassandra"
          image = "cassandra:5.0"

          env {
            name  = "CASSANDRA_CLUSTER_NAME"
            value = "TicketCluster"
          }
          env {
            name  = "CASSANDRA_DC"
            value = "datacenter1"
          }
          env {
            name  = "CASSANDRA_ENDPOINT_SNITCH"
            value = "GossipingPropertyFileSnitch"
          }

          port { container_port = 9042 }

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
            tcp_socket { port = 9042 }
            initial_delay_seconds = 30
            period_seconds        = 10
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "cassandra" {
  metadata {
    name      = "cassandra"
    namespace = var.namespace_name
  }

  spec {
    selector = { app = "cassandra" }
    port {
      port        = 9042
      target_port = 9042
    }
  }
}

# --- REDIS CACHE ---
resource "kubernetes_deployment" "redis" {
  metadata {
    name      = "redis"
    namespace = var.namespace_name
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "redis" } }

    template {
      metadata { labels = { app = "redis" } }
      spec {
        container {
          name  = "redis"
          image = "redis:7-alpine"
          port { container_port = 6379 }

          resources {
            limits = {
              cpu    = "500m"
              memory = "256Mi"
            }
            requests = {
              cpu    = "100m"
              memory = "128Mi"
            }
          }

          readiness_probe {
            tcp_socket { port = 6379 }
            initial_delay_seconds = 5
            period_seconds        = 5
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "redis" {
  metadata {
    name      = "redis"
    namespace = var.namespace_name
  }

  spec {
    selector = { app = "redis" }
    port {
      port        = 6379
      target_port = 6379
    }
  }
}
