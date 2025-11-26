package com.library.repository;

import com.library.model.Member;
import com.library.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class này chịu trách nhiệm tương tác trực tiếp với Database MySQL.
 * Đã được chuẩn hóa để khớp với MemberManagementController.
 */
public class MemberRepository {

    // ------------------- 1. CÁC HÀM LẤY DỮ LIỆU (READ) -------------------

    // Hàm lấy tất cả thành viên (Controller đang gọi hàm này)
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    // Hàm tìm kiếm đa năng (Search)
    public List<Member> search(String keyword) {
        List<Member> result = new ArrayList<>();
        // Tìm trong cả ID, Name và Phone (dùng LOWER để tìm không phân biệt hoa thường)
        String sql = "SELECT * FROM members WHERE LOWER(name) LIKE ? OR id LIKE ? OR phone LIKE ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ------------------- 2. CÁC HÀM THAY ĐỔI DỮ LIỆU (CUD) -------------------

    // Hàm thêm mới (Controller đang gọi hàm này)
    public boolean addMember(Member member) {
        // Lưu ý: Chỉ insert 4 trường cơ bản khớp với form nhập liệu
        String sql = "INSERT INTO members (id, name, email, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getName());
            pstmt.setString(3, member.getEmail());
            pstmt.setString(4, member.getPhone());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm thành viên: " + e.getMessage());
            return false;
        }
    }

    // Hàm cập nhật (Controller đang gọi hàm này)
    public boolean updateMember(Member member) {
        String sql = "UPDATE members SET name = ?, email = ?, phone = ? WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getPhone());
            pstmt.setString(4, member.getId()); // ID là điều kiện WHERE

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật: " + e.getMessage());
            return false;
        }
    }

    // Hàm xóa (Controller đang gọi hàm này)
    public boolean deleteMember(String id) {
        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            // Lỗi thường gặp: IntegrityConstraintViolationException (do đang mượn sách)
            System.err.println("Lỗi khi xóa: " + e.getMessage());
            return false;
        }
    }

    // ------------------- 3. HÀM PHỤ TRỢ (HELPER) -------------------

    // Hàm kiểm tra ID đã tồn tại chưa (Dùng khi cần validate)
    public boolean existsById(String id) {
        String sql = "SELECT 1 FROM members WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // Hàm chuyển đổi từ ResultSet (SQL) sang Object (Java)
    // Em đã rút gọn để khớp với Constructor 4 tham số của sếp
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone")
        );
    }
}