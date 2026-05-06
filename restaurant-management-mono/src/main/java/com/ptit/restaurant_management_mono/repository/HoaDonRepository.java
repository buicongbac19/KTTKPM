package com.ptit.restaurant_management_mono.repository;

import com.ptit.restaurant_management_mono.domain.entity.HoaDon;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {

	@EntityGraph(attributePaths = {
			"khachHang",
			"nhanVienThuNgan",
			"dsBanDat",
			"dsBanDat.ban",
			"dsBanDat.monAnDats",
			"dsBanDat.monAnDats.monAn"
	})
	@Override
	Optional<HoaDon> findById(Long id);
}
