INSERT INTO
    ban (so_nguoi_toi_da, so_thu_tu)
SELECT 4, 1
WHERE
    NOT EXISTS (
        SELECT 1
        FROM ban
        WHERE
            so_thu_tu = 1
    );

INSERT INTO
    ban (so_nguoi_toi_da, so_thu_tu)
SELECT 4, 2
WHERE
    NOT EXISTS (
        SELECT 1
        FROM ban
        WHERE
            so_thu_tu = 2
    );

INSERT INTO
    ban (so_nguoi_toi_da, so_thu_tu)
SELECT 6, 3
WHERE
    NOT EXISTS (
        SELECT 1
        FROM ban
        WHERE
            so_thu_tu = 3
    );

INSERT INTO
    ban (so_nguoi_toi_da, so_thu_tu)
SELECT 6, 4
WHERE
    NOT EXISTS (
        SELECT 1
        FROM ban
        WHERE
            so_thu_tu = 4
    );

INSERT INTO
    ban (so_nguoi_toi_da, so_thu_tu)
SELECT 6, 5
WHERE
    NOT EXISTS (
        SELECT 1
        FROM ban
        WHERE
            so_thu_tu = 5
    );

INSERT INTO
    ban (so_nguoi_toi_da, so_thu_tu)
SELECT 8, 6
WHERE
    NOT EXISTS (
        SELECT 1
        FROM ban
        WHERE
            so_thu_tu = 6
    );

INSERT INTO
    mon_an (ten, gia, mo_ta, hinh_anh)
SELECT 'Cơm Gà Xối Mỡ', 45000, 'Cơm gà giòn nước sốt đặc trưng', '/images/com-ga.jpg'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM mon_an
        WHERE
            hinh_anh = '/images/com-ga.jpg'
    );

INSERT INTO
    mon_an (ten, gia, mo_ta, hinh_anh)
SELECT 'Phở Bò Tái Lăn', 55000, 'Phở bò tái lăn đậm vị', '/images/pho-bo.jpg'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM mon_an
        WHERE
            hinh_anh = '/images/pho-bo.jpg'
    );

INSERT INTO
    mon_an (ten, gia, mo_ta, hinh_anh)
SELECT 'Phở Xào', 45000, 'Phở xào rau thịt', '/images/pho-xao.jpg'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM mon_an
        WHERE
            hinh_anh = '/images/pho-xao.jpg'
    );

INSERT INTO
    mon_an (ten, gia, mo_ta, hinh_anh)
SELECT 'Nem Rán Hà Nội', 45000, 'Nem rán giòn truyền thống', '/images/nem-ran.jpg'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM mon_an
        WHERE
            hinh_anh = '/images/nem-ran.jpg'
    );

INSERT INTO
    mon_an (ten, gia, mo_ta, hinh_anh)
SELECT 'Trà Đào Cam Sả', 30000, 'Đồ uống giải khát', '/images/tra-dao.jpg'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM mon_an
        WHERE
            hinh_anh = '/images/tra-dao.jpg'
    );

/* Chuẩn hóa tiếng Việt có dấu cho CSDL đã tồn tại (bản ghi seed cũ không dấu) */
UPDATE mon_an
SET
    ten = 'Cơm Gà Xối Mỡ',
    mo_ta = 'Cơm gà giòn nước sốt đặc trưng'
WHERE
    hinh_anh = '/images/com-ga.jpg';

UPDATE mon_an
SET
    ten = 'Phở Bò Tái Lăn',
    mo_ta = 'Phở bò tái lăn đậm vị'
WHERE
    hinh_anh = '/images/pho-bo.jpg';

UPDATE mon_an
SET
    ten = 'Phở Xào',
    mo_ta = 'Phở xào rau thịt'
WHERE
    hinh_anh = '/images/pho-xao.jpg';

UPDATE mon_an
SET
    ten = 'Nem Rán Hà Nội',
    mo_ta = 'Nem rán giòn truyền thống'
WHERE
    hinh_anh = '/images/nem-ran.jpg';

UPDATE mon_an
SET
    ten = 'Trà Đào Cam Sả',
    mo_ta = 'Đồ uống giải khát'
WHERE
    hinh_anh = '/images/tra-dao.jpg';

INSERT INTO
    nguoi_dung (
        username,
        password,
        ho_va_ten,
        ngay_sinh,
        so_dien_thoai,
        email
    )
SELECT 'cashier_default', NULL, 'Nguyễn Văn A', NULL, '0816119402', 'cashier@local'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM nguoi_dung
        WHERE
            username = 'cashier_default'
    );

INSERT INTO
    nhan_vien (nguoi_dung_id, vi_tri)
SELECT nd.id, 'THU_NGAN'
FROM nguoi_dung nd
WHERE
    nd.username = 'cashier_default'
    AND NOT EXISTS (
        SELECT 1
        FROM nhan_vien nv
        WHERE
            nv.nguoi_dung_id = nd.id
    );

UPDATE nguoi_dung
SET ho_va_ten = 'Nguyễn Văn A'
WHERE
    username = 'cashier_default';

/* Mật khẩu mặc định: cashier123 (BCrypt) — đổi sau khi triển khai thật */
UPDATE nguoi_dung
SET
    password = '$2a$10$mjd3HXU.dCiwBBznXSB.Yuazdi/s9n8yaocYHcImyh466y/0RUSWO'
WHERE
    username = 'cashier_default';