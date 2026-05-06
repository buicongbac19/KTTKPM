package com.ptit.restaurant_management_mono.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ptit.restaurant_management_mono.domain.entity.NhanVien;
import com.ptit.restaurant_management_mono.service.auth.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Body là một đối tượng {@link com.ptit.restaurant_management_mono.domain.entity.NguoiDung}
     * (kiểu cụ thể {@link NhanVien}): chỉ cần {@code username}, {@code password}.
     */
    @PostMapping("/login")
    public NhanVien login(@RequestBody NhanVien nguoiDung) {
        return authService.login(nguoiDung);
    }
}
