# Etap 4 – Secret

## Cel
Przekazać do aplikacji dane wrażliwe (token API) oddzielnie od zwykłej konfiguracji.

## ConfigMap vs Secret
| | ConfigMap | Secret |
|---|---|---|
| przeznaczenie | konfiguracja jawna | hasła, tokeny, certyfikaty |
| przechowywanie | plain text | base64 (+ opcjonalnie szyfrowanie at-rest w etcd) |
| RBAC | zwykle szeroki odczyt | dostęp ograniczany osobno |
| montowanie jako plik | dysk | `tmpfs` (RAM) |
| typy | – | `Opaque`, `kubernetes.io/tls`, `kubernetes.io/dockerconfigjson`, ... |

> **base64 ≠ szyfrowanie.** Kto ma `get secret`, ten zna wartość.

## Kroki
```bash
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml

kubectl get secret -n poke
kubectl describe secret poke-secret -n poke          # pokazuje tylko rozmiar
kubectl get secret poke-secret -n poke -o jsonpath='{.data.APP_API_TOKEN}' | base64 -d; echo

kubectl exec -n poke deploy/poke -- wget -qO- localhost:8080/api/info
# "apiTokenConfigured": true, "apiTokenMasked": "pi****"
```

## Lepsza praktyka – sekret tworzony poza repo
```bash
kubectl create secret generic poke-secret -n poke \
  --from-literal=APP_API_TOKEN="$(openssl rand -hex 16)" \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl rollout restart deploy/poke -n poke
```

## Zadania dodatkowe
- Zamontuj Secret jako plik (`volumes.secret`) i sprawdź `kubectl exec ... -- mount | grep secret` (tmpfs).
- Co się stanie, gdy Secret nie istnieje? Usuń go i zrób `rollout restart` → `CreateContainerConfigError`.
