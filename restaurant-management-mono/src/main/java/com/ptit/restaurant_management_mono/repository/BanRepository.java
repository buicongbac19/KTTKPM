package com.ptit.restaurant_management_mono.repository;

import com.ptit.restaurant_management_mono.domain.entity.Ban;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BanRepository extends JpaRepository<Ban, Long> {
    @Query("SELECT b FROM Ban b ORDER BY b.soThuTu ASC")
    List<Ban> getListTable();

    @Query("SELECT b FROM Ban b WHERE b.soThuTu = :tableNumber")
    Optional<Ban> findByTableNumber(@Param("tableNumber") Integer tableNumber);
}
