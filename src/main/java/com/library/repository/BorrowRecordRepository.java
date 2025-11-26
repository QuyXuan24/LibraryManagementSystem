package com.library.repository;

import com.library.model.BorrowRecord;
import com.library.util.DatabaseHelper;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordRepository {

    // 1. Lấy danh sách ĐANG MƯỢN (Status = 'BORROWING')
    // Dùng JOIN để lấy thêm tên sách (title) từ bảng books
    public List<BorrowRecord> getBorrowingList() {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT br.*, b.title FROM borrow_records br " +
                "LEFT JOIN books b ON br.book_id = b.id " +
                "WHERE br.status = 'BORROWING'";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm phiếu mượn mới (Create)
    public boolean addBorrowRecord(BorrowRecord record) {
        String sql = "INSERT INTO borrow_records (member_id, book_id, borrow_date, due_date, status, fine) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getMemberId());
            pstmt.setString(2, record.getBookId());
            // Chuyển đổi LocalDate (Java) sang Date (SQL)
            pstmt.setDate(3, Date.valueOf(record.getBorrowDate()));
            pstmt.setDate(4, Date.valueOf(record.getDueDate()));
            pstmt.setString(5, record.getStatus()); // Thường là 'BORROWING'
            pstmt.setDouble(6, 0.0); // Mới mượn thì phạt = 0

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Tìm phiếu mượn cụ thể (Để trả sách)
    // Phải tìm đúng Member đó, Sách đó và ĐANG MƯỢN (tránh lấy nhầm phiếu cũ đã trả)
    public BorrowRecord findBorrowingRecord(String bookId, String memberId) {
        String sql = "SELECT br.*, b.title FROM borrow_records br " +
                "LEFT JOIN books b ON br.book_id = b.id " +
                "WHERE br.book_id = ? AND br.member_id = ? AND br.status = 'BORROWING'";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookId);
            pstmt.setString(2, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy
    }

    // 4. Trả sách (Update) - Cập nhật ngày trả, trạng thái và tiền phạt
    public boolean returnBook(String bookId, String memberId, double fine) {
        String sql = "UPDATE borrow_records SET return_date = ?, status = 'RETURNED', fine = ? " +
                "WHERE book_id = ? AND member_id = ? AND status = 'BORROWING'";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(LocalDate.now())); // Ngày trả là hôm nay
            pstmt.setDouble(2, fine);
            pstmt.setString(3, bookId);
            pstmt.setString(4, memberId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Lấy lịch sử mượn trả (Tất cả trạng thái) - Dùng cho trang Thống kê sau này
    public List<BorrowRecord> getAllHistory() {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT br.*, b.title FROM borrow_records br " +
                "LEFT JOIN books b ON br.book_id = b.id " +
                "ORDER BY br.borrow_date DESC"; // Mới nhất lên đầu

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- HÀM PHỤ TRỢ (Mapping từ SQL -> Java Object) ---
    private BorrowRecord mapRow(ResultSet rs) throws SQLException {
        // Xử lý ngày trả (vì trong DB có thể là NULL)
        Date sqlReturnDate = rs.getDate("return_date");
        LocalDate returnDate = (sqlReturnDate != null) ? sqlReturnDate.toLocalDate() : null;

        return new BorrowRecord(
                rs.getInt("id"),
                rs.getString("member_id"),
                rs.getString("book_id"),
                rs.getString("title"), // Cột này có được nhờ lệnh LEFT JOIN books
                rs.getDate("borrow_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                returnDate,
                rs.getString("status"),
                rs.getDouble("fine")
        );
    }

    public boolean isBookCurrentlyBorrowed(String bookId) {
        String sql = "SELECT 1 FROM borrow_records WHERE book_id = ? AND status = 'BORROWING'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Nếu tìm thấy -> Đang có người mượn
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra xem thành viên có đang mượn sách không (Status = BORROWING)
    public boolean isMemberCurrentlyBorrowing(String memberId) {
        String sql = "SELECT 1 FROM borrow_records WHERE member_id = ? AND status = 'BORROWING'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Nếu có kết quả -> Đang mượn sách
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}