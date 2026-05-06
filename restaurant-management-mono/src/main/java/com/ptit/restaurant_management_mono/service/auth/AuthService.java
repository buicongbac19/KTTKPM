package com.ptit.restaurant_management_mono.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ptit.restaurant_management_mono.domain.entity.NhanVien;
import com.ptit.restaurant_management_mono.exception.BusinessException;
import com.ptit.restaurant_management_mono.repository.NhanVienRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final NhanVienRepository nhanVienRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public NhanVien login(NhanVien nguoiDung) {
        if (nguoiDung == null) {
            throw new BusinessException("Thông tin đăng nhập không được để trống.");
        }
        String username = nguoiDung.getUsername();
        String rawPassword = nguoiDung.getPassword();

        if (!StringUtils.hasText(username)) {
            throw new BusinessException("Tên đăng nhập không được để trống.");
        }
        if (!StringUtils.hasText(rawPassword)) {
            throw new BusinessException("Mật khẩu không được để trống.");
        }

        NhanVien nhanVien = nhanVienRepository.findByUsername(username.trim())
                .orElseThrow(() -> new BusinessException("Sai tên đăng nhập hoặc mật khẩu."));

        String storedPassword = nhanVien.getPassword();
        if (!StringUtils.hasText(storedPassword)) {
            throw new BusinessException("Tài khoản chưa được cấu hình mật khẩu.");
        }

        boolean valid;
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            valid = passwordEncoder.matches(rawPassword, storedPassword);
        } else {
            valid = storedPassword.equals(rawPassword);
        }
        if (!valid) {
            throw new BusinessException("Sai tên đăng nhập hoặc mật khẩu.");
        }

        nhanVien.setPassword(null);
        return nhanVien;
    }
}
