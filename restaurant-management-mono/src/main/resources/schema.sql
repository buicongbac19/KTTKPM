CREATE DATABASE IF NOT EXISTS restaurant_management_mono CHARACTER
SET
    utf8mb4 COLLATE utf8mb4_0900_as_ci;

USE restaurant_management_mono;

CREATE TABLE IF NOT EXISTS nguoi_dung (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(100) NULL,
    password VARCHAR(255) NULL,
    ho_va_ten VARCHAR(255) NOT NULL,
    ngay_sinh DATE NULL,
    so_dien_thoai VARCHAR(20) NOT NULL,
    email VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_nguoi_dung_username (username),
    KEY idx_nguoi_dung_so_dien_thoai (so_dien_thoai)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_ci;

CREATE TABLE IF NOT EXISTS khach_hang (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ho_va_ten VARCHAR(255) NOT NULL,
    so_dien_thoai VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_khach_hang_so_dien_thoai (so_dien_thoai)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_ci;

CREATE TABLE IF NOT EXISTS nhan_vien (
    nguoi_dung_id BIGINT NOT NULL,
    vi_tri VARCHAR(100) NULL,
    PRIMARY KEY (nguoi_dung_id),
    CONSTRAINT fk_nhan_vien_nguoi_dung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_ci;

CREATE TABLE IF NOT EXISTS mon_an (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ten VARCHAR(255) NOT NULL,
    gia DECIMAL(15, 2) NOT NULL,
    mo_ta VARCHAR(1000) NULL,
    hinh_anh VARCHAR(500) NULL,
    PRIMARY KEY (id),
    KEY idx_mon_an_ten (ten)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_ci;

CREATE TABLE IF NOT EXISTS ban (
    id BIGINT NOT NULL AUTO_INCREMENT,
    so_nguoi_toi_da INT NOT NULL,
    so_thu_tu INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_ban_so_thu_tu (so_thu_tu)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_ci;

CREATE TABLE IF NOT EXISTS hoa_don (
    id BIGINT NOT NULL AUTO_INCREMENT,
    tong_tien DECIMAL(15, 2) NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    phuong_thuc_thanh_toan VARCHAR(20) NULL,
    khach_hang_id BIGINT NULL,
    nhan_vien_thu_ngan_id BIGINT NULL,
    PRIMARY KEY (id),
    KEY idx_hoa_don_khach_hang_id (khach_hang_id),
    KEY idx_hoa_don_nhan_vien_thu_ngan_id (nhan_vien_thu_ngan_id),
    CONSTRAINT fk_hoa_don_khach_hang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang (id),
    CONSTRAINT fk_hoa_don_nhan_vien FOREIGN KEY (nhan_vien_thu_ngan_id) REFERENCES nhan_vien (nguoi_dung_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_ci;

CREATE TABLE IF NOT EXISTS ban_dat (
    id BIGINT NOT NULL AUTO_INCREMENT,
    thoi_gian_vao DATETIME NOT NULL,
    trang_thai VARCHAR(30) NOT NULL,
    ban_id BIGINT NOT NULL,
    hoa_don_id BIGINT NULL,
    PRIMARY KEY (id),
    KEY idx_ban_dat_ban_id (ban_id),
    KEY idx_ban_dat_hoa_don_id (hoa_don_id),
    CONSTRAINT fk_ban_dat_ban FOREIGN KEY (ban_id) REFERENCES ban (id),
    CONSTRAINT fk_ban_dat_hoa_don FOREIGN KEY (hoa_don_id) REFERENCES hoa_don (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_ci;

CREATE TABLE IF NOT EXISTS mon_an_dat (
    id BIGINT NOT NULL AUTO_INCREMENT,
    so_luong INT NOT NULL,
    don_gia DECIMAL(15, 2) NOT NULL,
    mon_an_id BIGINT NOT NULL,
    ban_dat_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_mon_an_dat_mon_an_id (mon_an_id),
    KEY idx_mon_an_dat_ban_dat_id (ban_dat_id),
    CONSTRAINT fk_mon_an_dat_mon_an FOREIGN KEY (mon_an_id) REFERENCES mon_an (id),
    CONSTRAINT fk_mon_an_dat_ban_dat FOREIGN KEY (ban_dat_id) REFERENCES ban_dat (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_as_ci;