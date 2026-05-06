package com.ptit.restaurant_management_mono.repository;

import com.ptit.restaurant_management_mono.domain.entity.KhachHang;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {
    @Query("SELECT customer FROM KhachHang customer WHERE customer.soDienThoai = :phoneNumber")
    Optional<KhachHang> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    default KhachHang findOrCreate(String hoVaTen, String soDienThoai) {
        KhachHang khachHang = findByPhoneNumber(soDienThoai)
                .orElseGet(() -> new KhachHang(hoVaTen, soDienThoai));

        khachHang.setHoVaTen(hoVaTen);
        khachHang.setSoDienThoai(soDienThoai);
        return save(khachHang);
    }
}
