package com.ptit.restaurant_management_mono.domain.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.restaurant_management_mono.domain.enums.HoaDonTrangThai;
import com.ptit.restaurant_management_mono.domain.enums.PaymentMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Hoá đơn — thuộc tính khớp thiết kế CSDL (mục 5.2): id, tongTien, trangThai, phuongThucThanhToan
 * và quan hệ tới khách hàng, nhân viên thu ngân, danh sách bàn đặt.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Khi nhận từ JSON xác nhận thanh toán có thể null (không gửi tongTien => không ép số tiền khách trả). */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal tongTien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HoaDonTrangThai trangThai = HoaDonTrangThai.CHUA_THANH_TOAN;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod phuongThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "khach_hang_id")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "nhan_vien_thu_ngan_id")
    private NhanVien nhanVienThuNgan;

    @JsonManagedReference
    @OneToMany(mappedBy = "hoaDon")
    private List<BanDat> dsBanDat = new ArrayList<>();
}
