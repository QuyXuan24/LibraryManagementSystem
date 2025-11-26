package com.library.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Label lblWelcome;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(">> Hệ thống đã khởi động thành công!");

        // Hiển thị ngày giờ hiện tại lên góc màn hình cho chuyên nghiệp
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblWelcome.setText("Admin | Ngày: " + dtf.format(LocalDateTime.now()));
    }
}