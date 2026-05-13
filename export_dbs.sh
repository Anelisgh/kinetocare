#!/bin/bash

# ==============================================================================
# SCRIPT DE EXPORT AUTOMAT AL BAZELOR DE DATE (BASH / GIT BASH)
# 
# ACEST SCRIPT TE AJUTĂ SĂ:
# 1. Automatizezi exportul: Descarcă toate cele 7 baze de date MySQL locale ale
#    microserviciilor în fișiere separate .sql printr-o singură rulare.
# 2. Creezi structura corectă pentru Docker: Folosește flag-ul `--databases`
#    care introduce automat 'CREATE DATABASE IF NOT EXISTS' și 'USE <nume_db>'
#    la începutul fiecărui fișier .sql, prevenind erorile de tip "No database selected"
#    când Docker încearcă să le importe.
# 3. Pregătești volumul de inițializare: Plasează fișierele .sql direct în folderul
#    `db-init/` care este montat în containerul de Docker MySQL.
# ==============================================================================

# Configuration
DB_USER=${MYSQL_USER:-"root"}
DB_PASS=${MYSQL_PASSWORD:-"Emma19"}
DB_HOST=${MYSQL_HOST_LOCAL:-"localhost"}
DB_PORT=${MYSQL_PORT_LOCAL:-"3306"}
OUTPUT_DIR="db-init"

# List of databases to export
DATABASES=(
  "user_service"
  "pacienti_service"
  "terapeuti_service"
  "programari_service"
  "servicii_service"
  "chat_service"
  "notificari_service"
)

echo "==============================================================="
echo "KinetoCare Database Export Utility"
echo "==============================================================="
echo "Local MySQL Host: $DB_HOST:$DB_PORT"
echo "User:             $DB_USER"
echo "Target Directory: $OUTPUT_DIR/"
echo "==============================================================="

# Create directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Export each database
for DB in "${DATABASES[@]}"; do
  echo ">>> Exporting database '$DB'..."
  
  # Perform the mysqldump
  if [ -n "$DB_PASS" ]; then
    mysqldump -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" --databases "$DB" > "$OUTPUT_DIR/$DB.sql"
  else
    mysqldump -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" --databases "$DB" > "$OUTPUT_DIR/$DB.sql"
  fi
  
  # Check status
  if [ $? -eq 0 ]; then
    echo "    [SUCCESS] Exported $DB to $OUTPUT_DIR/$DB.sql"
  else
    echo "    [ERROR] Failed to export database $DB"
  fi
done

echo "==============================================================="
echo "Export process finished!"
echo "Please verify the contents of '$OUTPUT_DIR/' before starting Docker."
echo "==============================================================="
