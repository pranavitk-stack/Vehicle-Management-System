@echo off
title Download MySQL JDBC Connector Driver
cd /d "%~dp0"

echo ============================================================
echo   Download MySQL Connector/J Driver
echo ============================================================
echo.
echo Attempting to download mysql-connector-j-8.3.0.jar from Maven Central...

powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; try { Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar' -OutFile 'mysql-connector-j-8.3.0.jar' -UseBasicParsing; Write-Host 'Download successful!' -ForegroundColor Green } catch { Write-Host 'Automatic download failed. You can manually download mysql-connector-j-8.3.0.jar and place it in this folder.' -ForegroundColor Yellow }"

echo.
pause
