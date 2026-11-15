import os

# Nội dung run_server.bat chuẩn định dạng Windows CRLF
run_server_content = (
    "@echo off\r\n"
    "title Shopping Service MVC - Tomcat 11 Server\r\n"
    "echo ==============================================================\r\n"
    "echo        KHOI DONG HE THONG SHOPPING SERVICE MVC (TOMCAT 11)\r\n"
    "echo ==============================================================\r\n"
    "echo.\r\n"
    "set JAVA_HOME=C:\\Program Files\\Java\\jdk-21\r\n"
    "set JRE_HOME=C:\\Program Files\\Java\\jdk-21\r\n"
    "set PATH=%JAVA_HOME%\\bin;%PATH%\r\n"
    "\r\n"
    "set TOMCAT_DIR=C:\\Users\\Ngan Dang Yeu\\Downloads\\apache-tomcat-11.0.27-windows-x64\\apache-tomcat-11.0.27\r\n"
    "set WAR_SOURCE=d:\\LT_Web\\BTVN_9-11\\ShoppingServiceMVC\\target\\ShoppingServiceMVC-1.0-SNAPSHOT.war\r\n"
    "set WAR_TARGET=%TOMCAT_DIR%\\webapps\\ShoppingServiceMVC.war\r\n"
    "\r\n"
    "echo [1/3] Dang sao chep file WAR moi nhat vao Tomcat webapps...\r\n"
    "copy /Y \"%WAR_SOURCE%\" \"%WAR_TARGET%\" >nul\r\n"
    "if %errorlevel% neq 0 (\r\n"
    "    echo [CANH BAO] Khong the copy file WAR (co the Tomcat dang chay va giu file).\r\n"
    ") else (\r\n"
    "    echo [OK] Da cap nhat ShoppingServiceMVC.war thanh cong!\r\n"
    ")\r\n"
    "\r\n"
    "echo [2/3] Kiem tra thu vien ket noi mssql-jdbc_auth DLL...\r\n"
    "if not exist \"%TOMCAT_DIR%\\bin\\mssql-jdbc_auth-12.8.1.x64.dll\" (\r\n"
    "    copy /Y \"d:\\LT_Web\\BTVN_9-11\\mssql_auth\\x64\\mssql-jdbc_auth-12.8.1.x64.dll\" \"%TOMCAT_DIR%\\bin\\\" >nul\r\n"
    ")\r\n"
    "\r\n"
    "echo [3/3] Dang khoi dong Apache Tomcat 11...\r\n"
    "echo.\r\n"
    "echo ==============================================================\r\n"
    "echo  Server dang chay! Ban co the mo trinh duyet truy cap:\r\n"
    "echo.\r\n"
    "echo  1. Trang chu:     http://localhost:8080/ShoppingServiceMVC/\r\n"
    "echo  2. Dang nhap:    http://localhost:8080/ShoppingServiceMVC/login\r\n"
    "echo  3. Quan ly Admin: http://localhost:8080/ShoppingServiceMVC/admin/category/list\r\n"
    "echo ==============================================================\r\n"
    "echo (Luu y: Giu nguyen cua so nay de may chu tiep tuc hoat dong)\r\n"
    "echo.\r\n"
    "\r\n"
    "cd /d \"%TOMCAT_DIR%\\bin\"\r\n"
    "call catalina.bat run\r\n"
    "pause\r\n"
)

with open("d:/LT_Web/BTVN_9-11/run_server.bat", "wb") as f:
    f.write(run_server_content.encode("ascii"))

# Nội dung build_project.bat chuẩn Windows CRLF
build_content = (
    "@echo off\r\n"
    "title Build Project Shopping Service MVC\r\n"
    "echo ==============================================================\r\n"
    "echo        DONG GOI DU AN SHOPPING SERVICE MVC (MAVEN)\r\n"
    "echo ==============================================================\r\n"
    "echo.\r\n"
    "set JAVA_HOME=C:\\Program Files\\Java\\jdk-21\r\n"
    "set MAVEN_CMD=C:\\Users\\Ngan Dang Yeu\\Downloads\\apache-maven-3.10.0-bin\\apache-maven-3.10.0\\bin\\mvn.cmd\r\n"
    "\r\n"
    "cd /d \"d:\\LT_Web\\BTVN_9-11\\ShoppingServiceMVC\"\r\n"
    "echo Dang thuc hien mvn clean package...\r\n"
    "call \"%MAVEN_CMD%\" clean package\r\n"
    "if %errorlevel% equ 0 (\r\n"
    "    echo.\r\n"
    "    echo ==============================================================\r\n"
    "    echo [THANH CONG] File WAR da duoc tao thanh cong tai:\r\n"
    "    echo d:\\LT_Web\\BTVN_9-11\\ShoppingServiceMVC\\target\\ShoppingServiceMVC-1.0-SNAPSHOT.war\r\n"
    "    echo ==============================================================\r\n"
    ") else (\r\n"
    "    echo.\r\n"
    "    echo [LOI] Co loi xay ra khi build.\r\n"
    ")\r\n"
    "pause\r\n"
)

with open("d:/LT_Web/BTVN_9-11/build_project.bat", "wb") as f:
    f.write(build_content.encode("ascii"))

print("Both bat files created with CRLF line endings!")
