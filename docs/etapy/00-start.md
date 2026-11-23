# Etap 0 – punkt startowy

## Cel
Poznać aplikację i przygotować środowisko.

## Kroki
1. Uruchom aplikację lokalnie: `./gradlew bootRun` → http://localhost:8080
2. Sprawdź endpointy:
   ```bash
   curl localhost:8080/api/pokemon/pikachu
   curl localhost:8080/api/info
   curl localhost:8080/actuator/health/liveness
   curl localhost:8080/actuator/health/readiness
   ```
   Swagger UI: http://localhost:8080/swagger-ui.html
3. Postaw klaster:
   ```bash
   k3d cluster create workshop -p "8081:80@loadbalancer" --agents 1
   kubectl cluster-info
   kubectl get nodes -o wide
   ```

## Dlaczego te zmiany w aplikacji?
Kubernetes musi wiedzieć, czy kontener **żyje** (liveness) i czy jest **gotowy na ruch**
(readiness). Spring Boot Actuator wystawia oba stany. Konfigurację czytamy ze zmiennych
środowiskowych – zgodnie z zasadą *12-factor app* ten sam obraz działa w każdym środowisku.
