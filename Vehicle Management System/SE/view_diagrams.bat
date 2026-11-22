@echo off
title View Software Engineering Diagrams
cd /d "%~dp0"
echo Opening Software Engineering Diagrams in default browser...
start "" "%~dp0view_diagrams.html"
exit
