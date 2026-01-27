@echo off
REM Copia los archivos del editor de chat desde webapp a target

set SOURCE_BASE=src\main\webapp\xava\editors
set TARGET_BASE=target\chatvoice\xava\editors

echo Copiando archivos del editor de chat...

REM Crear directorios si no existen
if not exist "%TARGET_BASE%\js" mkdir "%TARGET_BASE%\js"
if not exist "%TARGET_BASE%\style" mkdir "%TARGET_BASE%\style"

REM Copiar archivos
copy /Y "%SOURCE_BASE%\chat.jsp" "%TARGET_BASE%\chat.jsp"
copy /Y "%SOURCE_BASE%\js\chat.js" "%TARGET_BASE%\js\chat.js"
copy /Y "%SOURCE_BASE%\style\chat.css" "%TARGET_BASE%\style\chat.css"

echo.
echo Archivos copiados exitosamente:
echo - chat.jsp
echo - js\chat.js
echo - style\chat.css
echo.
pause
