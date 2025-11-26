package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

public class BookService {

    private final BookRepository bookRepository;

    // Constructor
    public BookService() {
        this.bookRepository = new BookRepository();
    }

    // --- 1. LẤY DỮ LIỆU ---

    // Lấy toàn bộ sách (Sửa findAll -> getAllBooks)
    public List<Book> getAllBooks() {
        return bookRepository.getAllBooks();
    }

    // Tìm kiếm sách (Sửa findByTitle... -> search)
    public List<Book> searchBooks(String keyword) {
        return bookRepository.search(keyword);
    }

    // Lấy sách theo ID (Logic thủ công vì Repo chưa có findById riêng)
    public Optional<Book> getBookById(String id) {
        List<Book> results = bookRepository.search(id);
        // Vì hàm search tìm cả ID, nên nếu có kết quả trùng ID thì trả về
        for (Book b : results) {
            if (b.getId().equalsIgnoreCase(id)) {
                return Optional.of(b);
            }
        }
        return Optional.empty();
    }

    // --- 2. XỬ LÝ NGHIỆP VỤ (CUD) ---

    // Thêm sách mới
    public boolean addBook(Book book) {
        // Validate: Kiểm tra xem sách đã tồn tại chưa
        if (isBookExist(book.getId())) {
            return false; // Trùng ID thì không thêm
        }
        return bookRepository.addBook(book);
    }

    // Cập nhật sách
    public boolean updateBook(Book book) {
        // Validate: Phải tồn tại mới cho sửa
        if (!isBookExist(book.getId())) {
            return false;
        }
        return bookRepository.updateBook(book);
    }

    // Xóa sách
    public boolean deleteBook(String id) {
        // Validate: Phải tồn tại mới xóa
        if (!isBookExist(id)) {
            return false;
        }
        return bookRepository.deleteBook(id);
    }

    // --- 3. HÀM PHỤ TRỢ ---

    // Kiểm tra sách có tồn tại không (Dựa vào ID)
    public boolean isBookExist(String id) {
        List<Book> list = bookRepository.search(id);
        for (Book b : list) {
            if (b.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    // Thống kê tổng số sách
    public int getTotalBookCount() {
        return bookRepository.getAllBooks().size();
    }
}