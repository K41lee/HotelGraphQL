#!/bin/bash
# Script de réinitialisation des bases de données H2

echo "=============================================="
echo "  RÉINITIALISATION DES BASES DE DONNÉES H2"
echo "=============================================="
echo ""

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Vérifier si les serveurs sont en cours d'exécution
RUNNING_SERVERS=$(ps aux | grep -E "spring-boot:run" | grep -v grep | wc -l)

if [ "$RUNNING_SERVERS" -gt 0 ]; then
    echo -e "${YELLOW}⚠ ATTENTION${NC} : Des serveurs sont en cours d'exécution"
    echo ""
    echo "Serveurs actifs détectés :"
    ps aux | grep -E "spring-boot:run" | grep -v grep | awk '{print "  •", $11, $12, $13}'
    echo ""
    read -p "Voulez-vous arrêter les serveurs et continuer ? (o/N) " -n 1 -r
    echo ""
    if [[ $REPLY =~ ^[Oo]$ ]]; then
        echo -e "${BLUE}[1/5]${NC} Arrêt des serveurs..."
        pkill -9 -f "spring-boot:run"
        pkill -9 -f "mvn"
        sleep 2
        echo -e "${GREEN}✓${NC} Serveurs arrêtés"
    else
        echo -e "${RED}✗${NC} Opération annulée"
        exit 1
    fi
else
    echo -e "${GREEN}✓${NC} Aucun serveur en cours d'exécution"
fi

echo ""
echo -e "${BLUE}[2/5]${NC} Sauvegarde des bases de données actuelles..."

# Créer un dossier de sauvegarde avec timestamp
BACKUP_DIR="backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p "$BACKUP_DIR"

# Sauvegarder Opera
if [ -f "server-opera/data/hotel-opera-db.mv.db" ]; then
    cp server-opera/data/hotel-opera-db.mv.db "$BACKUP_DIR/"
    OPERA_SIZE=$(du -h server-opera/data/hotel-opera-db.mv.db | cut -f1)
    echo -e "${GREEN}✓${NC} Opera sauvegardé ($OPERA_SIZE) → $BACKUP_DIR/"
else
    echo -e "${YELLOW}⚠${NC} Base Opera introuvable (création à venir)"
fi

# Sauvegarder Rivage
if [ -f "server-rivage/data/hotel-rivage-db.mv.db" ]; then
    cp server-rivage/data/hotel-rivage-db.mv.db "$BACKUP_DIR/"
    RIVAGE_SIZE=$(du -h server-rivage/data/hotel-rivage-db.mv.db | cut -f1)
    echo -e "${GREEN}✓${NC} Rivage sauvegardé ($RIVAGE_SIZE) → $BACKUP_DIR/"
else
    echo -e "${YELLOW}⚠${NC} Base Rivage introuvable (création à venir)"
fi

echo ""
echo -e "${BLUE}[3/5]${NC} Suppression des bases de données..."

# Supprimer les fichiers de base Opera
if ls server-opera/data/hotel-opera-db* 1> /dev/null 2>&1; then
    rm -f server-opera/data/hotel-opera-db*
    echo -e "${GREEN}✓${NC} Base Opera supprimée"
else
    echo -e "${YELLOW}⚠${NC} Base Opera déjà absente"
fi

# Supprimer les fichiers de base Rivage
if ls server-rivage/data/hotel-rivage-db* 1> /dev/null 2>&1; then
    rm -f server-rivage/data/hotel-rivage-db*
    echo -e "${GREEN}✓${NC} Base Rivage supprimée"
else
    echo -e "${YELLOW}⚠${NC} Base Rivage déjà absente"
fi

echo ""
echo -e "${BLUE}[4/5]${NC} Vérification de la suppression..."

OPERA_EXISTS=$(ls server-opera/data/hotel-opera-db* 2>/dev/null | wc -l)
RIVAGE_EXISTS=$(ls server-rivage/data/hotel-rivage-db* 2>/dev/null | wc -l)

if [ "$OPERA_EXISTS" -eq 0 ] && [ "$RIVAGE_EXISTS" -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Toutes les bases ont été supprimées avec succès"
else
    echo -e "${RED}✗${NC} Erreur : Certains fichiers n'ont pas été supprimés"
    exit 1
fi

echo ""
echo -e "${BLUE}[5/5]${NC} Redémarrage des serveurs (pour recréer les bases)..."
echo ""

read -p "Voulez-vous redémarrer les serveurs maintenant ? (o/N) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Oo]$ ]]; then
    echo "Démarrage des serveurs..."
    echo ""

    # Démarrer Rivage
    echo "→ Démarrage Server Rivage (port 8082)..."
    cd server-rivage
    nohup mvn spring-boot:run > ../logs/rivage.log 2>&1 &
    cd ..
    sleep 2

    # Démarrer Opera
    echo "→ Démarrage Server Opera (port 8084)..."
    cd server-opera
    nohup mvn spring-boot:run > ../logs/opera.log 2>&1 &
    cd ..
    sleep 2

    # Démarrer Agency
    echo "→ Démarrage Agency Server (port 7070)..."
    cd agency-server
    nohup mvn spring-boot:run > ../logs/agency.log 2>&1 &
    cd ..

    echo ""
    echo "Attente du démarrage des serveurs (15 secondes)..."
    sleep 15

    # Vérifier que les serveurs sont démarrés
    OPERA_PING=$(curl -s http://localhost:8084/api/ping 2>/dev/null)
    RIVAGE_PING=$(curl -s http://localhost:8082/api/ping 2>/dev/null)

    echo ""
    if [ "$OPERA_PING" = "pong" ]; then
        echo -e "${GREEN}✓${NC} Server Opera démarré et opérationnel"
    else
        echo -e "${YELLOW}⚠${NC} Server Opera en cours de démarrage..."
    fi

    if [ "$RIVAGE_PING" = "pong" ]; then
        echo -e "${GREEN}✓${NC} Server Rivage démarré et opérationnel"
    else
        echo -e "${YELLOW}⚠${NC} Server Rivage en cours de démarrage..."
    fi

    echo ""
    echo "Les bases de données seront automatiquement recréées par Hibernate (ddl-auto=update)"

else
    echo "Serveurs non redémarrés. Vous pouvez les démarrer manuellement avec :"
    echo "  ./lancement.sh"
fi

echo ""
echo "=============================================="
echo "  RÉSUMÉ"
echo "=============================================="
echo ""
echo "Bases de données réinitialisées :"
echo "  • Opera  : server-opera/data/hotel-opera-db.mv.db"
echo "  • Rivage : server-rivage/data/hotel-rivage-db.mv.db"
echo ""
echo "Sauvegarde créée dans :"
echo "  → $BACKUP_DIR/"
echo ""
echo "État des bases :"
echo "  • Anciennes bases : Supprimées ✓"
echo "  • Nouvelles bases : Seront créées au démarrage"
echo ""
echo "Pour voir les nouvelles bases :"
echo "  • Opera  : http://localhost:8084/h2-console"
echo "  • Rivage : http://localhost:8082/h2-console"
echo ""
echo "Pour restaurer une sauvegarde :"
echo "  1. Arrêter les serveurs : pkill -f 'spring-boot:run'"
echo "  2. Copier : cp $BACKUP_DIR/*.mv.db server-*/data/"
echo "  3. Redémarrer : ./lancement.sh"
echo ""
echo "=============================================="

