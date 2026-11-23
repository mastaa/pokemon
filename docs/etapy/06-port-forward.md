# Etap 6 – przekierowanie portu i testy lokalne

## Cel
Dostać się do aplikacji w klastrze z własnego komputera – bez wystawiania jej na świat.

```
przeglądarka ──► localhost:8080 ──► kubectl ──► API server ──► kubelet ──► Pod:8080
```
`kubectl port-forward` to narzędzie **deweloperskie/diagnostyczne** – tunel żyje tylko,
dopóki działa polecenie. Nie służy do udostępniania aplikacji użytkownikom (→ etap 7).

## Kroki
Terminal 1:
```bash
./scripts/port-forward.sh
# lub ręcznie:
kubectl port-forward -n poke svc/poke 8080:80
```
Terminal 2:
```bash
./scripts/smoke-test.sh
curl -s localhost:8080/api/pokemon/charizard | head -c 300
```
Przeglądarka: http://localhost:8080 i http://localhost:8080/swagger-ui.html

## Warianty port-forward
```bash
kubectl port-forward -n poke svc/poke 8080:80          # do Service
kubectl port-forward -n poke deploy/poke 8080:8080     # do (jednego) Poda z Deploymentu
kubectl port-forward -n poke pod/<nazwa-poda> 9000:8080
kubectl port-forward -n poke svc/poke 8080:80 --address 0.0.0.0   # dostęp z sieci LAN
```

## Ciekawostka: brak load balancingu
```bash
for i in $(seq 1 5); do curl -s localhost:8080/api/info | grep -o '"hostname":"[^"]*"'; done
```
Zawsze ten sam `hostname`! Port-forward do Service'u wybiera **jeden** Pod przy starcie tunelu.
Load balancing widzieliśmy w etapie 5 (curl z wnętrza klastra) i zobaczymy w etapie 7 (Ingress).

## Debugowanie – ściąga
```bash
kubectl get pods -n poke                       # STATUS, RESTARTS
kubectl describe pod <pod> -n poke             # Events na dole!
kubectl logs <pod> -n poke --previous          # logi poprzedniego (crashniętego) kontenera
kubectl get events -n poke --sort-by=.lastTimestamp
```
