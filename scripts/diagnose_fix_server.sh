#!/usr/bin/env bash
set -euo pipefail

# diagnose_fix_server.sh
# Usage: bash scripts/diagnose_fix_server.sh
# Ce script :
# - affiche la JVM utilisée et mvn -v
# - recherche les fichiers .class compilés avec major version > 55 (Java >11)
# - recherche dans ~/.m2 les jars contenant org/examples/server/HotelServerApplication.class
# - supprime targets des modules serveurs et recompile server-base/server-rivage/server-opera
# - donne les commandes pour démarrer les serveurs

echo "== Environnement -> java & mvn =="
java -version || true
./mvnw -v || true

echo
echo "== Recherche des .class avec major version > 55 (Java >11) dans le workspace =="
# find all .class and print those whose major version > 55
python3 - <<'PY'
import os,sys
root='.'
found=False
for dirpath,_,files in os.walk(root):
    for f in files:
        if f.endswith('.class'):
            p=os.path.join(dirpath,f)
            try:
                with open(p,'rb') as fh:
                    hdr=fh.read(8)
                    if len(hdr)>=8:
                        maj=(hdr[6]<<8)|hdr[7]
                        if maj>55:
                            print(p, 'major='+str(maj))
                            found=True
            except Exception as e:
                pass
if not found:
    print('No class file > major 55 found in workspace')
PY

echo
echo "== Recherche de HotelServerApplication.class dans ~/.m2 (liste des jars qui le contiennent) =="
if [ -d "$HOME/.m2" ]; then
  count=0
  while IFS= read -r -d '' jar; do
    if unzip -l "$jar" 2>/dev/null | grep -q 'org/examples/server/HotelServerApplication.class'; then
      echo "FOUND in: $jar"
      count=$((count+1))
    fi
  done < <(find "$HOME/.m2" -name '*.jar' -print0)
  if [ "$count" -eq 0 ]; then
    echo "No jar in ~/.m2 contains HotelServerApplication.class"
  fi
else
  echo "No ~/.m2 directory found"
fi

echo
echo "== Etat targets serveurs (sizes) =="
for d in server-base server-rivage server-opera; do
  if [ -d "$d/target" ]; then
    echo "-- $d/target exists -> size:" $(du -sh "$d/target" 2>/dev/null || true)
  else
    echo "-- $d/target missing"
  fi
done

read -p "Supprimer les répertoires target de server-base, server-rivage, server-opera et recompiler ces modules ? (y/N) " answer
if [[ "$answer" != "y" && "$answer" != "Y" ]]; then
  echo "Abandon. Aucune modification effectuée."
  exit 0
fi

echo "Suppression des target..."
rm -rf server-base/target server-rivage/target server-opera/target

# Try to detect Java 11 home
JAVA11_CANDIDATES=("/usr/lib/jvm/java-11-openjdk-amd64" "/usr/lib/jvm/java-11-openjdk" "/usr/lib/jvm/java-11" )
USE_JAVA_HOME=""
for c in "${JAVA11_CANDIDATES[@]}"; do
  if [ -d "$c" ]; then
    USE_JAVA_HOME="$c"
    break
  fi
done
if [ -z "${JAVA_HOME:-}" ] && [ -n "$USE_JAVA_HOME" ]; then
  echo "Setting JAVA_HOME to $USE_JAVA_HOME for the build"
  export JAVA_HOME="$USE_JAVA_HOME"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

echo
echo "== Recompilation server-base, server-rivage, server-opera =="
./mvnw -DskipTests=true -DskipITs -pl server-base,server-rivage,server-opera -am clean package || {
  echo "Build failed. Rerun with -e and paste the output to me."; exit 1;
}

echo
echo "== Vérification des headers des classes re-générées (HotelServerApplication) =="
if [ -f server-base/target/classes/org/examples/server/HotelServerApplication.class ]; then
  python3 - <<'PY'
from pathlib import Path
p=Path('server-base/target/classes/org/examples/server/HotelServerApplication.class')
if p.exists():
    b=p.read_bytes()[:8]
    maj=(b[6]<<8)|b[7]
    print('server-base/HotelServerApplication.class major=',maj)
else:
    print('class not found')
PY
fi

echo
echo "== Done =="
cat <<'EOF'
Si tout s'est bien passé, lance les serveurs (chacun dans un terminal) :

# Terminal 1
./mvnw -pl server-rivage -DskipTests=true spring-boot:run

# Terminal 2
./mvnw -pl server-opera -DskipTests=true spring-boot:run

Ensuite, une fois WSDL accessibles (p.ex. http://localhost:8081/hotel?wsdl), génère les stubs et lance le client :

./mvnw -pl client-cli -Pgenerate-stubs -Dgenerate.stubs=true -Dwsdl.url=http://localhost:8081/hotel?wsdl jaxws:wsimport
./mvnw -pl client-cli -DskipTests=true exec:java -Dwsdl.url=http://localhost:8081/hotel?wsdl
EOF

exit 0

