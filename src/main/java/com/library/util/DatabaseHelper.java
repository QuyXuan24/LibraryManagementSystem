package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    // Tên Database sếp vừa tạo trong phpMyAdmin
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root"; // User mặc định của XAMPP
    private static final String PASS = "";     // Mật khẩu mặc định (để trống)

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load driver
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("Không tìm thấy Driver MySQL!");
                    return null;
                }

                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println(">> Kết nối Database thành công! 🚀");
            }
        } catch (SQLException e) {
            System.err.println(">> Lỗi kết nối: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}