package com.ptit.restaurant_management_mono.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "nguoi_dung_id")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NhanVien extends NguoiDung {

    @Column(length = 100)
    private String viTri;

    @JsonIgnore
    @OneToMany(mappedBy = "nhanVienThuNgan")
    private List<HoaDon> hoaDonsXuLy = new ArrayList<>();

    public NhanVien(String hoVaTen, String soDienThoai, String email, String viTri) {
        setHoVaTen(hoVaTen);
        setSoDienThoai(soDienThoai);
        setEmail(email);
        this.viTri = viTri;
    }
}
