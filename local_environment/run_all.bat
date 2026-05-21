@echo off
chcp 65001 > nul
title VotaMas - Database Setup

cls
echo ==========================================
echo      INICIANDO CONFIGURACION DB
echo ==========================================
echo.

set PGPASSWORD=12345
set "PG_BIN=C:\Program Files\PostgreSQL\18\bin"

if not exist "%PG_BIN%\psql.exe" (
    echo ERROR: No se encontro psql.exe en:
    echo %PG_BIN%\psql.exe
    echo.
    echo Ajusta la variable PG_BIN en run_all.bat con la ruta correcta de PostgreSQL.
    pause
    exit /b 1
)

echo [1/3] Verificando PostgreSQL...
"%PG_BIN%\psql.exe" --version

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR verificando PostgreSQL
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [2/3] Ejecutando create_BD.sql...
"%PG_BIN%\psql.exe" -v ON_ERROR_STOP=1 -U postgres -d postgres -f create_BD.sql

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR ejecutando la base de datos
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [3/3] Base de datos creada correctamente
echo PROCESO FINALIZADO
echo.

pause
