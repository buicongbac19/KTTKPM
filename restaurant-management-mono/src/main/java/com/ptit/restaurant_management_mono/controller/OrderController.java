package com.ptit.restaurant_management_mono.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ptit.restaurant_management_mono.domain.entity.Ban;
import com.ptit.restaurant_management_mono.domain.entity.BanDat;
import com.ptit.restaurant_management_mono.domain.entity.MonAn;
import com.ptit.restaurant_management_mono.exception.BusinessException;
import com.ptit.restaurant_management_mono.service.order.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/tables")
    public List<Ban> getListTable() {
        return orderService.getListTable();
    }

    @PostMapping("/reservation-tables/{tableNumber}")
    public BanDat getOrCreateReservationTable(@PathVariable Integer tableNumber) {
        return orderService.getOrCreateReservationTable(tableNumber);
    }

    @GetMapping("/reservation-tables/{reservationTableId}")
    public BanDat getReservationTable(@PathVariable Long reservationTableId) {
        return orderService.getReservationTable(reservationTableId);
    }

    @GetMapping("/dishes")
    public List<MonAn> getDishesByName(@RequestParam(required = false) String keyword) {
        return orderService.getDishesByName(keyword);
    }

    /**
     * Tham số monAnId, soLuong truyền qua query — khớp chữ ký thiết kế addItem(banDatId, monAnId, soLuong),
     * không dùng lớp bọc DTO cho payload.
     */
    @PostMapping("/reservation-tables/{reservationTableId}/items")
    public BanDat addItem(
            @PathVariable Long reservationTableId,
            @RequestParam Long monAnId,
            @RequestParam Integer soLuong) {
        if (soLuong < 1) {
            throw new BusinessException("Số lượng phải lớn hơn hoặc bằng 1.");
        }
        return orderService.addItem(reservationTableId, monAnId, soLuong);
    }

    @PostMapping("/reservation-tables/{reservationTableId}/confirm")
    public BanDat confirmOrder(@PathVariable Long reservationTableId) {
        return orderService.confirmOrder(reservationTableId);
    }

}
