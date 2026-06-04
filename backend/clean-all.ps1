# Script to forcefully clean all target directories
$services = @("common", "api-gateway", "user-service", "terapeuti-service", "pacienti-service", "programari-service", "servicii-service", "notificari-service", "chat-service")

foreach ($service in $services) {
    $targetPath = ".\$service\target"
    if (Test-Path $targetPath) {
        Write-Host "Cleaning $service..." -ForegroundColor Yellow
        Remove-Item -Path $targetPath -Recurse -Force -ErrorAction SilentlyContinue
        Write-Host "✓ Cleaned $service" -ForegroundColor Green
    }
}

Write-Host "`nAll target directories cleaned!" -ForegroundColor Green
Write-Host "Now we can run: mvn clean install -DskipTests" -ForegroundColor Green

mvn clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "Build successful!" -ForegroundColor Green
