#!/usr/bin/env bash
set -euo pipefail

# Script de lancement complet (build + démarrage des serveurs + client via l'Agence)
# Architecture: TCP + GraphQL (Migration gRPC → GraphQL terminée)
# Emplacement: racine du projet HotelGraphQL

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
LOG_DIR="$ROOT_DIR/logs"
mkdir -p "$LOG_DIR"

# Helper
info(){ echo "[INFO] $*"; }
err(){ echo "[ERR] $*" >&2; }

# Parse options
NO_CLIENT=false
NO_GUI=false
ARRET_PROPRE=false
while [[ $# -gt 0 ]]; do
  case "$1" in
    --no-client) NO_CLIENT=true; shift ;;
    --no-gui) NO_GUI=true; shift ;;
    --arret-propre) ARRET_PROPRE=true; shift ;;
    -h|--help)
      echo "Usage: $0 [--no-client] [--no-gui] [--arret-propre]"
      echo ""
      echo "Options:"
      echo "  (défaut)       Lance les serveurs + interface graphique (GUI)"
      echo "  --no-gui       Lance les serveurs + client en ligne de commande (CLI)"
      echo "  --no-client    Lance uniquement les serveurs (pas de client)"
      echo "  --arret-propre Arrête proprement les serveurs à la fin"
      echo ""
      echo "Exemples:"
      echo "  $0                    # GUI par défaut"
      echo "  $0 --no-gui           # Client CLI"
      echo "  $0 --no-client        # Serveurs uniquement"
      exit 0
      ;;
    *) echo "Unknown arg: $1"; exit 1 ;;
  esac
done

# 1) Build
info "Compilation et packaging (maven)..."
mvn -DskipTests=true clean install

# 2) Stopper d'anciens processus (libérer les ports)
info "Libération des ports TCP (7070-7071), GraphQL (8082, 8084)..."
fuser -k 7070/tcp 2>/dev/null || true  # TCP Agency 1
fuser -k 7071/tcp 2>/dev/null || true  # TCP Agency 2
fuser -k 8082/tcp 2>/dev/null || true  # GraphQL Opera
fuser -k 8084/tcp 2>/dev/null || true  # GraphQL Rivage

# utilities pour arrêter les serveurs
cleanup(){
  info "Arrêt des serveurs démarrés (si présents)..."
  if [ -f /tmp/rivage.pid ]; then
    pid=$(cat /tmp/rivage.pid 2>/dev/null || true)
    [ -n "$pid" ] && kill "$pid" 2>/dev/null || true
    rm -f /tmp/rivage.pid || true
    info "rivage stopped"
  fi
  if [ -f /tmp/opera.pid ]; then
    pid=$(cat /tmp/opera.pid 2>/dev/null || true)
    [ -n "$pid" ] && kill "$pid" 2>/dev/null || true
    rm -f /tmp/opera.pid || true
    info "opera stopped"
  fi
  if [ -f /tmp/agency.pid ]; then
    pid=$(cat /tmp/agency.pid 2>/dev/null || true)
    [ -n "$pid" ] && kill "$pid" 2>/dev/null || true
    rm -f /tmp/agency.pid || true
    info "agency stopped"
  fi
  if [ -f /tmp/agency2.pid ]; then
    pid=$(cat /tmp/agency2.pid 2>/dev/null || true)
    [ -n "$pid" ] && kill "$pid" 2>/dev/null || true
    rm -f /tmp/agency2.pid || true
    info "agency2 stopped"
  fi
}

# Attendre qu'un port GraphQL soit disponible
wait_graphql_port(){
  local port=$1
  local name=$2
  local timeout=${3:-90}
  local start=$(date +%s)
  info "Attente $name GraphQL (port $port) (timeout ${timeout}s)..."
  while true; do
    if nc -z localhost $port 2>/dev/null; then
      info "$name GraphQL disponible (port $port)"
      return 0
    fi
    now=$(date +%s)
    if (( now - start >= timeout )); then
      err "Timeout waiting for $name GraphQL"
      return 1
    fi
    sleep 1
  done
}

# Attendre que l'agence TCP soit disponible
wait_agency_tcp(){
  local port=$1
  local name=$2
  local timeout=${3:-90}
  local start=$(date +%s)
  info "Attente $name TCP (port $port) (timeout ${timeout}s)..."
  while true; do
    if echo '{"op":"ping"}' | nc -w 2 localhost $port 2>/dev/null | grep -q "pong"; then
      info "$name TCP disponible"
      return 0
    fi
    now=$(date +%s)
    if (( now - start >= timeout )); then
      err "Timeout waiting for $name TCP"
      return 1
    fi
    sleep 1
  done
}

# Tester un service GraphQL avec curl
test_graphql_service(){
  local port=$1
  local name=$2
  info "Test du service GraphQL $name avec curl..."
  local query='{"query":"query { ping(message: \"test\") { message } }"}'
  if curl -sSf --max-time 3 -X POST "http://localhost:$port/graphql" \
       -H "Content-Type: application/json" \
       -d "$query" &> /dev/null; then
    info "✓ Service GraphQL $name opérationnel"
    return 0
  else
    info "⚠ Service GraphQL $name : pas de réponse (peut être en cours de démarrage)"
    return 1
  fi
}

# 3) Démarrer les serveurs en arrière-plan
start_server(){
  local module="$1"
  local logfile="$2"
  local pidfile="$3"
  info "Démarrage de $module -> $logfile"
  cd "$ROOT_DIR/$module"
  mvn -DskipTests=true spring-boot:run > "$logfile" 2>&1 &
  local pid=$!
  echo "$pid" > "$pidfile"
  cd "$ROOT_DIR"
  info "$module démarré (pid=$pid)"
}

