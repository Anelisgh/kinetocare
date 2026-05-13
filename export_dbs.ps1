# ==============================================================================
# SCRIPT DE EXPORT AUTOMAT AL BAZELOR DE DATE (POWERSHELL / WINDOWS)
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
# 4. Compatibilitate Windows Nativ: Rulează perfect în terminalul nativ PowerShell,
#    folosind calea ta specifică spre `mysqldump.exe` și parametrii de codificare corecți.
# ==============================================================================

# Configuration
$dbUser = if ($env:MYSQL_USER) { $env:MYSQL_USER } else { "root" }
$dbPass = if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { "Emma19" }
$dbHost = if ($env:MYSQL_HOST_LOCAL) { $env:MYSQL_HOST_LOCAL } else { "localhost" }
$dbPort = if ($env:MYSQL_PORT_LOCAL) { $env:MYSQL_PORT_LOCAL } else { "3306" }
$outputDir = "db-init"
$mysqldumpPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"
$databases = @(
  "user_service",
  "pacienti_service",
  "terapeuti_service",
  "programari_service",
  "servicii_service",
  "chat_service",
  "notificari_service"
)

Write-Host "==============================================================="
Write-Host "KinetoCare Database Export Utility (PowerShell)"
Write-Host "==============================================================="
Write-Host "Local MySQL Host: ${dbHost}:${dbPort}"
Write-Host "User:             $dbUser"
Write-Host "Target Directory: $outputDir/"
Write-Host "==============================================================="

# Create directory if it doesn't exist
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
}

# Export each database
foreach ($db in $databases) {
    Write-Host ">>> Exporting database '$db'..."
    $outFile = Join-Path $outputDir "$db.sql"
    
    if ($dbPass) {
        & $mysqldumpPath -h $dbHost -P $dbPort -u $dbUser --password=$dbPass --databases $db --result-file=$outFile
    } else {
        & $mysqldumpPath -h $dbHost -P $dbPort -u $dbUser --databases $db --result-file=$outFile
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "    [SUCCESS] Exported $db to $outFile" -ForegroundColor Green
    } else {
        Write-Host "    [ERROR] Failed to export database $db" -ForegroundColor Red
    }
}

Write-Host "==============================================================="
Write-Host "Export process finished!"
Write-Host "Please verify the contents of '$outputDir/' before starting Docker."
Write-Host "==============================================================="
