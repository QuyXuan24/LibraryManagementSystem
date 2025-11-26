package com.library.model;

public enum MemberType {
	STUDENT(5, 14, "Sinh viên"), TEACHER(10, 30, "Giảng viên"), STAFF(8, 21, "Nhân viên");

	private final int maxBorrowLimit;
	private final int borrowDays;
	private final String vietnameseName;

	MemberType(int maxBorrowLimit, int borrowDays, String vietnameseName) {
		this.maxBorrowLimit = maxBorrowLimit;
		this.borrowDays = borrowDays;
		this.vietnameseName = vietnameseName;
	}

	public int getMaxBorrowLimit() {
		return maxBorrowLimit;
	}

	public int getBorrowDays() {
		return borrowDays;
	}

	public String getVietnameseName() {
		return vietnameseName;
	}
}
