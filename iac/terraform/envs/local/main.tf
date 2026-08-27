module "cluster" {
  source         = "../../modules/k8s_cluster"
  cluster_name   = var.cluster_config.name
  namespace_name = var.cluster_config.namespace
}

module "ci_cd" {
  source               = "../../modules/ci_cd"
  cluster_name         = var.cluster_config.name
  namespace_name       = module.cluster.namespace
  registry_ip          = module.cluster.registry_ip
  github_repo_url      = var.github_repo_url
  github_pat           = var.github_pat
  sonar_admin_password = var.sonar_admin_password
  sonar_data_host_path = var.sonar_data_host_path
  nvd_api_key          = var.nvd_api_key
}

module "observability" {
  source         = "../../modules/observability"
  namespace_name = module.cluster.namespace
}

module "persistence" {
  source         = "../../modules/persistence"
  namespace_name = module.cluster.namespace
}

module "messaging" {
  source         = "../../modules/messaging"
  namespace_name = module.cluster.namespace
}

module "identity" {
  source         = "../../modules/identity"
  namespace_name = module.cluster.namespace
}
