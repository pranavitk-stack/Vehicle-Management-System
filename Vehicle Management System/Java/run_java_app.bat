@echo off
title Vehicle Service Management System
color 0A
cd /d "%~dp0"

echo ============================================================
echo   Vehicle Service Management System - Console Launcher
echo ============================================================
echo Checking Java installation...

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Java runtime not found in PATH!
    echo Please install JDK/JRE and try again.
    pause
    exit /b 1
)

where javac >nul 2>nul
if %errorlevel% equ 0 (
    echo Compiling VehicleServiceManagement.java...
    javac VehicleServiceManagement.java
    if %errorlevel% neq 0 (
        echo [ERROR] Compilation failed!
        pause
        exit /b 1
    )
)

echo Starting Vehicle Service Management System...
echo ============================================================
echo.

REM Launch with local directory and any mysql jar in classpath
java -cp ".;*" VehicleServiceManagement

echo.
echo ============================================================
echo Application terminated.
pause
