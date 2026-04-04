@echo off
setlocal

set CLASS_NAME=%1
if "%CLASS_NAME%"=="" set CLASS_NAME=gui.GiaoDienChinh

if "%DB_HOST%"=="" set DB_HOST=127.0.0.1
if "%DB_PORT%"=="" set DB_PORT=1433
if "%DB_NAME%"=="" set DB_NAME=HotelManagement
if "%DB_USER%"=="" set DB_USER=sa
if "%DB_PASSWORD%"=="" set DB_PASSWORD=Quan@0511
if "%DB_ENCRYPT%"=="" set DB_ENCRYPT=true
if "%DB_TRUST_SERVER_CERT%"=="" set DB_TRUST_SERVER_CERT=true
if "%APPLY_SCHEMA_ON_START%"=="" set APPLY_SCHEMA_ON_START=0

where sqlcmd >nul 2>&1
if errorlevel 1 (
  echo [INFO] sqlcmd not found. Skip schema apply and run app with existing local DB.
) else (
  if "%APPLY_SCHEMA_ON_START%"=="1" (
    sqlcmd -S %DB_HOST%,%DB_PORT% -U %DB_USER% -P %DB_PASSWORD% -C -i db\schema.sql
    if errorlevel 1 (
      echo [WARN] Could not apply db\schema.sql. Continue running app...
    )
  ) else (
    echo [INFO] APPLY_SCHEMA_ON_START=0, skip schema apply.
    echo [INFO] Set APPLY_SCHEMA_ON_START=1 if you want to apply db\schema.sql manually on startup.
  )
)

if not exist bin mkdir bin
javac -d bin -cp "lib/*" src\connection\*.java src\entity\*.java src\dao\*.java src\gui\*.java
if errorlevel 1 exit /b 1

java -cp "bin;lib/*" %CLASS_NAME%
