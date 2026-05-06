package com.ptit.restaurant_management_mono.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ptit.restaurant_management_mono.domain.entity.NhanVien;
import com.ptit.restaurant_management_mono.exception.BusinessException;
import com.ptit.restaurant_management_mono.repository.NhanVienRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Đăng nhập: body là {@link com.ptit.restaurant_management_mono.domain.entity.NguoiDung}
     * (ở đây dùng lớp cụ thể {@link NhanVien} để Jackson deserialize được), chỉ cần username + password.
     */
    @Transactional(readOnly = true)
    public NhanVien login(NhanVien thongTinDangNhap) {
        if (thongTinDangNhap == null) {
            throw new BusinessException("Thông tin đăng nhập không được để trống.");
        }
        String username = thongTinDangNhap.getUsername();
        String rawPassword = thongTinDangNhap.getPassword();

        if (!StringUtils.hasText(username)) {
            throw new BusinessException("Tên đăng nhập không được để trống.");
        }
        if (!StringUtils.hasText(rawPassword)) {
            throw new BusinessException("Mật khẩu không được để trống.");
        }

        String trimmedUser = username.trim();
        NhanVien nhanVien = nhanVienRepository.findByUsername(trimmedUser)
                .orElseThrow(() -> new BusinessException("Sai tên đăng nhập hoặc mật khẩu."));

        String stored = nhanVien.getPassword();
        if (!StringUtils.hasText(stored)) {
            throw new BusinessException("Tài khoản chưa được cấu hình mật khẩu.");
        }

        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            if (!passwordEncoder.matches(rawPassword, stored)) {
                throw new BusinessException("Sai tên đăng nhập hoặc mật khẩu.");
            }
        } else if (!rawPassword.equals(stored)) {
            throw new BusinessException("Sai tên đăng nhập hoặc mật khẩu.");
        }

        nhanVien.setPassword(null);
        return nhanVien;
    }
}
