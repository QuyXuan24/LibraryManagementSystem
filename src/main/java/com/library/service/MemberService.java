package com.library.service;

import java.util.List;
import java.util.Optional;

import com.library.model.Member;
import com.library.model.MemberType;
import com.library.repository.MemberRepository;

public class MemberService {

    private final MemberRepository memberRepository;

    // Constructor mặc định
    public MemberService() {
        this.memberRepository = new MemberRepository();
    }

    // Constructor cho phép tiêm dependency (Dependency Injection)
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 1. Lấy danh sách (Sửa findAll -> getAllMembers)
    public List<Member> getAllMembers() {
        return memberRepository.getAllMembers();
    }

    // 2. Lấy theo ID
    public Optional<Member> getMemberById(String id) {
        // Tận dụng hàm search của Repo (vì search có tìm theo ID)
        List<Member> results = memberRepository.search(id);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        // Trả về kết quả đầu tiên tìm được
        return Optional.of(results.get(0));
    }

    // 3. Thêm mới (Sửa save -> addMember)
    public boolean addMember(Member member) {
        // Kiểm tra trùng ID
        if (memberRepository.existsById(member.getId())) {
            return false;
        }
        return memberRepository.addMember(member);
    }

    // 4. Cập nhật (Sửa save -> updateMember)
    public boolean updateMember(Member member) {
        // Kiểm tra xem có tồn tại không mới sửa
        if (!memberRepository.existsById(member.getId())) {
            return false;
        }
        return memberRepository.updateMember(member);
    }

    // 5. Xóa (Sửa delete -> deleteMember)
    public boolean deleteMember(String id) {
        if (!memberRepository.existsById(id)) {
            return false;
        }
        return memberRepository.deleteMember(id);
    }

    // 6. Tìm kiếm (Sửa findByName -> search)
    public List<Member> searchMembersByName(String name) {
        return memberRepository.search(name);
    }

    // 7. Tìm theo Email (Xử lý thủ công vì Repo đã bỏ findByEmail)
    public Optional<Member> searchMemberByEmail(String email) {
        // Lấy hết lên rồi lọc bằng Java Stream
        return memberRepository.getAllMembers().stream()
                .filter(m -> m.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    // 8. Thống kê tổng số
    public int getTotalMemberCount() {
        return memberRepository.getAllMembers().size();
    }

    // 9. Thống kê theo loại (Stream Filter)
    public int getMemberCountByType(MemberType type) {
        return (int) memberRepository.getAllMembers().stream()
                .filter(member -> member.getMemberType() == type)
                .count();
    }

    // 10. Kiểm tra tồn tại
    public boolean isMemberExist(String memberId) {
        return memberRepository.existsById(memberId);
    }
}