# ⭐ Démarrer d'abord les hôtels (serveurs GraphQL)
start_server server-opera  "$LOG_DIR/opera.log"  /tmp/opera.pid
start_server server-rivage "$LOG_DIR/rivage.log" /tmp/rivage.pid

# ⭐ Attendre que les serveurs GraphQL des hôtels soient vraiment prêts
info "⏳ Attente que les serveurs GraphQL soient opérationnels..."
info "   Vérification de Server Opera (port 8082)..."
wait_graphql_port 8082 "Server Opera" 90 || err "Server Opera non disponible"
info "   Vérification de Server Rivage (port 8084)..."
wait_graphql_port 8084 "Server Rivage" 90 || err "Server Rivage non disponible"

# Attendre quelques secondes supplémentaires pour que GraphQL soit complètement initialisé
info "⏳ Attente finale de 5 secondes pour initialisation complète..."
sleep 5

# ⭐ Maintenant démarrer les agences (qui vont se connecter aux hôtels via GraphQL)
info "🚀 Démarrage des agences (qui se connectent aux serveurs GraphQL)..."
start_server agency-server  "$LOG_DIR/agency.log" /tmp/agency.pid
start_server agency-server-2  "$LOG_DIR/agency2.log" /tmp/agency2.pid

# Attendre un peu que les agences démarrent
info "⏳ Attente démarrage des agences (5 secondes)..."
sleep 5

# Si demande d'arret propre, installer le trap
if [ "$ARRET_PROPRE" = true ]; then
  trap cleanup EXIT
  info "Option --arret-propre activée : les serveurs seront arrêtés proprement à la fin du script"
fi

# 4) Vérifier que les services TCP sont disponibles
info ""
info "═══════════════════════════════════════════════════════════"
info "Vérification des services TCP..."
info "═══════════════════════════════════════════════════════════"
wait_agency_tcp 7070 "Agency Server 1" 60 || true
wait_agency_tcp 7071 "Agency Server 2" 60 || true

# 5) Vérifier les services GraphQL
info ""
info "═══════════════════════════════════════════════════════════"
info "Vérification des services GraphQL..."
info "═══════════════════════════════════════════════════════════"
wait_graphql_port 8082 "Server Opera" 90 || true
wait_graphql_port 8084 "Server Rivage" 90 || true

# Tester les services GraphQL
sleep 3
test_graphql_service 8082 "Opera" || true
test_graphql_service 8084 "Rivage" || true

info ""
info "═══════════════════════════════════════════════════════════"
info "✓ Tous les serveurs sont démarrés"
info "═══════════════════════════════════════════════════════════"
info ""
info "Architecture TCP + GraphQL active:"
info "  • TCP       : ports 7070-7071 (client → agency)"
info "  • GraphQL   : port 8082 (Server Opera + GraphiQL)"
info "  • GraphQL   : port 8084 (Server Rivage + GraphiQL)"
info ""
info "Logs disponibles dans: $LOG_DIR/"
info "  - opera.log   : Server Opera (GraphQL 8082)"
info "  - rivage.log  : Server Rivage (GraphQL 8084)"
info "  - agency.log  : Agency Server 1 (TCP 7070 + GraphQL client)"
info "  - agency2.log : Agency Server 2 (TCP 7071)"
info ""
info "Interfaces GraphQL disponibles:"
info "  • GraphiQL Opera  : http://localhost:8082/graphiql"
info "  • GraphiQL Rivage : http://localhost:8084/graphiql"
info ""
info "Note: Migration gRPC → GraphQL terminée avec succès!"
info ""

# 5) Lancer le client (au premier plan) sauf si --no-client
if [ "$NO_CLIENT" = true ]; then
  info "--no-client : les serveurs ont été démarrés et aucun client n'est lancé."
  if [ "$ARRET_PROPRE" = true ]; then
    info "Appuyez sur Ctrl-C pour arrêter les serveurs proprement."
    # garder le script en vie pour permettre Ctrl-C
    while true; do sleep 3600; done
  else
    info "Les serveurs restent en arrière-plan. (PIDs: $(cat /tmp/rivage.pid 2>/dev/null || echo "-") $(cat /tmp/opera.pid 2>/dev/null || echo "-") $(cat /tmp/agency.pid 2>/dev/null || echo "-"))"
    exit 0
  fi
elif [ "$NO_GUI" = true ]; then
  info "Lancement du client CLI (ligne de commande) au premier plan, pour quitter: Ctrl-C"
  cd "$ROOT_DIR/client-cli"
  mvn -DskipTests=true exec:java \
    -Dexec.mainClass=org.examples.client.ClientMain \
    -Dagency.tcp.enabled=true
else
  info "Lancement de l'interface graphique (GUI) au premier plan..."
  info "Si l'interface ne s'affiche pas, attendez quelques secondes pour la connexion à l'agence."
  cd "$ROOT_DIR/client-cli"
  mvn -DskipTests=true exec:java \
    -Dexec.mainClass=org.examples.client.gui.HotelClientGUI \
    -Dagency.tcp.host=localhost \
    -Dagency.tcp.port=7070
fi

# Si on atteint ici, le client s'est terminé
info "Client terminé, script fini."

# Si --arret-propre était activé, le trap cleanup sera exécuté automatiquement à la sortie
