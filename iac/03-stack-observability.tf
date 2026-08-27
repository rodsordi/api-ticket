# --- PROMETHEUS ---
resource "kubernetes_config_map" "prometheus_config" {
  depends_on = [kubernetes_namespace.ticket]

  metadata {
    name      = "prometheus-config"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  data = {
    "prometheus.yml" = file("${path.module}/prometheus/prometheus.yml")
  }
}

resource "kubernetes_deployment" "prometheus" {
  depends_on = [kubernetes_config_map.prometheus_config]

  metadata {
    name      = "prometheus"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "prometheus" } }

    template {
      metadata { labels = { app = "prometheus" } }
      spec {
        container {
          name  = "prometheus"
          image = "prom/prometheus:latest"

          args = [
            "--config.file=/etc/prometheus/prometheus.yml",
            "--web.enable-otlp-receiver"
          ]

          port { container_port = 9090 }

          volume_mount {
            name       = "config-volume"
            mount_path = "/etc/prometheus"
          }
        }

        volume {
          name = "config-volume"
          config_map { name = kubernetes_config_map.prometheus_config.metadata[0].name }
        }
      }
    }
  }
}

resource "kubernetes_service" "prometheus" {
  metadata {
    name      = "prometheus"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    selector = { app = "prometheus" }
    port {
      name        = "http"
      port        = 9090
      target_port = 9090
    }
  }
}

# --- LOKI ---
resource "kubernetes_deployment" "loki" {
  depends_on = [kubernetes_namespace.ticket]

  metadata {
    name      = "loki"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "loki" } }

    template {
      metadata { labels = { app = "loki" } }
      spec {
        container {
          name  = "loki"
          image = "grafana/loki:latest"
          port { container_port = 3100 }
        }
      }
    }
  }
}

resource "kubernetes_service" "loki" {
  metadata {
    name      = "loki"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    selector = { app = "loki" }
    port {
      name        = "http"
      port        = 3100
      target_port = 3100
    }
  }
}

# --- JAEGER ---
resource "kubernetes_deployment" "jaeger" {
  depends_on = [kubernetes_namespace.ticket]

  metadata {
    name      = "jaeger"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "jaeger" } }

    template {
      metadata { labels = { app = "jaeger" } }
      spec {
        container {
          name  = "jaeger"
          image = "jaegertracing/all-in-one:latest"

          env {
            name  = "COLLECTOR_OTLP_ENABLED"
            value = "true"
          }

          port { container_port = 16686 }
          port { container_port = 4317 }
          port { container_port = 4318 }
        }
      }
    }
  }
}

resource "kubernetes_service" "jaeger" {
  metadata {
    name      = "jaeger"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    selector = { app = "jaeger" }
    port {
      name        = "query"
      port        = 16686
      target_port = 16686
    }
    port {
      name        = "otlp-grpc"
      port        = 4317
      target_port = 4317
    }
    port {
      name        = "otlp-http"
      port        = 4318
      target_port = 4318
    }
  }
}

# --- GRAFANA ---
resource "kubernetes_config_map" "grafana_datasources" {
  depends_on = [kubernetes_namespace.ticket]

  metadata {
    name      = "grafana-datasources"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  data = {
    "prometheus.yml" = file("${path.module}/grafana/provisioning/datasources/prometheus.yml")
    "loki.yml"       = file("${path.module}/grafana/provisioning/datasources/loki.yml")
    "jaeger.yml"     = file("${path.module}/grafana/provisioning/datasources/jaeger.yml")
  }
}

resource "kubernetes_config_map" "grafana_dashboards_provider" {
  depends_on = [kubernetes_namespace.ticket]

  metadata {
    name      = "grafana-dashboards-provider"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  data = {
    "dashboards.yml" = file("${path.module}/grafana/provisioning/dashboards/dashboards.yml")
  }
}

resource "kubernetes_config_map" "grafana_dashboards_json" {
  depends_on = [kubernetes_namespace.ticket]

  metadata {
    name      = "grafana-dashboards-json"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  data = {
    "prometheus-metrics.json" = file("${path.module}/grafana/provisioning/dashboards/json/prometheus-metrics.json")
    "loki-metrics.json"       = file("${path.module}/grafana/provisioning/dashboards/json/loki-metrics.json")
    "jaeger-traces.json"      = file("${path.module}/grafana/provisioning/dashboards/json/jaeger-traces.json")
    "hpa-scaling.json"        = file("${path.module}/grafana/provisioning/dashboards/json/hpa-scaling.json")
  }
}

resource "kubernetes_deployment" "grafana" {
  depends_on = [
    kubernetes_config_map.grafana_datasources,
    kubernetes_config_map.grafana_dashboards_provider,
    kubernetes_config_map.grafana_dashboards_json
  ]

  metadata {
    name      = "grafana"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    replicas = 1
    selector { match_labels = { app = "grafana" } }

    template {
      metadata { labels = { app = "grafana" } }
      spec {
        container {
          name  = "grafana"
          image = "grafana/grafana:latest"

          env {
            name  = "GF_AUTH_ANONYMOUS_ENABLED"
            value = "true"
          }
          env {
            name  = "GF_AUTH_ANONYMOUS_ORG_ROLE"
            value = "Admin"
          }

          port { container_port = 3000 }

          volume_mount {
            name       = "datasources"
            mount_path = "/etc/grafana/provisioning/datasources"
          }
          volume_mount {
            name       = "dashboards-provider"
            mount_path = "/etc/grafana/provisioning/dashboards"
          }
          volume_mount {
            name       = "dashboards-json"
            mount_path = "/var/lib/grafana/dashboards"
          }
        }

        volume {
          name = "datasources"
          config_map { name = kubernetes_config_map.grafana_datasources.metadata[0].name }
        }
        volume {
          name = "dashboards-provider"
          config_map { name = kubernetes_config_map.grafana_dashboards_provider.metadata[0].name }
        }
        volume {
          name = "dashboards-json"
          config_map { name = kubernetes_config_map.grafana_dashboards_json.metadata[0].name }
        }
      }
    }
  }
}

resource "kubernetes_service" "grafana" {
  metadata {
    name      = "grafana"
    namespace = kubernetes_namespace.ticket.metadata[0].name
  }

  spec {
    selector = { app = "grafana" }
    port {
      port        = 3000
      target_port = 3000
      node_port   = 30000
    }
    type = "NodePort"
  }
}
