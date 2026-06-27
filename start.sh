#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# start.sh — Build and launch TrainOrder inside Docker
#
# Usage:
#   ./start.sh              # uses default image tag and container name
#   ./start.sh myport       # override host port  (default: 8080)
#
# The H2 database is stored in ./data/ on the HOST so it survives
# container restarts and image rebuilds.
# ---------------------------------------------------------------------------
set -euo pipefail

IMAGE_NAME="trainorder"
IMAGE_TAG="latest"
CONTAINER_NAME="trainorder-app"
HOST_PORT="${1:-8080}"
CONTAINER_PORT="8080"

# Absolute path to the data directory on the host.
# Docker requires an absolute path for bind-mounts.
DATA_DIR="$(cd "$(dirname "$0")" && pwd)/data"

echo "==> Creating host data directory (if needed): $DATA_DIR"
mkdir -p "$DATA_DIR"

# ----- 1. Build the fat JAR (skip tests for speed) -------------------------
echo "==> Building JAR with Maven wrapper..."
./mvnw -q package -DskipTests

# ----- 2. Build the Docker image -------------------------------------------
echo "==> Building Docker image: $IMAGE_NAME:$IMAGE_TAG"
docker build -t "$IMAGE_NAME:$IMAGE_TAG" .

# ----- 3. Stop & remove any existing container with the same name ----------
if docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "==> Stopping and removing existing container: $CONTAINER_NAME"
    docker stop "$CONTAINER_NAME" >/dev/null 2>&1 || true
    docker rm   "$CONTAINER_NAME" >/dev/null 2>&1 || true
fi

# ----- 4. Run the container with the data directory bind-mounted -----------
echo "==> Starting container: $CONTAINER_NAME"
echo "    Host port  : $HOST_PORT"
echo "    Data volume: $DATA_DIR → /app/data"

docker run -d \
    --name "$CONTAINER_NAME" \
    -p "$HOST_PORT:$CONTAINER_PORT" \
    -v "$DATA_DIR:/app/data" \
    --restart unless-stopped \
    "$IMAGE_NAME:$IMAGE_TAG"

echo ""
echo "✓ TrainOrder is starting up."
echo "  App      : http://localhost:$HOST_PORT"
echo "  Health   : http://localhost:$HOST_PORT/actuator/health"
echo "  H2 console: http://localhost:$HOST_PORT/h2-console"
echo "  Logs     : docker logs -f $CONTAINER_NAME"
echo ""
echo "  Waiting for health check..."
for i in $(seq 1 30); do
    STATUS=$(curl -s "http://localhost:$HOST_PORT/actuator/health" 2>/dev/null | grep -o '"UP"' || true)
    if [ "$STATUS" = '"UP"' ]; then
        echo "  → Application is UP (attempt $i)"
        break
    fi
    sleep 2
done

