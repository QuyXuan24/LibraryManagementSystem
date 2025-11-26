package com.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {
    // Các trường dữ liệu khớp với Database
    private int id;                 // Mã phiếu mượn (Database tự sinh)
    private String memberId;        // Mã thành viên
    private String bookId;          // Mã sách (Thay cho ISBN)
    private String bookTitle;       // (Mới) Tên sách để hiển thị lên bảng cho đẹp
    private LocalDate borrowDate;   // Ngày mượn
    private LocalDate dueDate;      // Hạn trả
    private LocalDate returnDate;   // Ngày trả thực tế
    private double fine;            // Tiền phạt (Đổi tên fineAmount -> fine cho khớp DB)
    private String status;          // Trạng thái (BORROWING / RETURNED)

    // --- CONSTRUCTORS ---

    // 1. Constructor đầy đủ (Dùng khi lấy dữ liệu từ Database lên)
    public BorrowRecord(int id, String memberId, String bookId, String bookTitle,
                        LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate,
                        String status, double fine) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
    }

    // 2. Constructor dùng khi tạo phiếu mượn MỚI (Chưa có ID, chưa trả, chưa phạt)
    public BorrowRecord(String memberId, String bookId, LocalDate borrowDate, LocalDate dueDate) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.bookTitle = ""; // Tạm thời để trống, SQL JOIN sẽ lấy sau
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fine = 0.0;
        this.status = "BORROWING"; // Mặc định là đang mượn
    }

    // --- LOGIC NGHIỆP VỤ ---

    // Hàm trả sách
    public void returnBook(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.status = "RETURNED";
        calculateFine(); // Tính tiền ngay khi trả
    }

    // Hàm tính tiền phạt (Logic sếp viết rất chuẩn, em giữ nguyên)
    public void calculateFine() {
        if (returnDate != null && returnDate.isAfter(dueDate)) {
            // Dùng ChronoUnit để tính khoảng cách ngày chuẩn xác
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            if (daysLate > 0) {
                this.fine = daysLate * 5000; // 5000 VND mỗi ngày trễ
            }
        } else {
            this.fine = 0.0;
        }
    }

    // Kiểm tra xem có quá hạn không (Dùng để tô màu đỏ trên giao diện nếu cần)
    public boolean isOverdue() {
        // Quá hạn nếu: Chưa trả VÀ Hôm nay đã vượt quá ngày hẹn
        return "BORROWING".equals(status) && LocalDate.now().isAfter(dueDate);
    }

    // --- GETTERS & SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}