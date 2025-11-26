package com.library.repository;

import com.library.util.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatisticsRepository {

    // 1. Tổng thành viên
    public int getTotalMembers() {
        return executeCountQuery("SELECT COUNT(*) FROM members");
    }

    // 2. Tổng sách
    public int getTotalBookQuantity() {
        return executeCountQuery("SELECT SUM(quantity) FROM books");
    }

    // 3. Đang mượn
    public int getActiveBorrowCount() {
        return executeCountQuery("SELECT COUNT(*) FROM borrow_records WHERE status = 'BORROWING'");
    }

    // 4. Tổng tiền phạt (Doanh thu)
    public double getTotalFineCollected() {
        String sql = "SELECT SUM(fine) FROM borrow_records WHERE status = 'RETURNED'";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // 5. Dữ liệu Biểu đồ Tròn: Thống kê sách theo thể loại
    public Map<String, Integer> getBookCategoryData() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT category, COUNT(*) FROM books GROUP BY category";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String cat = rs.getString(1);
                if (cat == null || cat.isEmpty()) cat = "Khác";
                data.put(cat, rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 6. Dữ liệu Biểu đồ Cột: Top 5 sách được mượn nhiều nhất
    public Map<String, Integer> getTopBorrowedBooks() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT b.title, COUNT(*) as count " +
                "FROM borrow_records br " +
                "JOIN books b ON br.book_id = b.id " +
                "GROUP BY b.title " +
                "ORDER BY count DESC " +
                "LIMIT 5"; // Lấy top 5
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString("title"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    private int executeCountQuery(String sql) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}