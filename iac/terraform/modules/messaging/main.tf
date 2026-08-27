# --- KAFKA BROKER ---
resource "kubernetes_deployment" "kafka" {
  metadata {
    name      = "kafka"
    namespace = var.namespace_name
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "kafka" } }

    template {
      metadata { labels = { app = "kafka" } }
      spec {
        container {
          name  = "kafka"
          image = "apache/kafka:3.8.0"

          env {
            name  = "KAFKA_NODE_ID"
            value = "1"
          }
          env {
            name  = "KAFKA_PROCESS_ROLES"
            value = "broker,controller"
          }
          env {
            name  = "KAFKA_CONTROLLER_QUORUM_VOTERS"
            value = "1@localhost:9093"
          }
          env {
            name  = "KAFKA_CONTROLLER_LISTENER_NAMES"
            value = "CONTROLLER"
          }
          env {
            name  = "KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR"
            value = "1"
          }
          env {
            name  = "KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR"
            value = "1"
          }
          env {
            name  = "KAFKA_TRANSACTION_STATE_LOG_MIN_ISR"
            value = "1"
          }
          env {
            name  = "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP"
            value = "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT"
          }
          env {
            name  = "KAFKA_LISTENERS"
            value = "PLAINTEXT://:29092,CONTROLLER://:9093,PLAINTEXT_HOST://:9092"
          }
          env {
            name  = "KAFKA_ADVERTISED_LISTENERS"
            value = "PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092"
          }

          port { container_port = 9092 }
          port { container_port = 29092 }

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
            tcp_socket { port = 29092 }
            initial_delay_seconds = 15
            period_seconds        = 10
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "kafka" {
  metadata {
    name      = "kafka"
    namespace = var.namespace_name
  }

  spec {
    selector = { app = "kafka" }
    port {
      name        = "plaintext"
      port        = 29092
      target_port = 29092
    }
    port {
      name        = "host"
      port        = 9092
      target_port = 9092
    }
  }
}
