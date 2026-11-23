#!/usr/bin/env bash
# Etap 6/7: prosty test działania aplikacji.
#   ./scripts/smoke-test.sh                          # przez port-forward (localhost:8080)
#   BASE_URL=http://poke.localhost:8081 ./scripts/smoke-test.sh   # przez Ingress
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
fail=0

check() {
  local name="$1" path="$2" expected="$3"
  local code
  code=$(curl -s -o /dev/null -w '%{http_code}' "${BASE_URL}${path}" || true)
  if [[ "$code" == "$expected" ]]; then
    printf '  [OK]   %-28s %s -> %s\n' "$name" "$path" "$code"
  else
    printf '  [FAIL] %-28s %s -> %s (oczekiwano %s)\n' "$name" "$path" "$code" "$expected"
    fail=1
  fi
}

echo "Testuję ${BASE_URL}"
check "liveness"            /actuator/health/liveness  200
check "readiness"           /actuator/health/readiness 200
check "frontend (Angular)"  /                          200
check "API - pikachu"       /api/pokemon/pikachu       200
check "API - lista"         "/api/pokemon?limit=5"     200
check "API - brak pokemona" /api/pokemon/nie-ma-takiego 404
check "Swagger UI"          /swagger-ui.html           302

echo
echo "Konfiguracja instancji (/api/info):"
curl -s "${BASE_URL}/api/info"; echo

exit $fail
