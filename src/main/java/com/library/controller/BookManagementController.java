package com.library.controller;

import com.library.model.Book;
import com.library.repository.BookRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BookManagementController implements Initializable {

    // --- 1. Khai báo biến giao diện (Phải khớp fx:id bên FXML) ---
    @FXML private TextField searchField;
    @FXML private TableView<Book> bookTableView; // Lưu ý: TableView<Book>
    @FXML private TableColumn<Book, String> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colCategory;
    @FXML private TableColumn<Book, Integer> colQuantity;
    @FXML private Label messageLabel;

    // Form nhập liệu
    @FXML private TextField txtId;
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtCategory;
    @FXML private TextField txtQuantity;

    // --- 2. Logic ---
    private BookRepository bookRepository = new BookRepository();
    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cấu hình cột (Mapping với các thuộc tính trong Book.java)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Gán dữ liệu vào bảng
        bookTableView.setItems(bookList);

        // Load dữ liệu ban đầu
        loadData();

        // Sự kiện: Click vào bảng -> Đổ dữ liệu lên form
        bookTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtId.setText(newVal.getId());
                txtTitle.setText(newVal.getTitle());
                txtAuthor.setText(newVal.getAuthor());
                txtCategory.setText(newVal.getCategory());
                txtQuantity.setText(String.valueOf(newVal.getQuantity())); // Chuyển int sang String

                txtId.setDisable(true); // Khóa ID khi sửa
            }
        });

        // Tìm kiếm tự động (Real-time)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                loadData();
                messageLabel.setText("");
            } else {
                searchBook(newValue.trim());
            }
        });
    }

    // --- 3. CÁC CHỨC NĂNG CRUD ---

    @FXML
    public void handleAddBook() {
        // 1. Lấy dữ liệu
        String id = txtId.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String category = txtCategory.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        // 2. Validate cơ bản
        if (id.isEmpty() || title.isEmpty() || qtyStr.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập Mã, Tên sách và Số lượng!");
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr); // Ép kiểu số lượng

            // 3. Tạo đối tượng Book mới (Dùng đúng Constructor 5 tham số mới sửa)
            Book newBook = new Book(id, title, author, category, quantity);

            // 4. Gọi Repo lưu
            if (bookRepository.addBook(newBook)) {
                showAlert("Thành công", "Đã thêm sách: " + title);
                loadData();
                clearFields();
            } else {
                showAlert("Thất bại", "Không thể thêm (có thể trùng Mã sách).");
            }

        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng phải là một con số nguyên!");
        }
    }

    @FXML
    public void handleUpdateBook() {
        String id = txtId.getText().trim();
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String category = txtCategory.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        if (id.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn sách để sửa!");
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);
            Book book = new Book(id, title, author, category, quantity);

            if (bookRepository.updateBook(book)) {
                showAlert("Thành công", "Đã cập nhật sách: " + title);
                loadData();
                clearFields();
                txtId.setDisable(false);
            } else {
                showAlert("Thất bại", "Lỗi cập nhật.");
            }
        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số lượng phải là số!");
        }
    }

    @FXML
    public void handleDeleteBook() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn sách để xóa!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setContentText("Bạn có chắc muốn xóa cuốn sách mã " + id + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (bookRepository.deleteBook(id)) {
                showAlert("Thành công", "Đã xóa sách " + id);
                loadData();
                clearFields();
                txtId.setDisable(false);
            } else {
                showAlert("Thất bại", "Không thể xóa (sách đang được mượn hoặc lỗi DB).");
            }
        }
    }

    @FXML
    public void handleClear() {
        clearFields();
        txtId.setDisable(false);
        bookTableView.getSelectionModel().clearSelection();
        loadData();
    }

    // --- 4. HÀM PHỤ TRỢ ---

    private void loadData() {
        List<Book> list = bookRepository.getAllBooks();
        bookList.clear();
        bookList.addAll(list);
    }

    private void searchBook(String keyword) {
        List<Book> list = bookRepository.search(keyword);
        bookList.clear();
        bookList.addAll(list);
    }

    private void clearFields() {
        txtId.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtCategory.clear();
        txtQuantity.clear();
        messageLabel.setText("");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}