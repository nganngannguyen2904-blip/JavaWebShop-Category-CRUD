content = """@echo off
title Shopping Service MVC - Tomcat 11 Server
cls
echo ==============================================================
echo        KHOI DONG HE THONG SHOPPING SERVICE MVC (TOMCAT 11)
echo ==============================================================
echo.

set JAVA_HOME=C:\\Program Files\\Java\\jdk-21
set JRE_HOME=C:\\Program Files\\Java\\jdk-21
set PATH=%JAVA_HOME%\\bin;%PATH%

set TOMCAT_DIR=C:\\Users\\Ngan Dang Yeu\\Downloads\\apache-tomcat-11.0.27-windows-x64\\apache-tomcat-11.0.27
set WAR_SRC=d:\\LT_Web\\BTVN_9-11\\ShoppingServiceMVC\\target\\ShoppingServiceMVC-1.0-SNAPSHOT.war
set WAR_DST=%TOMCAT_DIR%\\webapps\\ShoppingServiceMVC.war

echo [1/3] Dong bo ung dung ShoppingServiceMVC.war vao Tomcat...
if exist "%TOMCAT_DIR%\\webapps\\ShoppingServiceMVC" rd /s /q "%TOMCAT_DIR%\\webapps\\ShoppingServiceMVC"
copy /Y "%WAR_SRC%" "%WAR_DST%" >nul

echo [2/3] Kiem tra thu vien ket noi SQL Server mssql-jdbc_auth.dll...
copy /Y "d:\\LT_Web\\BTVN_9-11\\mssql_auth\\x64\\mssql-jdbc_auth-12.8.1.x64.dll" "%TOMCAT_DIR%\\bin\\" >nul

echo [3/3] Khoi dong Apache Tomcat 11...
echo.
echo ==============================================================
echo  Dang khoi dong may chu web...
echo  Trinh duyet se TU DONG MO sau 4 giay tai dia chi:
echo  http://localhost:8080/ShoppingServiceMVC/home
echo ==============================================================
echo.

start "" powershell -WindowStyle Hidden -Command "Start-Sleep -Seconds 4; Start-Process 'http://localhost:8080/ShoppingServiceMVC/home'"

cd /d "%TOMCAT_DIR%\\bin"
catalina.bat run

echo.
echo ==============================================================
echo May chu da dung lai.
echo ==============================================================
pause
"""

with open(r"d:\LT_Web\BTVN_9-11\run_server.bat", "wb") as f:
    f.write(content.replace("\n", "\r\n").encode("utf-8"))

print("run_server.bat updated successfully!")
