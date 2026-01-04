#!/usr/bin/env bash
set -euo pipefail

# start_all_and_generate.sh
# Démarre les serveurs Rivage et Opera, attend les WSDL, génère les stubs client et lance le client CLI.
# Usage: bash scripts/start_all_and_generate.sh

BASE_DIR=$(cd "$(dirname "$0")/.." && pwd)
cd "$BASE_DIR"

RIVAGE_MODULE=server-rivage
OPERA_MODULE=server-opera
CLIENT_MODULE=client-cli

RIVAGE_JAR=$BASE_DIR/$RIVAGE_MODULE/target/hotel-server-soap-rivage-1.0.0.jar
OPERA_JAR=$BASE_DIR/$OPERA_MODULE/target/hotel-server-soap-opera-1.0.0.jar

RIVAGE_PORT=8081
OPERA_PORT=8082
RIVAGE_PATH=/hotel-rivage
OPERA_PATH=/hotel-opera

RIVAGE_WSDL="http://localhost:${RIVAGE_PORT}${RIVAGE_PATH}?wsdl"
OPERA_WSDL="http://localhost:${OPERA_PORT}${OPERA_PATH}?wsdl"

LOGDIR=$BASE_DIR/logs
mkdir -p "$LOGDIR"

PIDFILE_DIR=/tmp/hotel_soap_pids
mkdir -p "$PIDFILE_DIR"

start_server() {
  local name="$1"; shift
  local jar="$1"; shift
  local module="$1"; shift
  local log="$1"; shift
  local pidfile="$1"; shift

  # If jar exists, check manifest for Main-Class or Start-Class
  if [ -f "$jar" ]; then
    if unzip -p "$jar" META-INF/MANIFEST.MF 2>/dev/null | grep -E "Main-Class:|Start-Class:" >/dev/null; then
      echo "Starting $name from jar: $jar"
      nohup java -jar "$jar" >"$log" 2>&1 &
      echo $! >"$pidfile"
    else
      echo "Jar $jar missing Main-Class/Start-Class, running via reactor mvnw for module $module"
      nohup ./mvnw -am -pl "$module" -DskipTests=true spring-boot:run >"$log" 2>&1 &
      echo $! >"$pidfile"
    fi
  else
    echo "Jar not found for $name, running via reactor mvnw for module $module"
    nohup ./mvnw -am -pl "$module" -DskipTests=true spring-boot:run >"$log" 2>&1 &
    echo $! >"$pidfile"
  fi

  echo "$name started (pid=$(cat $pidfile)), log=$log"
}

wait_for_wsdl() {
  local url="$1"; shift
  local timeout=${1:-60}; shift
  echo "Waiting for WSDL $url (timeout ${timeout}s)..."
  local start=$(date +%s)
  while true; do
    if command -v curl >/dev/null 2>&1; then
      if curl -sSf "$url" >/dev/null 2>&1; then
        echo "WSDL available: $url"
        return 0
      fi
    else
      # use wget if curl absent
      if command -v wget >/dev/null 2>&1; then
        if wget -qO- "$url" >/dev/null 2>&1; then
          echo "WSDL available: $url"
          return 0
        fi
      fi
    fi
    now=$(date +%s)
    if [ $((now-start)) -ge $timeout ]; then
      echo "Timeout waiting for $url" >&2
      return 1
    fi
    sleep 1
  done
}

# Ensure server-base (and other modules) are installed to local repo so spring-boot:run picks latest jars
echo "Installing server-base to local repository (so spring-boot:run picks updated classes)"
./mvnw -pl server-base -DskipTests=true install

# Start servers
RIVAGE_LOG="$LOGDIR/rivage.log"
OPERA_LOG="$LOGDIR/opera.log"
RIVAGE_PIDFILE="$PIDFILE_DIR/rivage.pid"
OPERA_PIDFILE="$PIDFILE_DIR/opera.pid"

start_server "Rivage" "$RIVAGE_JAR" "$RIVAGE_MODULE" "$RIVAGE_LOG" "$RIVAGE_PIDFILE"
start_server "Opera" "$OPERA_JAR" "$OPERA_MODULE" "$OPERA_LOG" "$OPERA_PIDFILE"

# Wait for WSDLs
wait_for_wsdl "$RIVAGE_WSDL" 90 || { echo "Rivage WSDL not available, check $RIVAGE_LOG"; exit 1; }
wait_for_wsdl "$OPERA_WSDL" 90 || { echo "Opera WSDL not available, check $OPERA_LOG"; exit 1; }

# Generate stubs for Rivage (example). You can change to Opera if desired.
echo "Generating client stubs from $RIVAGE_WSDL"
./mvnw -pl "$CLIENT_MODULE" -Pgenerate-stubs -Dgenerate.stubs=true -Dwsdl.url="$RIVAGE_WSDL" jaxws:wsimport

# Build client
./mvnw -pl "$CLIENT_MODULE" -DskipTests=true clean package

# Run client
echo "Launching client CLI (it will connect to $RIVAGE_WSDL)"
./mvnw -pl "$CLIENT_MODULE" -DskipTests=true exec:java -Dwsdl.url="$RIVAGE_WSDL"

# End
echo "Done. Server logs: $RIVAGE_LOG $OPERA_LOG"

echo "PIDs stored in: $PIDFILE_DIR"

exit 0
