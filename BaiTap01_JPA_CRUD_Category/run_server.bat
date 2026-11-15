@echo off
chcp 65001 >nul
title [HCMUTE] BaiTap01_JPA_CRUD_Category - Tomcat Server
cls

echo ================================================================================
echo    TRUONG DAI HOC SU PHAM KY THUAT TP. HO CHI MINH (HCMUTE)
echo    MON HOC: LAP TRINH WEB (WEBPR330479) - GVHD: ThS. NGUYEN HUU TRUNG
echo    BAI TAP: CRUD CATEGORY BANG JPA 3.0 VA HIBERNATE ORM (SQL SERVER)
echo ================================================================================
echo.

:: 1. Thu muc du an
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
echo [LOI] Khong tim thay Java JDK tren may!
pause
exit /b 1

:JAVA_FOUND
set "JRE_HOME=%JAVA_HOME%"
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo [*] Java JDK: %JAVA_HOME%

:: 3. Kiem tra Apache Tomcat 11
if exist "C:\Users\Ngan Dang Yeu\Downloads\apache-tomcat-11.0.27-windows-x64\apache-tomcat-11.0.27\bin\catalina.bat" (
    set "TOMCAT_DIR=C:\Users\Ngan Dang Yeu\Downloads\apache-tomcat-11.0.27-windows-x64\apache-tomcat-11.0.27"
    goto TOMCAT_FOUND
)
if exist "%USERPROFILE%\Downloads\apache-tomcat-11.0.27-windows-x64\apache-tomcat-11.0.27\bin\catalina.bat" (
    set "TOMCAT_DIR=%USERPROFILE%\Downloads\apache-tomcat-11.0.27-windows-x64\apache-tomcat-11.0.27"
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

:: 4. Giai phong port 8080 va 8005 neu co tien trinh cu dang chay
echo [*] Kiem tra va tat tien trinh cu tren port 8080 va 8005...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 "') do (
    if not "%%a"=="0" taskkill /F /PID %%a >nul 2>nul
)
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8005 "') do (
    if not "%%a"=="0" taskkill /F /PID %%a >nul 2>nul
)

:: 5. Kiem tra file WAR
set "WAR_SRC=%PROJECT_ROOT%\target\BaiTap01_JPA_CRUD_Category.war"
set "WAR_DST=%TOMCAT_DIR%\webapps\BaiTap01_JPA_CRUD_Category.war"

if not exist "%WAR_SRC%" (
    echo [*] Dang bien dich va dong goi WAR bang Maven...
    cd /d "%PROJECT_ROOT%"
    set "MVN_RUN=mvn"
    if exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.10.0\2aaba761688b1a6ea5a46e7e5f4d5406ed7a75ee8da95e5e3079863a23461c9b\bin\mvn.cmd" (
        set "MVN_RUN=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.10.0\2aaba761688b1a6ea5a46e7e5f4d5406ed7a75ee8da95e5e3079863a23461c9b\bin\mvn.cmd"
    ) else if exist "C:\Users\Ngan Dang Yeu\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd" (
        set "MVN_RUN=C:\Users\Ngan Dang Yeu\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd"
    )
    call "%MVN_RUN%" package -DskipTests
)

echo [*] Lam sach cache webapp cu trong Tomcat...
if exist "%TOMCAT_DIR%\webapps\BaiTap01_JPA_CRUD_Category" rd /s /q "%TOMCAT_DIR%\webapps\BaiTap01_JPA_CRUD_Category"
if exist "%TOMCAT_DIR%\work\Catalina\localhost\BaiTap01_JPA_CRUD_Category" rd /s /q "%TOMCAT_DIR%\work\Catalina\localhost\BaiTap01_JPA_CRUD_Category"

echo [*] Sao chep ban build WAR vao Tomcat webapps...
copy /Y "%WAR_SRC%" "%WAR_DST%" >nul

:: 6. Sao chep DLL ket noi SQL Server va file .env vao Tomcat bin
if exist "%PROJECT_ROOT%\lib\mssql-jdbc_auth-12.8.1.x64.dll" (
    copy /Y "%PROJECT_ROOT%\lib\mssql-jdbc_auth-12.8.1.x64.dll" "%TOMCAT_DIR%\bin\" >nul
) else if exist "%PROJECT_ROOT%\..\lib\mssql-jdbc_auth-12.8.1.x64.dll" (
    copy /Y "%PROJECT_ROOT%\..\lib\mssql-jdbc_auth-12.8.1.x64.dll" "%TOMCAT_DIR%\bin\" >nul
)
if exist "%PROJECT_ROOT%\.env" copy /Y "%PROJECT_ROOT%\.env" "%TOMCAT_DIR%\bin\.env" >nul

:: 7. Thiet lap tham so
set "UPLOAD_DIR=%PROJECT_ROOT:\=/%/upload/category"
set "CATALINA_OPTS=-Dfile.encoding=UTF-8 -Dupload.dir=%UPLOAD_DIR%"

echo.
echo ================================================================================
echo  [THANH CONG] TAT CA DA SAN SANG! DANG KHOI DONG TOMCAT 11...
echo  Trinh duyet se TU DONG MO sau 4 giay tai dia chi:
echo  [URL] http://localhost:8080/BaiTap01_JPA_CRUD_Category/admin/categories
echo ================================================================================
echo.

start "" powershell -WindowStyle Hidden -Command "Start-Sleep -Seconds 4; Start-Process 'http://localhost:8080/BaiTap01_JPA_CRUD_Category/admin/categories'"

cd /d "%TOMCAT_DIR%\bin"
call catalina.bat run

echo.
pause
