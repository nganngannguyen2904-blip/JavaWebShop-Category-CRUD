package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class InitWebst2Database {
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://localhost:1433;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";
        try (Connection c = DriverManager.getConnection(url); Statement s = c.createStatement()) {
            boolean found = false;
            ResultSet rs = s.executeQuery("SELECT name FROM sys.databases WHERE name = 'webst2'");
            if (rs.next()) {
                found = true;
                System.out.println("[*] Cơ sở dữ liệu 'webst2' đã tồn tại sẵn trên SQL Server!");
            }
            if (!found) {
                System.out.println("[*] Đang khởi tạo cơ sở dữ liệu 'webst2' trên SQL Server...");
                s.executeUpdate("CREATE DATABASE webst2 COLLATE Latin1_General_100_CI_AI_SC_UTF8;");
                System.out.println("[+] Tạo thành công cơ sở dữ liệu 'webst2'!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
