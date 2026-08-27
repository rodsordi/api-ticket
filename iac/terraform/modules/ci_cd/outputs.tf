output "sonarqube_url" {
  description = "URL to access local SonarQube web dashboard"
  value       = "http://localhost:30900"
}

output "sonar_token" {
  description = "Generated API token for SonarQube authentication"
  value       = trimspace(data.local_file.sonar_token.content)
  sensitive   = true
}
