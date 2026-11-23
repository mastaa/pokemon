# Etap 3 – ConfigMap

## Cel
Wyciągnąć konfigurację z obrazu. Obraz jest niezmienny, konfiguracja zależy od środowiska.

## Dwa sposoby użycia ConfigMap
| Sposób | W Deployment | Kiedy |
|--------|--------------|-------|
| zmienne środowiskowe | `envFrom.configMapRef` / `env.valueFrom.configMapKeyRef` | proste wartości |
| plik w wolumenie | `volumes.configMap` + `volumeMounts` | całe pliki konfiguracyjne |

## Kroki
```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml      # zmiana template -> rolling update

kubectl get cm -n poke
kubectl describe cm poke-config poke-config-files -n poke

# sprawdź zmienne i plik w kontenerze
kubectl exec -n poke deploy/poke -- env | grep -E 'APP_|POKEAPI|SPRING'
kubectl exec -n poke deploy/poke -- cat /config/application-k8s.yaml

# aplikacja widzi nową konfigurację
kubectl exec -n poke deploy/poke -- wget -qO- localhost:8080/api/info
```

## Ważne: zmiana ConfigMap ≠ restart Podów
- zmienne środowiskowe są czytane **tylko przy starcie** kontenera
- pliki z wolumenu aktualizują się same (po ~1 min), ale aplikacja musi je przeładować

```bash
kubectl edit cm poke-config -n poke        # zmień APP_WELCOME_MESSAGE
kubectl rollout restart deploy/poke -n poke
```

## Alternatywa imperatywna
```bash
kubectl create configmap poke-config -n poke \
  --from-literal=APP_ENVIRONMENT=kubernetes \
  --dry-run=client -o yaml          # wygeneruj YAML zamiast tworzyć
kubectl create configmap poke-config-files -n poke \
  --from-file=application-k8s.yaml --dry-run=client -o yaml
```
