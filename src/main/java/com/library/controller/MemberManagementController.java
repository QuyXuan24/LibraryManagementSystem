package com.library.controller;

import com.library.model.Member;
import com.library.repository.BorrowRecordRepository; // <-- Mới thêm
import com.library.repository.MemberRepository;
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

public class MemberManagementController implements Initializable {

    // --- Khai báo biến giao diện (Phải khớp fx:id bên FXML) ---
    @FXML private TextField searchField;
    @FXML private TableView<Member> memberTableView;
    @FXML private TableColumn<Member, String> memberIdColumn;
    @FXML private TableColumn<Member, String> nameColumn;
    @FXML private TableColumn<Member, String> emailColumn;
    @FXML private TableColumn<Member, String> phoneColumn;
    @FXML private Label messageLabel;

    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;

    // --- Logic ---
    private MemberRepository memberRepository = new MemberRepository();
    // Thêm repo này để kiểm tra xem có đang mượn sách không
    private BorrowRecordRepository borrowRepo = new BorrowRecordRepository();

    private ObservableList<Member> memberList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Cấu hình cột
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // 2. Gán danh sách vào bảng
        memberTableView.setItems(memberList);

        // 3. Load dữ liệu ban đầu
        loadData();

        // 4. Binding: Click vào bảng -> hiện lên form
        memberTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtId.setText(newVal.getId());
                txtName.setText(newVal.getName());
                txtEmail.setText(newVal.getEmail());
                txtPhone.setText(newVal.getPhone());
                txtId.setDisable(true); // Khóa ID khi sửa
            }
        });

        // 5. Tìm kiếm tự động (Real-time search)
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                loadData();
                messageLabel.setText("");
            } else {
                searchMember(newValue.trim());
            }
        });
    }

    // --- CÁC CHỨC NĂNG CRUD ---

    @FXML
    public void handleAddMember() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (id.isEmpty() || name.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập Mã và Tên thành viên!");
            return;
        }

        Member newMember = new Member(id, name, email, phone);
        if (memberRepository.addMember(newMember)) {
            showAlert("Thành công", "Đã thêm: " + name);
            loadData();
            clearFields();
        } else {
            showAlert("Thất bại", "Không thể thêm (ID có thể bị trùng).");
        }
    }

    @FXML
    public void handleUpdateMember() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (id.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn thành viên để sửa!");
            return;
        }

        Member member = new Member(id, name, email, phone);
        if (memberRepository.updateMember(member)) {
            showAlert("Thành công", "Đã cập nhật: " + name);
            loadData();
            clearFields();
            txtId.setDisable(false);
        } else {
            showAlert("Thất bại", "Lỗi cập nhật.");
        }
    }

    @FXML
    public void handleDeleteMember() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn thành viên để xóa!");
            return;
        }

        // --- KIỂM TRA AN TOÀN TRƯỚC KHI XÓA ---
        if (borrowRepo.isMemberCurrentlyBorrowing(id)) {
            showAlert("Cảnh báo", "Không thể xóa! Thành viên này đang mượn sách.\nYêu cầu trả hết sách trước khi xóa tài khoản.");
            return; // Dừng lại ngay
        }
        // ---------------------------------------

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setContentText("Bạn có chắc muốn xóa thành viên mã " + id + "?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (memberRepository.deleteMember(id)) {
                showAlert("Thành công", "Đã xóa thành viên " + id);
                loadData();
                clearFields();
                txtId.setDisable(false);
            } else {
                showAlert("Thất bại", "Không thể xóa (Lỗi Database).");
            }
        }
    }

    @FXML
    public void handleClear() {
        clearFields();
        txtId.setDisable(false);
        memberTableView.getSelectionModel().clearSelection();
        loadData();
    }

    // --- CÁC HÀM PHỤ TRỢ ---

    private void loadData() {
        List<Member> list = memberRepository.getAllMembers();
        memberList.clear();
        memberList.addAll(list);
    }

    private void searchMember(String keyword) {
        List<Member> list = memberRepository.search(keyword);
        memberList.clear();
        memberList.addAll(list);
        if (list.isEmpty()) {
            messageLabel.setText("Không tìm thấy kết quả nào.");
            messageLabel.setStyle("-fx-text-fill: red;");
        } else {
            messageLabel.setText("Tìm thấy " + list.size() + " kết quả.");
            messageLabel.setStyle("-fx-text-fill: green;");
        }
    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
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