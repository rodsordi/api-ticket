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
            name  = "MAX_HEAP_SIZE"
            value = "512M"
          }
          env {
            name  = "HEAP_NEWSIZE"
            value = "128M"
          }
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
              memory = "2048Mi"
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

# --- CASSANDRA INIT JOB ---
resource "kubernetes_config_map" "cassandra_init_script" {
  metadata {
    name      = "cassandra-init-script"
    namespace = var.namespace_name
  }

  data = {
    "V1__create_tables.cql" = file("${path.module}/../../../../application/src/main/resources/db/migration/V1__create_tables.cql")
  }
}

resource "kubernetes_job" "cassandra_init" {
  depends_on = [kubernetes_deployment.cassandra, kubernetes_service.cassandra]

  metadata {
    name      = "cassandra-init"
    namespace = var.namespace_name
  }

  spec {
    template {
      metadata {
        name = "cassandra-init"
      }
      spec {
        restart_policy = "OnFailure"
        container {
          name    = "cassandra-init"
          image   = "cassandra:5.0"
          command = [
            "sh",
            "-c",
            "until cqlsh cassandra 9042 -e 'describe cluster'; do sleep 3; done; cqlsh cassandra 9042 -e \"CREATE KEYSPACE IF NOT EXISTS ticket WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};\"; cqlsh cassandra 9042 -k ticket -f /scripts/V1__create_tables.cql"
          ]

          volume_mount {
            name       = "cql-script"
            mount_path = "/scripts"
          }
        }

        volume {
          name = "cql-script"
          config_map {
            name = kubernetes_config_map.cassandra_init_script.metadata[0].name
          }
        }
      }
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
