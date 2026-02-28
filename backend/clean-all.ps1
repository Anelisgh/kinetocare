# Script to forcefully clean all target directories
$services = @("api-gateway", "user-service", "terapeuti-service", "pacienti-service", "programari-service", "servicii-service", "notificari-service", "chat-service")

foreach ($service in $services) {
    $targetPath = ".\$service\target"
    if (Test-Path $targetPath) {
        Write-Host "Cleaning $service..." -ForegroundColor Yellow
        Remove-Item -Path $targetPath -Recurse -Force -ErrorAction SilentlyContinue
        Write-Host "✓ Cleaned $service" -ForegroundColor Green
    }
}

Write-Host "`nAll target directories cleaned!" -ForegroundColor Green
Write-Host "Now you can run: mvn clean install" -ForegroundColor Cyan
