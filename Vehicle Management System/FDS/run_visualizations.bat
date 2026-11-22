@echo off
title FDS - Vehicle Service Analysis Visualizations
color 0B
cd /d "%~dp0"

echo ============================================================
echo   Foundations of Data Science (FDS) - Analysis ^& Visualizations
echo ============================================================
echo Checking Python installation...

where python >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Python not found in PATH!
    echo Please install Python 3 and add it to PATH.
    pause
    exit /b 1
)

echo.
echo Choose execution mode:
echo   [1] Standalone Analysis (Embedded data - No MySQL required)
echo   [2] MySQL Database Analysis (Requires VehicleServiceDB running)
echo.
set /p choice="Enter choice (1 or 2, default is 1): "

if "%choice%"=="2" (
    echo.
    echo Running MySQL Database Analysis...
    python vehicle_service_analysis.py
) else (
    echo.
    echo Running Standalone Analysis ^& Generating 8 Visualizations...
    python sample_data_standalone.py
)

echo.
echo ============================================================
echo Visualizations generated in folder: "%~dp0output"
echo Opening output charts folder...
start "" "%~dp0output"
echo ============================================================
pause
