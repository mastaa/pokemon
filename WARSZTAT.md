# Warsztat: wprowadzenie do Kubernetesa na przykładzie aplikacji Pokédex

Aplikacja: Spring Boot 4 (WebFlux) + Angular, pobiera dane z https://pokeapi.co.

## Etapy (branche)

Każdy branch bazuje na poprzednim – można w dowolnym momencie "przeskoczyć" do gotowego rozwiązania:

| Etap | Branch | Co dochodzi |
|------|--------|-------------|
| 0 | `k8s/00-start` | Aplikacja przygotowana pod K8s (actuator/probe'y, `/api/info`), prezentacja |
| 1 | `k8s/01-dockerfile` | `Dockerfile`, `.dockerignore` – obraz kontenera |
| 2 | `k8s/02-deployment` | `k8s/00-namespace.yaml`, `k8s/deployment.yaml` |
| 3 | `k8s/03-configmap` | `k8s/configmap.yaml` + `envFrom` w Deployment |
| 4 | `k8s/04-secret` | `k8s/secret.yaml` + `secretKeyRef` w Deployment |
| 5 | `k8s/05-service` | `k8s/service.yaml` – stabilny adres i load balancing |
| 6 | `k8s/06-port-forward` | `scripts/` – port-forward i testy lokalne |
| 7 | `k8s/07-ingress-traefik` | `k8s/ingress.yaml` – wystawienie na świat przez Traefik |

```bash
git checkout k8s/03-configmap          # przejście do etapu
git diff k8s/02-deployment k8s/03-configmap   # co dokładnie doszło w etapie
```

Instrukcje krok po kroku: `docs/etapy/`. Prezentacja: `docs/prezentacja/index.html`
(otwórz w przeglądarce, strzałki ←/→ lub spacja, `F` – pełny ekran).

## Wymagania

- Docker
- `kubectl`
- lokalny klaster – rekomendowany **k3d** (k3s w Dockerze, ma wbudowany Traefik):
  ```bash
  brew install k3d          # lub: curl -s https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash
  k3d cluster create workshop -p "8081:80@loadbalancer" --agents 1
  kubectl get nodes
  ```

## Zmiany w aplikacji na potrzeby warsztatu (etap 0)

- `spring-boot-starter-actuator` – endpointy `/actuator/health/liveness` i `/actuator/health/readiness`
  używane przez probe'y Kubernetesa.
- `GET /api/info` – zwraca hostname (nazwę Poda), środowisko i konfigurację
  (token tylko zamaskowany). Pozwala "zobaczyć" ConfigMap, Secret i load balancing.
- Konfiguracja `app.*` w `application.yaml` z wartościami domyślnymi, nadpisywana
  zmiennymi środowiskowymi (`APP_ENVIRONMENT`, `APP_WELCOME_MESSAGE`, `APP_API_TOKEN`, `POKEAPI_BASE_URL`).
- Graceful shutdown (`server.shutdown: graceful`) – ważne przy rolling update.
