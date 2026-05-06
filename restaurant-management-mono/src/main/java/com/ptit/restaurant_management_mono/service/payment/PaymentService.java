package com.ptit.restaurant_management_mono.service.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.ptit.restaurant_management_mono.domain.entity.BanDat;
import com.ptit.restaurant_management_mono.domain.entity.HoaDon;
import com.ptit.restaurant_management_mono.domain.entity.KhachHang;
import com.ptit.restaurant_management_mono.domain.entity.MonAnDat;
import com.ptit.restaurant_management_mono.domain.entity.NhanVien;
import com.ptit.restaurant_management_mono.domain.enums.BanDatTrangThai;
import com.ptit.restaurant_management_mono.domain.enums.HoaDonTrangThai;
import com.ptit.restaurant_management_mono.domain.enums.PaymentMethod;
import com.ptit.restaurant_management_mono.exception.BusinessException;
import com.ptit.restaurant_management_mono.exception.ResourceNotFoundException;
import com.ptit.restaurant_management_mono.repository.BanDatRepository;
import com.ptit.restaurant_management_mono.repository.HoaDonRepository;
import com.ptit.restaurant_management_mono.repository.KhachHangRepository;
import com.ptit.restaurant_management_mono.repository.NhanVienRepository;
import com.ptit.restaurant_management_mono.service.strategy.PaymentContext;
import com.ptit.restaurant_management_mono.service.strategy.PaymentResult;
import com.ptit.restaurant_management_mono.service.strategy.PaymentStrategy;
import com.ptit.restaurant_management_mono.service.strategy.PaymentStrategyFactory;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final List<BanDatTrangThai> PAYABLE_STATUSES = List.of(BanDatTrangThai.CHO_GOI_MON);

    private final BanDatRepository banDatRepository;
    private final HoaDonRepository hoaDonRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;

    @Transactional
    public KhachHang getOrCreateCustomer(String tenKhachHang, String soDienThoaiKhachHang) {
        return resolveCustomer(tenKhachHang, soDienThoaiKhachHang);
    }

    @Transactional
    public List<BanDat> previewPayment(List<Integer> soBanThanhToan) {
        PaymentAggregate aggregate = aggregateByTableNumbers(soBanThanhToan, PAYABLE_STATUSES);
        return aggregate.sessions();
    }

    @Transactional
    public HoaDon confirmPayment(
            List<Integer> soBanThanhToan,
            Long khachHangId,
            Long thuNganId,
            PaymentMethod paymentMethod,
            BigDecimal soTienKhachTra) {
        PaymentAggregate aggregate = aggregateByTableNumbers(soBanThanhToan, PAYABLE_STATUSES);

        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng id=" + khachHangId));
        NhanVien thuNgan = resolveCashier(thuNganId);

        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(paymentMethod);
        BigDecimal effectiveAmountReceived = soTienKhachTra == null ? aggregate.totalAmount() : soTienKhachTra;
        PaymentResult paymentResult = paymentStrategy.process(
                new PaymentContext(paymentMethod, aggregate.totalAmount(), effectiveAmountReceived));

        if (!paymentResult.success()) {
            throw new BusinessException(paymentResult.message());
        }

        HoaDon hoaDon = new HoaDon();
        hoaDon.setTongTien(aggregate.totalAmount());
        hoaDon.setTrangThai(HoaDonTrangThai.DA_THANH_TOAN);
        hoaDon.setPhuongThucThanhToan(paymentMethod);
        hoaDon.setKhachHang(khachHang);
        hoaDon.setNhanVienThuNgan(thuNgan);

        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

        for (BanDat banDat : aggregate.sessions()) {
            banDat.setTrangThai(BanDatTrangThai.DA_HOAN_THANH);
            banDat.setHoaDon(savedHoaDon);
        }
        banDatRepository.saveAll(aggregate.sessions());
        return hoaDonRepository.findById(savedHoaDon.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn id=" + savedHoaDon.getId()));
    }

    @Transactional
    public HoaDon getInvoice(Long hoaDonId) {
        return hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn id=" + hoaDonId));
    }

    private PaymentAggregate aggregateByTableNumbers(List<Integer> requestedTableNumbers,
            Collection<BanDatTrangThai> statuses) {
        if (requestedTableNumbers == null || requestedTableNumbers.isEmpty()) {
            throw new BusinessException("Danh sách bàn thanh toán không được để trống.");
        }

        Set<Integer> normalizedTableNumbers = new TreeSet<>(requestedTableNumbers);
        List<BanDat> banDats = banDatRepository.findByTableNumbersAndStatuses(normalizedTableNumbers, statuses);

        if (banDats.isEmpty()) {
            throw new BusinessException("Không tìm thấy phiên bàn đặt hợp lệ để thanh toán.");
        }

        Set<Integer> foundTableNumbers = banDats.stream()
                .map(b -> b.getBan().getSoThuTu())
                .collect(TreeSet::new, Set::add, Set::addAll);

        List<Integer> missingTables = normalizedTableNumbers.stream()
                .filter(tableNumber -> !foundTableNumbers.contains(tableNumber))
                .toList();

        if (!missingTables.isEmpty()) {
            throw new BusinessException("Một số bàn chưa ở trạng thái có thể thanh toán: " + missingTables);
        }

        List<MonAnDat> items = buildPaymentItems(banDats);
        if (items.isEmpty()) {
            throw new BusinessException("Không có món ăn nào trong các bàn được yêu cầu thanh toán.");
        }

        BigDecimal tienMon = items.stream()
                .map(item -> item.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tongTien = tienMon;

        return new PaymentAggregate(
                new ArrayList<>(normalizedTableNumbers),
                banDats,
                items,
                tienMon,
                tongTien);
    }

    private List<MonAnDat> buildPaymentItems(Collection<BanDat> banDats) {
        return banDats.stream()
                .sorted(Comparator.comparing(b -> b.getBan().getSoThuTu()))
                .flatMap(banDat -> banDat.getMonAnDats().stream())
                .toList();
    }

    private KhachHang resolveCustomer(String tenKhachHang, String soDienThoaiKhachHang) {
        return khachHangRepository.findOrCreate(tenKhachHang, soDienThoaiKhachHang);
    }

    private NhanVien resolveCashier(Long thuNganId) {
        if (thuNganId != null) {
            return nhanVienRepository.findById(thuNganId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thu ngân id=" + thuNganId));
        }

        return nhanVienRepository.findFirstByPositionIgnoreCase("THU_NGAN")
                .orElseGet(() -> {
                    NhanVien defaultCashier = new NhanVien("Nguyen Van A", "0000000000", "cashier@local", "THU_NGAN");
                    defaultCashier.setUsername("cashier_default");
                    return nhanVienRepository.save(defaultCashier);
                });
    }

    private record PaymentAggregate(
            List<Integer> tableNumbersToPay,
            List<BanDat> sessions,
            List<MonAnDat> items,
            BigDecimal foodAmount,
            BigDecimal totalAmount) {
    }
}
