# Etap 5 – Service i wystawienie portu

## Problem
Pody są efemeryczne: każdy restart = nowe IP. Jak inne aplikacje mają się z nimi łączyć?

## Rozwiązanie: Service
```
             Service "poke" (ClusterIP 10.43.x.x:80, DNS: poke.poke.svc)
                     │  selector: app.kubernetes.io/name=poke
          ┌──────────┴──────────┐
     Pod poke-...-a:8080   Pod poke-...-b:8080     (tylko Pody Ready!)
```
`port` (80) – na czym słucha Service; `targetPort` (`http` → 8080) – port w kontenerze.

## Kroki
```bash
kubectl apply -f k8s/service.yaml

kubectl get svc -n poke
kubectl get endpointslices -n poke -o wide     # IP Podów za Service'em
kubectl get pods -n poke -o wide               # porównaj IP
kubectl describe svc poke -n poke
```

## Test z wnętrza klastra (Pod tymczasowy)
```bash
kubectl run curl -n poke --rm -it --restart=Never --image=curlimages/curl -- \
  sh -c 'for i in $(seq 1 6); do curl -s http://poke/api/info; echo; done'
```
→ pole `hostname` się zmienia = **load balancing** między replikami.

## Eksperymenty
- `kubectl scale deploy/poke -n poke --replicas=4` i obserwuj endpointy:
  `kubectl get endpointslices -n poke -w`
- Zmień `selector` na nieistniejącą etykietę → Service bez endpointów → `Connection refused`.
- Typ `NodePort`: `kubectl patch svc poke -n poke -p '{"spec":{"type":"NodePort"}}'`, potem `kubectl get svc -n poke`.
