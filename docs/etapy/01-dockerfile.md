# Etap 1 – Dockerfile: obraz kontenera

## Cel
Zbudować obraz Docker aplikacji, który Kubernetes uruchomi w Podzie.

> Kubernetes nie uruchamia "aplikacji" ani "jarów" – uruchamia **kontenery z obrazów**.
> Pod = najmniejsza jednostka w K8s = 1..N kontenerów współdzielących sieć i wolumeny.

## Pliki
- `Dockerfile` – build wieloetapowy: `frontend` (Node) → `backend` (JDK+Gradle) → `runtime` (JRE)
- `.dockerignore` – co NIE trafia do kontekstu builda

## Kroki
```bash
docker build -t poke:1.0.0 .
docker images poke

# test lokalny kontenera
docker run --rm -p 8080:8080 -e APP_ENVIRONMENT=docker poke:1.0.0
curl localhost:8080/api/info
```

Załaduj obraz do klastra (k3d nie widzi lokalnych obrazów Dockera):
```bash
k3d image import poke:1.0.0 -c workshop
# kind:      kind load docker-image poke:1.0.0
# minikube:  minikube image load poke:1.0.0
# Docker Desktop: obraz jest widoczny od razu
```

## Dobre praktyki widoczne w Dockerfile
- **multi-stage** – w obrazie końcowym nie ma Node, Gradle ani JDK (mniejszy obraz, mniejsza powierzchnia ataku)
- **kolejność warstw** – najpierw zależności, potem kod → szybkie przebudowy
- **non-root** (`USER 1000`) – wymagane przez `runAsNonRoot` w etapie 2
- **`MaxRAMPercentage`** – JVM dopasowuje heap do limitu pamięci kontenera
- **konkretny tag** (`1.0.0`), nie `latest` – powtarzalne wdrożenia

## Zadania dodatkowe
- `docker history poke:1.0.0` – zobacz warstwy
- Zmień plik w `src/` i przebuduj – które warstwy zostały z cache?
