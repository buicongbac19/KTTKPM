package com.ptit.restaurant_management_mono.repository;

import com.ptit.restaurant_management_mono.domain.entity.NhanVien;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
    @Query("""
        SELECT staff
        FROM NhanVien staff
        WHERE LOWER(staff.viTri) = LOWER(:position)
        ORDER BY staff.id ASC
        """)
    List<NhanVien> findByPositionIgnoreCaseOrderByIdAsc(@Param("position") String position);

    default Optional<NhanVien> findFirstByPositionIgnoreCase(String position) {
        return findByPositionIgnoreCaseOrderByIdAsc(position).stream().findFirst();
    }
}
