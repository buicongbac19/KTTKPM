package com.ptit.restaurant_management_mono.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ptit.restaurant_management_mono.domain.entity.HoaDon;
import com.ptit.restaurant_management_mono.domain.entity.KhachHang;
import com.ptit.restaurant_management_mono.domain.entity.MonAnDat;
import com.ptit.restaurant_management_mono.domain.enums.PaymentMethod;
import com.ptit.restaurant_management_mono.exception.BusinessException;
import com.ptit.restaurant_management_mono.service.payment.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Khớp thiết kế: {@code getOrCreateCustomer(soDienThoai, hoVaTen)} — tham số
     * query/form, không bọc DTO.
     */
    @PostMapping("/customers/get-or-create")
    public KhachHang getOrCreateCustomer(
            @RequestParam String soDienThoai,
            @RequestParam String hoVaTen) {
        if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
            throw new BusinessException("hoVaTen không được để trống.");
        }
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new BusinessException("soDienThoai không được để trống.");
        }
        return paymentService.getOrCreateCustomer(soDienThoai.trim(), hoVaTen.trim());
    }

    /**
     * Trả về {@link MonAnDat}[] — khớp biểu đồ lớp module thanh toán (preview).
     */
    @GetMapping("/preview")
    public List<MonAnDat> previewPayment(
            @RequestParam(required = false) List<Integer> soBanThanhToan,
            @RequestParam(required = false) List<Integer> tableNumbers) {
        List<Integer> resolvedTables = soBanThanhToan != null ? soBanThanhToan : tableNumbers;
        if (resolvedTables == null || resolvedTables.isEmpty()) {
            throw new BusinessException("soBanThanhToan không được để trống.");
        }
        return paymentService.previewPayment(resolvedTables);
    }

    /**
     * Khớp thiết kế:
     * {@code confirmPayment(soBanThanhToan, khachHangId, thuNganId, paymentMethod, soTienKhachTra)}.
     * Tham số gửi qua query (POST) — không body {@code HoaDon}.
     */
    @PostMapping("/confirm")
    public HoaDon confirmPayment(
            @RequestParam List<Integer> soBanThanhToan,
            @RequestParam Long khachHangId,
            @RequestParam(required = false) Long thuNganId,
            @RequestParam PaymentMethod paymentMethod,
            @RequestParam(required = false) BigDecimal soTienKhachTra) {
        return paymentService.confirmPayment(
                soBanThanhToan,
                khachHangId,
                thuNganId,
                paymentMethod,
                soTienKhachTra);
    }

    @GetMapping("/invoices/{invoiceId}")
    public HoaDon getInvoice(@PathVariable Long invoiceId) {
        return paymentService.getInvoice(invoiceId);
    }
}
