@echo off
setlocal

if "%DB_HOST%"=="" set DB_HOST=127.0.0.1
if "%DB_PORT%"=="" set DB_PORT=1433
if "%DB_NAME%"=="" set DB_NAME=HotelManagement
if "%DB_USER%"=="" set DB_USER=sa
if "%DB_PASSWORD%"=="" set DB_PASSWORD=Quan@0511

sqlcmd -S %DB_HOST%,%DB_PORT% -U %DB_USER% -P %DB_PASSWORD% -C -i db\schema.sql
if errorlevel 1 (
  echo [ERROR] Seed failed.
  exit /b 1
)

echo Seeded 15-line baseline data via db\schema.sql.
