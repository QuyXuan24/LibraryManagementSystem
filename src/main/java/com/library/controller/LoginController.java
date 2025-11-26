package com.library.controller;

import com.library.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserRepository userRepo = new UserRepository();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Kiểm tra trong Database
        if (userRepo.checkLogin(username, password)) {
            // Đăng nhập thành công -> Chuyển cảnh
            try {
                // 1. Load file giao diện chính (Main Dashboard)
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));

                // 2. Lấy cái Stage (Cửa sổ) hiện tại từ nút bấm
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // 3. Đổi nội dung cửa sổ sang Main
                Scene scene = new Scene(root, 1200, 700);
                stage.setScene(scene);
                stage.setTitle("Hệ Thống Quản Lý Thư Viện Pro");
                stage.centerOnScreen(); // Căn giữa màn hình cho đẹp
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Lỗi khi tải giao diện chính!");
            }
        } else {
            errorLabel.setText("Sai tên đăng nhập hoặc mật khẩu!");
        }
    }
}