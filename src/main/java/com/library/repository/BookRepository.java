package com.library.repository;

import com.library.model.Book;
import com.library.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {

    // 1. Lấy toàn bộ sách từ Database
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // 2. Tìm kiếm đa năng (Tên, Tác giả, Mã ID)
    public List<Book> search(String keyword) {
        List<Book> result = new ArrayList<>();
        // Tìm trong Tên, Tác giả hoặc ID (Category tương ứng với Genre cũ)
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? OR id LIKE ? OR LOWER(category) LIKE ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 3. Thêm sách mới
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (id, title, author, category, quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getId());        // getId thay cho getIsbn
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());    // getAuthor thay cho getAuthors
            pstmt.setString(4, book.getCategory());  // getCategory thay cho getGenre
            pstmt.setInt(5, book.getQuantity());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm sách: " + e.getMessage());
            return false;
        }
    }

    // 4. Cập nhật sách
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, category=?, quantity=? WHERE id=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getCategory());
            pstmt.setInt(4, book.getQuantity());
            pstmt.setString(5, book.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 5. Xóa sách
    public boolean deleteBook(String id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Không thể xóa sách (có thể đang được mượn).");
            return false;
        }
    }

    // Hàm phụ trợ: Chuyển dữ liệu SQL thành Object Java
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getInt("quantity")
        );
    }

    // Hàm kiểm tra sách có tồn tại không (Dùng cho Validate)
    public boolean existsById(String id) {
        String sql = "SELECT 1 FROM books WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Nếu có kết quả trả về -> Tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- HÀM CẬP NHẬT SỐ LƯỢNG KHO ---
    // amount = -1 (Mượn -> Giảm)
    // amount = 1 (Trả -> Tăng)
    public boolean updateStock(String bookId, int amount) {
        String sql = "UPDATE books SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, amount);
            pstmt.setString(2, bookId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}