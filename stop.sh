#!/usr/bin/env bash
# ---------------------------------------------------------------------------
# stop.sh — Gracefully stop and remove the TrainOrder Docker container
#
# Usage:
#   ./stop.sh           # stops the default container
#   ./stop.sh myname    # stops a container with a custom name
#
# The H2 data files in ./data/ are NOT touched; they remain on the host.
# ---------------------------------------------------------------------------
set -euo pipefail

CONTAINER_NAME="${1:-trainorder-app}"

if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "==> Stopping container: $CONTAINER_NAME"
    docker stop "$CONTAINER_NAME"
    echo "==> Removing container: $CONTAINER_NAME"
    docker rm   "$CONTAINER_NAME"
    echo "✓ Container stopped and removed. Data files in ./data/ are preserved."
elif docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "==> Container $CONTAINER_NAME is already stopped. Removing..."
    docker rm "$CONTAINER_NAME"
    echo "✓ Container removed."
else
    echo "No container named '$CONTAINER_NAME' found."
fi

