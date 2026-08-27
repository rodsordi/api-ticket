output "cassandra_service" {
  value = kubernetes_service.cassandra.metadata[0].name
}

output "redis_service" {
  value = kubernetes_service.redis.metadata[0].name
}
