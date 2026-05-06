package com.ptit.restaurant_management_mono.domain.enums;

/**
 * CHO_GOI_MON — phiên bàn đặt đang mở (gọi món, có thể đã bấm xác nhận gọi món), chờ thanh toán;
 * DA_HOAN_THANH — chỉ sau khi thanh toán xong ({@code PaymentService#confirmPayment}), khi đã gắn {@code BanDat#hoaDon}.
 */
public enum BanDatTrangThai {
    CHO_GOI_MON,
    DA_HOAN_THANH
}
