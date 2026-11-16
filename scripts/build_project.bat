@echo off
title Build Project Shopping Service MVC
echo ==============================================================
echo        DONG GOI DU AN SHOPPING SERVICE MVC (MAVEN)
echo ==============================================================
echo.
if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-21" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    ) else if exist "C:\Program Files\Java\jdk-17" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-17"
    )
)

if not defined MAVEN_CMD (
    if exist "%~dp0..\maven\bin\mvn.cmd" (
        set "MAVEN_CMD=%~dp0..\maven\bin\mvn.cmd"
    ) else if exist "%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.10.0\2aaba761688b1a6ea5a46e7e5f4d5406ed7a75ee8da95e5e3079863a23461c9b\bin\mvn.cmd" (
        set "MAVEN_CMD=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.10.0\2aaba761688b1a6ea5a46e7e5f4d5406ed7a75ee8da95e5e3079863a23461c9b\bin\mvn.cmd"
    ) else if exist "C:\Users\Ngan Dang Yeu\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd" (
        set "MAVEN_CMD=C:\Users\Ngan Dang Yeu\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd"
    ) else if exist "%USERPROFILE%\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd" (
        set "MAVEN_CMD=%USERPROFILE%\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd"
    ) else (
        set "MAVEN_CMD=mvn"
    )
)

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%.."
echo Dang thuc hien mvn clean package tai thu muc goc...
call "%MAVEN_CMD%" clean package
if exist "%SCRIPT_DIR%..\target\ShoppingServiceMVC-1.0-SNAPSHOT.war" (
    if not exist "%SCRIPT_DIR%..\ShoppingServiceMVC\target" mkdir "%SCRIPT_DIR%..\ShoppingServiceMVC\target"
    copy /Y "%SCRIPT_DIR%..\target\ShoppingServiceMVC-1.0-SNAPSHOT.war" "%SCRIPT_DIR%..\ShoppingServiceMVC\target\" >nul
)
if %errorlevel% equ 0 (
    echo.
    echo ==============================================================
    echo [THANH CONG] File WAR da duoc tao thanh cong tai:
    echo %SCRIPT_DIR%..\target\ShoppingServiceMVC-1.0-SNAPSHOT.war
    echo ==============================================================
) else (
    echo.
    echo [LOI] Co loi xay ra khi build.
)
if "%1" neq "nopause" pause
