package com.ptit.restaurant_management_mono.domain.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class MonAnDat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ban_dat_id", nullable = false)
    @JsonBackReference
    private BanDat banDat;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mon_an_id", nullable = false)
    private MonAn monAn;

    @Column(nullable = false)
    private Integer soLuong;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal donGia;

    public BigDecimal calculateLineTotal() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
}
