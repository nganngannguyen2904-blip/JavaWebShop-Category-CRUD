@echo off
chcp 65001 >nul
title [HCMUTE] Shopping Service MVC - Tomcat Server
cls

echo ================================================================================
echo    TRUONG DAI HOC SU PHAM KY THUAT TP. HO CHI MINH (HCMUTE)
echo    MON HOC: LAP TRINH WEB (WEBPR330479) - GVHD: ThS. NGUYEN HUU TRUNG
echo    DO AN CUOI KY: SHOPPING SERVICE MVC (SERVLET - JDBC - 3 TIER)
echo ================================================================================
echo.

:: 1. Xac dinh thu muc goc du an dong thoi theo vi tri file bat
set "PROJECT_ROOT=%~dp0"
if "%PROJECT_ROOT:~-1%"=="\" set "PROJECT_ROOT=%PROJECT_ROOT:~0,-1%"

echo [*] Thu muc du an: %PROJECT_ROOT%

:: 2. Kiem tra Java JDK
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" goto JAVA_FOUND
)
if exist "C:\Program Files\Java\jdk-21\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    goto JAVA_FOUND
)
if exist "C:\Program Files\Java\jdk-17\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    goto JAVA_FOUND
)
where java >nul 2>nul
if %errorlevel% equ 0 (
    for /f "delims=" %%i in ('where java') do (
        set "JAVA_CMD=%%i"
        goto JAVA_FOUND
    )
)
echo [LOI] Khong tim thay Java JDK tren may! Vui long cai dat JDK 17 hoac JDK 21.
pause
exit /b 1

:JAVA_FOUND
set "JRE_HOME=%JAVA_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo [*] Java JDK: %JAVA_HOME%

:: 3. Kiem tra Apache Tomcat
if exist "C:\Users\Ngan Dang Yeu\Downloads\apache-tomcat-11.0.27-windows-x64\apache-tomcat-11.0.27\bin\catalina.bat" (
    set "TOMCAT_DIR=C:\Users\Ngan Dang Yeu\Downloads\apache-tomcat-11.0.27-windows-x64\apache-tomcat-11.0.27"
    goto TOMCAT_FOUND
)
if exist "%USERPROFILE%\Downloads\apache-tomcat-11.0.27-windows-x64\apache-tomcat-11.0.27\bin\catalina.bat" (
    set "TOMCAT_DIR=%USERPROFILE%\Downloads\apache-tomcat-11.0.27-windows-x64\apache-tomcat-11.0.27"
    goto TOMCAT_FOUND
)
if exist "%PROJECT_ROOT%\tomcat\bin\catalina.bat" (
    set "TOMCAT_DIR=%PROJECT_ROOT%\tomcat"
    goto TOMCAT_FOUND
)
if defined CATALINA_HOME (
    if exist "%CATALINA_HOME%\bin\catalina.bat" (
        set "TOMCAT_DIR=%CATALINA_HOME%"
        goto TOMCAT_FOUND
    )
)
echo [LOI] Khong tim thay Apache Tomcat 11!
pause
exit /b 1

:TOMCAT_FOUND
echo [*] Apache Tomcat: %TOMCAT_DIR%

:: 4. Giai phong port 8080 va 8005 neu co tien trinh cu dang chay ngam
echo [*] Kiem tra va tat tien trinh cu tren port 8080 va 8005...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 "') do (
    if not "%%a"=="0" taskkill /F /PID %%a >nul 2>nul
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8005 "') do (
    if not "%%a"=="0" taskkill /F /PID %%a >nul 2>nul
)

:: 5. Dong bo file WAR va lam sach cache
set "WAR_SRC=%PROJECT_ROOT%\target\ShoppingServiceMVC-1.0-SNAPSHOT.war"
if not exist "%WAR_SRC%" set "WAR_SRC=%PROJECT_ROOT%\ShoppingServiceMVC\target\ShoppingServiceMVC-1.0-SNAPSHOT.war"
set "WAR_DST=%TOMCAT_DIR%\webapps\ShoppingServiceMVC.war"

if not exist "%WAR_SRC%" (
    echo [CANH BAO] Chua co file WAR trong target, dang goi mvn package...
    if exist "%PROJECT_ROOT%\scripts\build_project.bat" (
        call "%PROJECT_ROOT%\scripts\build_project.bat" nopause
    )
)

echo [*] Lam sach thu muc uncompress va cache JSP cu trong Tomcat...
if exist "%TOMCAT_DIR%\webapps\ShoppingServiceMVC" rd /s /q "%TOMCAT_DIR%\webapps\ShoppingServiceMVC"
if exist "%TOMCAT_DIR%\work\Catalina\localhost\ShoppingServiceMVC" rd /s /q "%TOMCAT_DIR%\work\Catalina\localhost\ShoppingServiceMVC"

echo [*] Sao chep ban build moi nhat vao Tomcat webapps...
copy /Y "%WAR_SRC%" "%WAR_DST%" >nul

:: 6. Sao chep DLL va JAR ket noi SQL Server vao Tomcat
if exist "%PROJECT_ROOT%\lib\mssql-jdbc_auth-12.8.1.x64.dll" (
    copy /Y "%PROJECT_ROOT%\lib\mssql-jdbc_auth-12.8.1.x64.dll" "%TOMCAT_DIR%\bin\" >nul
)
if exist "%PROJECT_ROOT%\lib\mssql-jdbc-12.8.1.jre11.jar" (
    copy /Y "%PROJECT_ROOT%\lib\mssql-jdbc-12.8.1.jre11.jar" "%TOMCAT_DIR%\lib\" >nul
)
if exist "%PROJECT_ROOT%\lib\jbcrypt-0.4.jar" (
    copy /Y "%PROJECT_ROOT%\lib\jbcrypt-0.4.jar" "%TOMCAT_DIR%\lib\" >nul
)

:: 6.1 Dong bo file .env vao Tomcat bin de ung dung luon doc duoc bien moi truong
if exist "%PROJECT_ROOT%\.env" (
    copy /Y "%PROJECT_ROOT%\.env" "%TOMCAT_DIR%\bin\.env" >nul
    copy /Y "%PROJECT_ROOT%\.env" "%TOMCAT_DIR%\.env" >nul
)

:: 7. Thiet lap tham so he thong cho Tomcat (Khong chua khoang trang gay loi phan giai tham so)
set "UPLOAD_DIR=%PROJECT_ROOT:\=/%/upload"
set "CATALINA_OPTS=-Dfile.encoding=UTF-8 -Dupload.dir=%UPLOAD_DIR% -Dproject.root=%PROJECT_ROOT%"

echo.
echo ================================================================================
echo  [THANH CONG] TAT CA DA SAN SANG! DANG KHOI DONG MAY CHU WEB TOMCAT 11...
echo  Trinh duyet se TU DONG MO sau 4 giay tai dia chi:
echo  [URL] http://localhost:8080/ShoppingServiceMVC/home
echo ================================================================================
echo.

:: Tu dong mo trinh duyet sau 4 giay
start "" powershell -WindowStyle Hidden -Command "Start-Sleep -Seconds 4; Start-Process 'http://localhost:8080/ShoppingServiceMVC/home'"

:: Chay Tomcat tren cua so console
cd /d "%TOMCAT_DIR%\bin"
call catalina.bat run

echo.
echo ================================================================================
echo  May chu web da dung.
echo ================================================================================
pause
