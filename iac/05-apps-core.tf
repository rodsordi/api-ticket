# --- CASSANDRA DATABASE ---
resource "kubernetes_deployment" "cassandra" {
  metadata {
    name      = "cassandra"
    namespace = kubernetes_namespace.ticket.metadata[0].name
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
        }
      }
    }
  }
}

resource "kubernetes_service" "cassandra" {
  metadata {
    name      = "cassandra"
    namespace = kubernetes_namespace.ticket.metadata[0].name
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
    namespace = kubernetes_namespace.ticket.metadata[0].name
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
        }
      }
    }
  }
}

resource "kubernetes_service" "redis" {
  metadata {
    name      = "redis"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    selector = { app = "redis" }
    port {
      port        = 6379
      target_port = 6379
    }
  }
}

# --- KAFKA BROKER ---
resource "kubernetes_deployment" "kafka" {
  metadata {
    name      = "kafka"
    namespace = kubernetes_namespace.ticket.metadata[0].name
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
        }
      }
    }
  }
}

resource "kubernetes_service" "kafka" {
  metadata {
    name      = "kafka"
    namespace = kubernetes_namespace.ticket.metadata[0].name
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

# --- KEYCLOAK IDENTITY PROVIDER ---
resource "kubernetes_deployment" "keycloak" {
  metadata {
    name      = "keycloak"
    namespace = kubernetes_namespace.ticket.metadata[0].name
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
        }
      }
    }
  }
}

resource "kubernetes_service" "keycloak" {
  metadata {
    name      = "keycloak"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    selector = { app = "keycloak" }
    port {
      port        = 8080
      target_port = 8080
    }
  }
}
