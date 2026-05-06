package com.ptit.restaurant_management_mono.service.order;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ptit.restaurant_management_mono.domain.entity.Ban;
import com.ptit.restaurant_management_mono.domain.entity.BanDat;
import com.ptit.restaurant_management_mono.domain.entity.MonAn;
import com.ptit.restaurant_management_mono.domain.entity.MonAnDat;
import com.ptit.restaurant_management_mono.domain.enums.BanDatTrangThai;
import com.ptit.restaurant_management_mono.exception.BusinessException;
import com.ptit.restaurant_management_mono.exception.ResourceNotFoundException;
import com.ptit.restaurant_management_mono.repository.BanDatRepository;
import com.ptit.restaurant_management_mono.repository.BanRepository;
import com.ptit.restaurant_management_mono.repository.MonAnRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final List<BanDatTrangThai> ACTIVE_ORDER_STATUSES = List.of(BanDatTrangThai.CHO_GOI_MON);

    private final BanRepository banRepository;
    private final BanDatRepository banDatRepository;
    private final MonAnRepository monAnRepository;

    @Transactional
    public List<Ban> getListTable() {
        return banRepository.getListTable();
    }

    @Transactional
    public BanDat getOrCreateReservationTable(Integer soThuTuBan) {
        Ban ban = banRepository.findByTableNumber(soThuTuBan)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn có số thứ tự: " + soThuTuBan));

        return banDatRepository.findOrCreate(ban, ACTIVE_ORDER_STATUSES);
    }

    @Transactional
    public List<MonAn> getDishesByName(String keyword) {
        String sanitizedKeyword = keyword == null ? null : keyword.trim();
        return monAnRepository.getDishesByName(sanitizedKeyword);
    }

    @Transactional
    public BanDat getReservationTable(Long reservationTableId) {
        return getReservationTableById(reservationTableId);
    }

    @Transactional
    public BanDat addItem(Long reservationTableId, Long monAnId, Integer soLuong) {
        BanDat banDat = getReservationTableById(reservationTableId);
        if (banDat.getTrangThai() != BanDatTrangThai.CHO_GOI_MON) {
            throw new BusinessException("Trạng thái hiện tại không cho phép thêm món.");
        }

        MonAn monAn = monAnRepository.findById(monAnId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn id=" + monAnId));

        MonAnDat monAnDat = new MonAnDat();
        monAnDat.setBanDat(banDat);
        monAnDat.setMonAn(monAn);
        monAnDat.setSoLuong(soLuong);
        monAnDat.setDonGia(monAn.getGia());
        banDat.getMonAnDats().add(monAnDat);

        return banDatRepository.save(banDat);
    }

    /**
     * Xác nhận một lần gọi món với khách (kết thúc thao tác trên giao diện gọi món cho lượt đó).
     * Không đổi {@link BanDat#getTrangThai()}; {@link BanDatTrangThai#DA_HOAN_THANH} chỉ gán khi thanh toán xong.
     */
    @Transactional
    public BanDat confirmOrder(Long reservationTableId) {
        BanDat banDat = getReservationTableById(reservationTableId);
        if (banDat.getTrangThai() != BanDatTrangThai.CHO_GOI_MON) {
            throw new BusinessException("Trạng thái hiện tại không cho phép xác nhận gọi món.");
        }
        if (banDat.getMonAnDats().isEmpty()) {
            throw new BusinessException("Không thể xác nhận bàn đặt khi danh sách món rỗng.");
        }
        return banDatRepository.save(banDat);
    }

    private BanDat getReservationTableById(Long reservationTableId) {
        return banDatRepository.findById(reservationTableId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy phiên bàn đặt id=" + reservationTableId));
    }
}
