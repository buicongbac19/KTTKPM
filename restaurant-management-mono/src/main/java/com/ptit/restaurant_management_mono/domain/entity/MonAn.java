package com.ptit.restaurant_management_mono.domain.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class MonAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String ten;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal gia;

    @Column(length = 1000)
    private String moTa;

    @Column(length = 500)
    private String hinhAnh;

    @JsonIgnore
    @OneToMany(mappedBy = "monAn")
    private List<MonAnDat> monAnDats = new ArrayList<>();
}
