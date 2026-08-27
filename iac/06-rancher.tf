resource "helm_release" "rancher" {
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
    name  = "service.type"
    value = "NodePort"
  }
  set {
    name  = "service.nodePort"
    value = "30443"
  }

  depends_on = [kind_cluster.ticket_cluster]
}
