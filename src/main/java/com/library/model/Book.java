package com.library.model;

import javafx.beans.property.*;

public class Book {
    // Sử dụng Property để Binding dữ liệu lên TableView tự động
    private final StringProperty id;        // Khớp với cột 'id' trong DB
    private final StringProperty title;     // Khớp với cột 'title'
    private final StringProperty author;    // Khớp với cột 'author'
    private final StringProperty category;  // Khớp với cột 'category'
    private final IntegerProperty quantity; // Khớp với cột 'quantity'

    // --- 1. CONSTRUCTOR ---

    // Constructor mặc định (Bắt buộc phải có để tránh lỗi ở một số thư viện)
    public Book() {
        this("", "", "", "", 0);
    }

    // Constructor đầy đủ tham số
    public Book(String id, String title, String author, String category, int quantity) {
        this.id = new SimpleStringProperty(id);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.category = new SimpleStringProperty(category);
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    // --- 2. JAVAFX PROPERTY METHODS (Cực quan trọng cho TableView) ---
    // TableView sẽ gọi các hàm này để hiển thị dữ liệu

    public StringProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }
    public StringProperty categoryProperty() { return category; }
    public IntegerProperty quantityProperty() { return quantity; }

    // --- 3. STANDARD GETTERS (Để lấy dữ liệu xử lý logic) ---

    public String getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getAuthor() { return author.get(); }
    public String getCategory() { return category.get(); }
    public int getQuantity() { return quantity.get(); }

    // --- 4. STANDARD SETTERS (Để gán dữ liệu) ---

    public void setId(String id) { this.id.set(id); }
    public void setTitle(String title) { this.title.set(title); }
    public void setAuthor(String author) { this.author.set(author); }
    public void setCategory(String category) { this.category.set(category); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }

    // --- 5. LOGIC PHỤ TRỢ ---

    @Override
    public String toString() {
        return getTitle(); // Để hiển thị đẹp nếu bỏ vào ComboBox
    }
}