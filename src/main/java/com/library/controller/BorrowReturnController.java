package com.library.controller;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.MemberRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BorrowReturnController implements Initializable {

    // --- 1. KHAI BÁO CÁC CONTROL ---
    @FXML private TextField memberIdField;
    @FXML private TextField isbnField;
    @FXML private DatePicker borrowDatePicker; // <--- MỚI THÊM

    @FXML private TextField returnMemberIdField;
    @FXML private TextField returnIsbnField;

    @FXML private TableView<BorrowRecord> borrowedBooksTable;
    @FXML private TableColumn<BorrowRecord, String> colMemberId;
    @FXML private TableColumn<BorrowRecord, String> colIsbn;
    @FXML private TableColumn<BorrowRecord, String> colBookName;
    @FXML private TableColumn<BorrowRecord, LocalDate> colBorrowDate;
    @FXML private TableColumn<BorrowRecord, LocalDate> colDueDate;

    @FXML private Label messageLabel;

    // --- 2. KHO DỮ LIỆU ---
    private BorrowRecordRepository borrowRepo = new BorrowRecordRepository();
    private BookRepository bookRepo = new BookRepository();
    private MemberRepository memberRepo = new MemberRepository();

    private ObservableList<BorrowRecord> borrowList = FXCollections.observableArrayList();
    private List<String> memberSuggestions;
    private List<String> bookSuggestions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        borrowedBooksTable.setItems(borrowList);
        loadData();
        initSuggestions();

        // Mặc định chọn ngày hôm nay cho DatePicker
        borrowDatePicker.setValue(LocalDate.now());
    }

    // ... (Các hàm gợi ý initSuggestions, setupAutoComplete, extractId giữ nguyên) ...
    // Sếp giữ nguyên phần Logic gợi ý ở bài trước nhé, em rút gọn để đỡ dài dòng
    private void initSuggestions() {
        List<Member> members = memberRepo.getAllMembers();
        memberSuggestions = members.stream().map(m -> m.getId() + " | " + m.getName()).collect(Collectors.toList());
        List<Book> books = bookRepo.getAllBooks();
        bookSuggestions = books.stream().map(b -> b.getId() + " | " + b.getTitle()).collect(Collectors.toList());

        setupAutoComplete(memberIdField, memberSuggestions);
        setupAutoComplete(returnMemberIdField, memberSuggestions);
        setupAutoComplete(isbnField, bookSuggestions);
        setupAutoComplete(returnIsbnField, bookSuggestions);
    }

    private void setupAutoComplete(TextField textField, List<String> dataList) {
        ContextMenu contextMenu = new ContextMenu();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                contextMenu.hide();
            } else {
                String search = newValue.toLowerCase();
                List<String> matches = dataList.stream().filter(item -> item.toLowerCase().contains(search)).limit(10).collect(Collectors.toList());
                if (!matches.isEmpty()) {
                    populatePopup(matches, textField, contextMenu);
                    if (!contextMenu.isShowing()) contextMenu.show(textField, Side.BOTTOM, 0, 0);
                } else {
                    contextMenu.hide();
                }
            }
        });
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> { if (!newVal) contextMenu.hide(); });
    }

    private void populatePopup(List<String> matches, TextField textField, ContextMenu contextMenu) {
        contextMenu.getItems().clear();
        for (String match : matches) {
            MenuItem item = new MenuItem(match);
            item.setOnAction(e -> {
                textField.setText(match);
                textField.positionCaret(match.length());
                contextMenu.hide();
            });
            contextMenu.getItems().add(item);
        }
    }

    private String extractId(String input) {
        if (input == null || input.isEmpty()) return "";
        if (input.contains("|")) return input.split("\\|")[0].trim();
        return input.trim();
    }

    // --- 3. XỬ LÝ MƯỢN SÁCH (LOGIC MỚI) ---
    @FXML
    public void handleBorrowBook(ActionEvent event) {
        String memId = extractId(memberIdField.getText());
        String bookId = extractId(isbnField.getText());

        if (memId.isEmpty() || bookId.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (!memberRepo.existsById(memId)) { showError("Thành viên không tồn tại!"); return; }
        if (!bookRepo.existsById(bookId)) { showError("Sách không tồn tại!"); return; }

        // --- LẤY NGÀY TỪ DATE PICKER ---
        LocalDate borrowDate = borrowDatePicker.getValue();
        if (borrowDate == null) borrowDate = LocalDate.now(); // Phòng hờ user xóa trắng

        LocalDate dueDate = borrowDate.plusDays(14); // Hạn trả vẫn là 14 ngày kể từ ngày mượn
        // -------------------------------

        BorrowRecord record = new BorrowRecord(memId, bookId, borrowDate, dueDate);

        if (borrowRepo.addBorrowRecord(record)) {
            bookRepo.updateStock(bookId, -1); // Trừ kho
            showSuccess("Mượn thành công! Ngày mượn: " + borrowDate + " (Hạn: " + dueDate + ")");
            loadData();

            memberIdField.clear();
            isbnField.clear();
            borrowDatePicker.setValue(LocalDate.now()); // Reset về hôm nay
        } else {
            showError("Lỗi hệ thống.");
        }
    }

    // --- 4. XỬ LÝ TRẢ SÁCH ---
    @FXML
    public void handleReturnBook(ActionEvent event) {
        String memId = extractId(returnMemberIdField.getText());
        String bookId = extractId(returnIsbnField.getText());

        if (memId.isEmpty() || bookId.isEmpty()) { showError("Nhập thông tin để trả!"); return; }

        BorrowRecord record = borrowRepo.findBorrowingRecord(bookId, memId);

        if (record == null) { showError("Không tìm thấy phiếu mượn!"); return; }

        // Ngày trả luôn là HÔM NAY (Thực tế)
        record.setReturnDate(LocalDate.now());
        record.calculateFine();
        double fine = record.getFine();

        if (fine > 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("QUÁ HẠN - THU PHẠT");
            alert.setHeaderText("Sách này đã quá hạn!");
            alert.setContentText("Ngày mượn: " + record.getBorrowDate() +
                    "\nHạn trả: " + record.getDueDate() +
                    "\n\nSố tiền phạt: " + String.format("%,.0f", fine) + " VNĐ" +
                    "\n\nXác nhận trả sách và thu tiền?");
            if (alert.showAndWait().get() != ButtonType.OK) return;
        }

        if (borrowRepo.returnBook(bookId, memId, fine)) {
            bookRepo.updateStock(bookId, 1); // Cộng kho
            String msg = "Đã trả sách thành công!";
            if (fine > 0) msg += " (Thu phạt: " + String.format("%,.0f", fine) + " VNĐ)";
            showSuccess(msg);
            loadData();
            returnMemberIdField.clear();
            returnIsbnField.clear();
        } else {
            showError("Lỗi hệ thống.");
        }
    }

    // --- Helper Methods ---
    private void loadData() {
        borrowList.clear();
        borrowList.addAll(borrowRepo.getBorrowingList());
    }
    private void showSuccess(String msg) {
        messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        messageLabel.setText(msg);
        Alert a = new Alert(Alert.AlertType.INFORMATION); a.setTitle("Thành công"); a.setContentText(msg); a.showAndWait();
    }
    private void showError(String msg) {
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        messageLabel.setText(msg);
        Alert a = new Alert(Alert.AlertType.ERROR); a.setTitle("Lỗi"); a.setContentText(msg); a.showAndWait();
    }
}