# Etap 7 – wystawienie na świat: Ingress + Traefik

## Cel
Udostępnić aplikację pod nazwą domenową, przez jeden wspólny punkt wejścia do klastra.

## Dlaczego nie Service typu LoadBalancer dla każdej aplikacji?
- 1 Service LoadBalancer = 1 zewnętrzne IP (koszt w chmurze)
- brak routingu po hoście/ścieżce, TLS, nagłówków, rate limitów
- **Ingress Controller** (Traefik) = jedno IP, wiele aplikacji, routing L7

```
Internet / przeglądarka
   │  http://poke.localhost:8081
   ▼
k3d loadbalancer :8081 ─► Traefik (namespace kube-system, Service LoadBalancer :80)
   │  reguła Ingress: Host = poke.localhost
   ▼
Service poke:80 ─► Pody poke :8080
```

## Kroki
```bash
# Traefik jest już w k3s/k3d:
kubectl get pods,svc -n kube-system -l app.kubernetes.io/name=traefik
kubectl get ingressclass

kubectl apply -f k8s/middleware.yaml     # CRD Traefika – nagłówki bezpieczeństwa
kubectl apply -f k8s/ingress.yaml
kubectl get ingress,middleware -n poke
kubectl describe ingress poke -n poke

curl -i http://poke.localhost:8081/api/info      # nagłówek X-Workshop od middleware'u
BASE_URL=http://poke.localhost:8081 ./scripts/smoke-test.sh
for i in $(seq 1 6); do curl -s http://poke.localhost:8081/api/info | grep -o '"hostname":"[^"]*"'; done
```
Przeglądarka: http://poke.localhost:8081

> Gdy `*.localhost` nie działa (niektóre systemy/curl): `curl -H 'Host: poke.localhost' http://127.0.0.1:8081/`
> albo wpis w `/etc/hosts`: `127.0.0.1 poke.localhost`.

## Wariant: IngressRoute (CRD Traefika)
```bash
kubectl delete -f k8s/ingress.yaml                       # Middleware zostaje
kubectl apply -f k8s/optional/ingressroute-traefik.yaml
kubectl get ingressroute -n poke
curl http://api.poke.localhost:8081/api/pokemon/ditto
```

## Inne klastry
- **Docker Desktop / kind / minikube** – Traefik trzeba zainstalować:
  ```bash
  helm repo add traefik https://traefik.github.io/charts
  helm install traefik traefik/traefik -n traefik --create-namespace
  kubectl get svc -n traefik     # EXTERNAL-IP / port
  ```
- **Chmura** – Service Traefika typu LoadBalancer dostaje publiczne IP;
  rekord DNS `poke.twojadomena.pl → IP`, TLS przez cert-manager lub `certResolver` Traefika.

## Całość jednym poleceniem
```bash
kubectl apply -f k8s/00-namespace.yaml && kubectl apply -f k8s/
kubectl delete ns poke     # sprzątanie
k3d cluster delete workshop
```
