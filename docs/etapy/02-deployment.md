# Etap 2 – Deployment

## Cel
Uruchomić aplikację w klastrze w 2 replikach, z automatycznym restartem i rolling update.

## Hierarchia obiektów
```
Deployment  (deklaracja: "chcę 2 Pody z obrazem poke:1.0.0")
  └── ReplicaSet  (pilnuje liczby Podów dla danej wersji szablonu)
        ├── Pod poke-7d9c...-abcde
        └── Pod poke-7d9c...-fghij
```

## Anatomia manifestu
Każdy manifest ma 4 sekcje: `apiVersion`, `kind`, `metadata`, `spec`.
- `spec.selector.matchLabels` musi pasować do `spec.template.metadata.labels`
- `resources.requests` – ile scheduler rezerwuje; `limits` – twardy limit (OOMKilled po przekroczeniu pamięci)
- probe'y: `startupProbe` (start JVM), `livenessProbe` (restart), `readinessProbe` (ruch z Service)

## Kroki
```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/deployment.yaml

kubectl get deploy,rs,pods -n poke
kubectl get pods -n poke -w                 # obserwuj start (Ctrl+C)
kubectl describe pod -n poke -l app.kubernetes.io/name=poke
kubectl logs -n poke deploy/poke -f
```

## Eksperymenty
```bash
# Samonaprawianie: usuń Pod – ReplicaSet od razu tworzy nowy
kubectl delete pod -n poke -l app.kubernetes.io/name=poke --wait=false
kubectl get pods -n poke -w

# Skalowanie
kubectl scale deploy/poke -n poke --replicas=3

# Rolling update i rollback
kubectl set image deploy/poke -n poke poke=poke:nie-istnieje
kubectl rollout status deploy/poke -n poke      # zawiśnie – ImagePullBackOff
kubectl get pods -n poke                        # stare Pody nadal działają (maxUnavailable: 0)
kubectl rollout undo deploy/poke -n poke
kubectl rollout history deploy/poke -n poke

# Wejście do kontenera
kubectl exec -it -n poke deploy/poke -- sh
```

> Uwaga: `kubectl scale` / `set image` zmieniają stan "imperatywnie". Źródłem prawdy
> powinien być plik YAML w repo – kolejne `kubectl apply` przywróci wartości z pliku.

## Na razie aplikacja jest niedostępna z zewnątrz – to naprawimy w etapach 5–7.
