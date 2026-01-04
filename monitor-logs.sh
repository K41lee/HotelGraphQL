#!/bin/bash

# Script pour surveiller les logs en temps réel pendant les tests

echo "╔════════════════════════════════════════════════════════════╗"
echo "║  Surveillance des Logs en Temps Réel                      ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "Ce script affiche les logs des 3 serveurs en temps réel"
echo "avec coloration pour identifier facilement les flux de données"
echo ""
echo "Appuyez sur Ctrl+C pour arrêter la surveillance"
echo ""
sleep 2

cd /home/etudiant/Bureau/gRPC/HotelgRPC

# Vérifier que les logs existent
if [ ! -f logs/agency.log ] || [ ! -f logs/opera.log ] || [ ! -f logs/rivage.log ]; then
    echo "❌ Erreur: Les serveurs ne sont pas démarrés"
    echo "   Lancez d'abord: ./start-servers-background.sh"
    exit 1
fi

# Surveiller les logs avec tail -f en parallèle
tail -f logs/agency.log | sed 's/^/[AGENCY]  /' &
PID1=$!

tail -f logs/opera.log | sed 's/^/[OPERA]   /' &
PID2=$!

tail -f logs/rivage.log | sed 's/^/[RIVAGE]  /' &
PID3=$!

# Attendre que l'utilisateur arrête avec Ctrl+C
trap "kill $PID1 $PID2 $PID3 2>/dev/null; echo ''; echo 'Surveillance arrêtée'; exit 0" INT

wait

