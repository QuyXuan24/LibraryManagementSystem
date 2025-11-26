package com.library.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.library.model.Book;
import com.library.model.Member;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
// import com.library.repository.BorrowRecordRepository; // Tạm đóng
// import com.library.model.BorrowRecord; // Tạm đóng

public class LibraryService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    // private final BorrowRecordRepository borrowRecordRepository; // Chưa dùng tới

    public LibraryService() {
        this.bookRepository = new BookRepository();
        this.memberRepository = new MemberRepository();
        // this.borrowRecordRepository = new BorrowRecordRepository();
    }

    // ========== 1. QUẢN LÝ SÁCH (Đã cập nhật theo Repository mới) ==========

    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks(); // Sửa findAll -> getAllBooks
    }

    public Optional<Book> getBookById(String id) {
        // Tận dụng hàm search vì Repo chưa có findById riêng
        List<Book> results = bookRepository.search(id);
        for (Book b : results) {
            if (b.getId().equalsIgnoreCase(id)) {
                return Optional.of(b);
            }
        }
        return Optional.empty();
    }

    public boolean addBook(Book book) {
        // Kiểm tra tồn tại (Repo đã có hàm này)
        if (bookRepository.existsById(book.getId())) {
            return false;
        }
        return bookRepository.addBook(book); // Sửa save -> addBook
    }

    public boolean updateBook(Book book) {
        if (!bookRepository.existsById(book.getId())) {
            return false;
        }
        return bookRepository.updateBook(book); // Sửa save -> updateBook
    }

    public boolean deleteBook(String id) {
        if (!bookRepository.existsById(id)) {
            return false;
        }
        return bookRepository.deleteBook(id); // Sửa delete -> deleteBook
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.getAllBooks();
        }
        return bookRepository.search(keyword);
    }

    // ========== 2. QUẢN LÝ THÀNH VIÊN (Đã cập nhật theo Repository mới) ==========

    public List<Member> getAllMembers() {
        return memberRepository.getAllMembers(); // Sửa findAll -> getAllMembers
    }

    public Optional<Member> getMemberById(String id) {
        // Tương tự, dùng search để tìm ID
        List<Member> results = memberRepository.search(id);
        if (!results.isEmpty()) {
            return Optional.of(results.get(0));
        }
        return Optional.empty();
    }

    public boolean addMember(Member member) {
        if (memberRepository.existsById(member.getId())) {
            return false;
        }
        return memberRepository.addMember(member); // Sửa save -> addMember
    }

    public boolean updateMember(Member member) {
        if (!memberRepository.existsById(member.getId())) {
            return false;
        }
        return memberRepository.updateMember(member); // Sửa save -> updateMember
    }

    public boolean deleteMember(String id) {
        if (!memberRepository.existsById(id)) {
            return false;
        }
        return memberRepository.deleteMember(id); // Sửa delete -> deleteMember
    }

    // ========== 3. MƯỢN/TRẢ SÁCH (TẠM THỜI ĐÓNG ĐỂ KHÔNG BÁO LỖI) ==========
    /* Lý do đóng: Mình chưa tạo bảng borrow_records trong Database và chưa viết Repository cho nó.
       Sẽ mở lại ngay khi làm xong module Mượn/Trả.
    */

    /*
    public Optional<BorrowRecord> borrowBook(String memberId, String bookId) {
       // Logic mượn sách sẽ viết lại sau khi có Database chuẩn
       return Optional.empty();
    }

    public boolean returnBook(String recordId) {
       // Logic trả sách sẽ viết lại sau
       return false;
    }
    */

    // ========== 4. THỐNG KÊ ==========

    public int getTotalBooks() {
        return bookRepository.getAllBooks().size();
    }

    public int getTotalMembers() {
        return memberRepository.getAllMembers().size();
    }

    // Các hàm thống kê mượn trả tạm trả về 0
    public int getBorrowedBooksCount() { return 0; }
    public int getOverdueBooksCount() { return 0; }

    public List<?> getBorrowHistory() {
        return Collections.emptyList();
    }
}