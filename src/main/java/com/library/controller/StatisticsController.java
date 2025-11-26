package com.library.controller;

import com.library.repository.StatisticsRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class StatisticsController implements Initializable {

    // --- Khai báo fx:id khớp với FXML ---
    @FXML private Label lblTotalBooks;
    @FXML private Label lblTotalMembers;
    @FXML private Label lblActiveBorrows;
    @FXML private Label lblTotalFine;

    @FXML private PieChart pieChart;
    @FXML private BarChart<String, Number> barChart;

    private StatisticsRepository statsRepo = new StatisticsRepository();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStatistics();
    }

    @FXML
    public void loadStatistics() {
        System.out.println(">> Đang tải dữ liệu thống kê...");

        // 1. Cập nhật các Thẻ bài (Cards)
        lblTotalBooks.setText(String.valueOf(statsRepo.getTotalBookQuantity()));
        lblTotalMembers.setText(String.valueOf(statsRepo.getTotalMembers()));
        lblActiveBorrows.setText(String.valueOf(statsRepo.getActiveBorrowCount()));

        double fine = statsRepo.getTotalFineCollected();
        lblTotalFine.setText(String.format("%,.0f VNĐ", fine));

        // 2. Vẽ Biểu đồ Tròn (Thể loại sách)
        loadPieChart();

        // 3. Vẽ Biểu đồ Cột (Top sách hot)
        loadBarChart();
    }

    private void loadPieChart() {
        pieChart.getData().clear();
        Map<String, Integer> data = statsRepo.getBookCategoryData();

        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            chartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        pieChart.setData(chartData);
    }

    private void loadBarChart() {
        barChart.getData().clear();
        Map<String, Integer> data = statsRepo.getTopBorrowedBooks();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt mượn");

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);
    }
}