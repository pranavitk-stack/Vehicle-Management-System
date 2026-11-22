@echo off
title Vehicle Service Management System - Master Launcher
color 0F
cd /d "%~dp0"

:MENU
cls
echo ============================================================
echo   VEHICLE SERVICE MANAGEMENT SYSTEM - MASTER RUNNER
echo ============================================================
echo.
echo   [1] Launch Java Menu-Driven Console Application
echo   [2] Run FDS Data Science Visualizations (Python)
echo   [3] Open FDS Visualizations Output Folder (8 PNG Charts)
echo   [4] Open DBMS ER Diagram Image (er_diagram.png)
echo   [5] Open DBMS Folder (SQL Queries & Schema)
echo   [6] Open Software Engineering (SE) Diagrams in Browser
echo   [7] Open SE Diagrams Folder (7 PNG Diagrams)
echo   [0] Exit
echo.
echo ============================================================
set /p opt="Enter choice [0-7]: "

if "%opt%"=="1" (
    cd /d "%~dp0Java"
    call run_java_app.bat
    cd /d "%~dp0"
    goto MENU
)
if "%opt%"=="2" (
    cd /d "%~dp0FDS"
    call run_visualizations.bat
    cd /d "%~dp0"
    goto MENU
)
if "%opt%"=="3" (
    start "" "%~dp0FDS\output"
    goto MENU
)
if "%opt%"=="4" (
    start "" "%~dp0DBMS\er_diagram.png"
    goto MENU
)
if "%opt%"=="5" (
    start "" "%~dp0DBMS"
    goto MENU
)
if "%opt%"=="6" (
    start "" "%~dp0SE\view_diagrams.html"
    goto MENU
)
if "%opt%"=="7" (
    start "" "%~dp0SE"
    goto MENU
)
if "%opt%"=="0" (
    exit /b 0
)

echo Invalid choice, please try again.
pause
goto MENU
