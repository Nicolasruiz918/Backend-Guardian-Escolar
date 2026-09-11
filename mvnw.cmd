@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

docker run --rm -v "%SCRIPT_DIR%:/workspace" -v gps_guardian_escolar_m2:/root/.m2 -w /workspace maven:3.9.11-eclipse-temurin-17 mvn %*

exit /b %ERRORLEVEL%
