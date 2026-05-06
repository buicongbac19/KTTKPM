package com.ptit.restaurant_management_mono.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ptit.restaurant_management_mono.domain.entity.Ban;
import com.ptit.restaurant_management_mono.domain.entity.BanDat;
import com.ptit.restaurant_management_mono.domain.enums.BanDatTrangThai;

public interface BanDatRepository extends JpaRepository<BanDat, Long> {

    @EntityGraph(attributePaths = { "ban", "monAnDats", "monAnDats.monAn" })
    @Query("""
            SELECT session
            FROM BanDat session
            WHERE session.ban = :table
              AND session.trangThai IN :statuses
            ORDER BY session.thoiGianVao DESC
            """)
    List<BanDat> findByTableAndStatusesOrderByCheckInTimeDesc(
            @Param("table") Ban table,
            @Param("statuses") Collection<BanDatTrangThai> statuses);

    default BanDat findOrCreate(Ban table, Collection<BanDatTrangThai> statuses) {
        Optional<BanDat> existingSession = findByTableAndStatusesOrderByCheckInTimeDesc(table, statuses).stream()
                .findFirst();
        if (existingSession.isPresent()) {
            return existingSession.get();
        }

        BanDat newSession = new BanDat();
        newSession.setBan(table);
        newSession.setThoiGianVao(LocalDateTime.now());
        newSession.setTrangThai(BanDatTrangThai.CHO_GOI_MON);
        return save(newSession);
    }

    @Override
    @EntityGraph(attributePaths = { "ban", "monAnDats", "monAnDats.monAn" })
    Optional<BanDat> findById(Long id);

    @EntityGraph(attributePaths = { "ban", "monAnDats", "monAnDats.monAn" })
    @Query("""
            SELECT session
            FROM BanDat session
            WHERE session.ban.soThuTu IN :tableNumbers
              AND session.trangThai IN :statuses
            """)
    List<BanDat> findByTableNumbersAndStatuses(
            @Param("tableNumbers") Collection<Integer> tableNumbers,
            @Param("statuses") Collection<BanDatTrangThai> statuses);
}
