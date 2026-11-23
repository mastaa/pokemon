#!/usr/bin/env bash
# Etap 6: przekierowanie portu z lokalnej maszyny do Service'u w klastrze.
# localhost:${LOCAL_PORT} --(tunel przez API server)--> svc/poke:80 --> Pod:8080
set -euo pipefail

NAMESPACE="${NAMESPACE:-poke}"
LOCAL_PORT="${LOCAL_PORT:-8080}"

echo "Czekam na gotowość Deploymentu..."
kubectl rollout status deploy/poke -n "$NAMESPACE" --timeout=120s

echo "Aplikacja:  http://localhost:${LOCAL_PORT}"
echo "Swagger UI: http://localhost:${LOCAL_PORT}/swagger-ui.html"
echo "Ctrl+C kończy przekierowanie."
exec kubectl port-forward -n "$NAMESPACE" svc/poke "${LOCAL_PORT}:80"
