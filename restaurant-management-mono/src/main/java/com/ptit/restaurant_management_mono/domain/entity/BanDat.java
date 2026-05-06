package com.ptit.restaurant_management_mono.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.restaurant_management_mono.domain.enums.BanDatTrangThai;

import jakarta.persistence.CascadeType;
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

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class BanDat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ban_id", nullable = false)
    private Ban ban;

    @ManyToOne
    @JoinColumn(name = "hoa_don_id")
    @JsonBackReference
    private HoaDon hoaDon;

    @Column(nullable = false)
    private LocalDateTime thoiGianVao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BanDatTrangThai trangThai = BanDatTrangThai.CHO_GOI_MON;

    @JsonManagedReference
    @OneToMany(mappedBy = "banDat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonAnDat> monAnDats = new ArrayList<>();
}
