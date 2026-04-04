#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "${ROOT_DIR}"

CONTAINER_NAME="${SQLSERVER_CONTAINER:-sqlserver2022-local}"
SUDO_PASSWORD="${SUDO_PASSWORD:-quan0511}"

export DB_HOST="${DB_HOST:-127.0.0.1}"
export DB_PORT="${DB_PORT:-1433}"
export DB_NAME="${DB_NAME:-HotelManagement}"
export DB_USER="${DB_USER:-sa}"
export DB_PASSWORD="${DB_PASSWORD:-${MSSQL_SA_PASSWORD:-Quan@0511}}"

run_with_sudo_if_needed() {
  if "$@"; then
    return 0
  fi
  if command -v sudo >/dev/null 2>&1; then
    printf '%s\n' "${SUDO_PASSWORD}" | sudo -S "$@"
    return 0
  fi
  return 1
}

docker_cmd() {
  run_with_sudo_if_needed docker "$@"
}

compose_up() {
  if docker compose version >/dev/null 2>&1; then
    run_with_sudo_if_needed docker compose up -d sqlserver
  elif command -v docker-compose >/dev/null 2>&1; then
    run_with_sudo_if_needed docker-compose up -d sqlserver
  else
    echo "Neither 'docker compose' nor 'docker-compose' is available." >&2
    return 1
  fi
}

if command -v docker >/dev/null 2>&1; then
  if docker_cmd ps -a --format '{{.Names}}' | grep -Fxq "${CONTAINER_NAME}"; then
    docker_cmd start "${CONTAINER_NAME}" >/dev/null 2>&1 || true
  else
    compose_up
  fi

  until docker_cmd exec "${CONTAINER_NAME}" /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -C -Q "SELECT 1" >/dev/null 2>&1; do
    echo "Waiting for SQL Server container..."
    sleep 2
  done

  cat "db/schema.sql" | docker_cmd exec -i "${CONTAINER_NAME}" /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "${DB_PASSWORD}" -C >/dev/null
  echo "Seeded 15-line baseline data via db/schema.sql (Docker)."
  exit 0
fi

if command -v sqlcmd >/dev/null 2>&1; then
  sqlcmd -S "${DB_HOST},${DB_PORT}" -U "${DB_USER}" -P "${DB_PASSWORD}" -C -i db/schema.sql >/dev/null
  echo "Seeded 15-line baseline data via db/schema.sql (Local SQL Server)."
  exit 0
fi

echo "No database client found: docker/sqlcmd unavailable." >&2
exit 1
