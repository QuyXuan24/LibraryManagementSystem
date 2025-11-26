package com.library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // SỬA: Load màn hình Đăng Nhập (login.fxml) trước tiên
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

            primaryStage.setTitle("Đăng nhập - Hệ Thống Thư Viện");

            // Kích thước nhỏ gọn cho màn hình Login (350x400 là đẹp)
            primaryStage.setScene(new Scene(root, 350, 400));

            // Không cho người dùng kéo giãn cửa sổ Login cho vỡ giao diện
            primaryStage.setResizable(false);

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khởi động: Không tìm thấy file /fxml/login.fxml hoặc file bị lỗi!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}