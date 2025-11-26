package com.library.model;

public enum BorrowStatus {
	BORROWED("Đang mượn"), RETURNED("Đã trả"), OVERDUE("Quá hạn");

	private final String vietnameseName;

	BorrowStatus(String vietnameseName) {
		this.vietnameseName = vietnameseName;
	}

	public String getVietnameseName() {
		return vietnameseName;
	}
}
