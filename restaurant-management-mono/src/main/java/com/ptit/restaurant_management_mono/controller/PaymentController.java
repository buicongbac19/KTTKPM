package com.ptit.restaurant_management_mono.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ptit.restaurant_management_mono.domain.entity.BanDat;
import com.ptit.restaurant_management_mono.domain.entity.HoaDon;
import com.ptit.restaurant_management_mono.domain.entity.KhachHang;
import com.ptit.restaurant_management_mono.exception.BusinessException;
import com.ptit.restaurant_management_mono.service.payment.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Body là {@link KhachHang}: dùng hoVaTen + soDienThoai để tìm hoặc tạo (id có
     * thể null).
     */
    @PostMapping("/customers/get-or-create")
    public KhachHang getOrCreateCustomer(@RequestBody KhachHang khachHang) {
        if (khachHang == null) {
            throw new BusinessException("KhachHang không được để trống.");
        }
        String hoVaTen = khachHang.getHoVaTen();
        String soDienThoai = khachHang.getSoDienThoai();
        if (hoVaTen == null || hoVaTen.trim().isEmpty()) {
            throw new BusinessException("hoVaTen không được để trống.");
        }
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new BusinessException("soDienThoai không được để trống.");
        }
        return paymentService.getOrCreateCustomer(hoVaTen.trim(), soDienThoai.trim());
    }

    @GetMapping("/preview")
    public List<BanDat> previewPayment(
            @RequestParam(required = false) List<Integer> soBanThanhToan,
            @RequestParam(required = false) List<Integer> tableNumbers) {
        List<Integer> resolvedTables = soBanThanhToan != null ? soBanThanhToan : tableNumbers;
        if (resolvedTables == null || resolvedTables.isEmpty()) {
            throw new BusinessException("soBanThanhToan không được để trống.");
        }
        return paymentService.previewPayment(resolvedTables);
    }

    /**
     * Body là {@link HoaDon} (phiếu xác nhận): khachHang.id, phuongThucThanhToan,
     * dsBanDat[].ban.soThuTu (danh sách bàn thanh toán), nhanVienThuNgan.id (tuỳ
     * chọn),
     * tongTien (tuỳ chọn — khi tiền mặt: số tiền khách trả; null thì lấy theo tổng
     * món).
     */
    @PostMapping("/confirm")
    public HoaDon confirmPayment(@RequestBody HoaDon phieuThanhToan) {
        if (phieuThanhToan == null) {
            throw new BusinessException("HoaDon không được để trống.");
        }
        if (phieuThanhToan.getKhachHang() == null || phieuThanhToan.getKhachHang().getId() == null) {
            throw new BusinessException("khachHang.id không được để trống.");
        }
        if (phieuThanhToan.getPhuongThucThanhToan() == null) {
            throw new BusinessException("phuongThucThanhToan không được để trống.");
        }
        List<Integer> soBanThanhToan = extractSoBan(phieuThanhToan);
        Long khachHangId = phieuThanhToan.getKhachHang().getId();
        Long thuNganId = phieuThanhToan.getNhanVienThuNgan() != null
                ? phieuThanhToan.getNhanVienThuNgan().getId()
                : null;
        BigDecimal soTienKhachTra = phieuThanhToan.getTongTien();

        return paymentService.confirmPayment(
                soBanThanhToan,
                khachHangId,
                thuNganId,
                phieuThanhToan.getPhuongThucThanhToan(),
                soTienKhachTra);
    }

    @GetMapping("/invoices/{invoiceId}")
    public HoaDon getInvoice(@PathVariable Long invoiceId) {
        return paymentService.getInvoice(invoiceId);
    }

    private static List<Integer> extractSoBan(HoaDon input) {
        if (input.getDsBanDat() == null || input.getDsBanDat().isEmpty()) {
            throw new BusinessException("dsBanDat không được để trống.");
        }
        List<Integer> numbers = new ArrayList<>();
        for (BanDat bd : input.getDsBanDat()) {
            if (bd.getBan() == null || bd.getBan().getSoThuTu() == null) {
                throw new BusinessException("Mỗi phần tử dsBanDat phải có ban.soThuTu.");
            }
            numbers.add(bd.getBan().getSoThuTu());
        }
        return numbers;
    }
}
