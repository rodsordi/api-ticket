output "kafka_service" {
  value = kubernetes_service.kafka.metadata[0].name
}
