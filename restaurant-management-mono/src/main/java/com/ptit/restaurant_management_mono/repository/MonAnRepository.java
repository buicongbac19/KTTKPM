package com.ptit.restaurant_management_mono.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ptit.restaurant_management_mono.domain.entity.MonAn;

public interface MonAnRepository extends JpaRepository<MonAn, Long> {

    @Query("""
            SELECT m
            FROM MonAn m
          WHERE (:keyword IS NULL
           OR LOWER(m.ten) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY m.ten ASC
            """)
    List<MonAn> getDishesByName(@Param("keyword") String keyword);

    default List<MonAn> findByName(String keyword) {
      return getDishesByName(keyword);
    }
}
