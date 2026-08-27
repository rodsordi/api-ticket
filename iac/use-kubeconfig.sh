#!/usr/bin/env bash
set -euo pipefail

CLUSTER_NAME="ticket-cluster-local"
KUBECONFIG_PATH="$(kind get kubeconfig-path --name "${CLUSTER_NAME}" 2>/dev/null || echo "")"

if [ -z "${KUBECONFIG_PATH}" ]; then
  echo "Error: Kind cluster '${CLUSTER_NAME}' not found." >&2
  exit 1
fi

export KUBECONFIG="${KUBECONFIG_PATH}"
echo "KUBECONFIG set to ${KUBECONFIG}"
kubectl get nodes
