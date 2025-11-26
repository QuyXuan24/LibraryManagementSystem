package com.library.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Member extends Person {
	private LocalDate registrationDate;
	private MemberType memberType;
	private List<BorrowRecord> borrowHistory;
	private int maxBorrowLimit;

	public Member(String id, String name, String email, String phone, LocalDate birthDate, String address,
			MemberType memberType) {
		super(id, name, email, phone, birthDate, address);
		this.registrationDate = LocalDate.now();
		this.memberType = memberType;
		this.borrowHistory = new ArrayList<>();
		this.maxBorrowLimit = memberType.getMaxBorrowLimit();
	}

    public Member(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

	// Kiểm tra xem thành viên có thể mượn thêm sách không
	public boolean canBorrowMoreBooks() {
		long currentBorrowed = borrowHistory.stream().filter(record -> record.getReturnDate() == null).count();
		return currentBorrowed < maxBorrowLimit;
	}

	public void addBorrowRecord(BorrowRecord record) {
		borrowHistory.add(record);
	}

	// Getters và Setters
	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public MemberType getMemberType() {
		return memberType;
	}

	public List<BorrowRecord> getBorrowHistory() {
		return borrowHistory;
	}

	public int getMaxBorrowLimit() {
		return maxBorrowLimit;
	}

	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
		this.maxBorrowLimit = memberType.getMaxBorrowLimit();
	}
}
