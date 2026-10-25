# Work Breakdown Structure: Ứng dụng tạo Font từ chữ viết tay (Android)

## 1. Objective
Xây dựng hoàn chỉnh ứng dụng Android offline cho phép người dùng tự vẽ/tải chữ viết tay mẫu, ghép dấu tiếng Việt tự động và xuất ra file TrueType Font (`.ttf`) hỗ trợ đầy đủ tiếng Việt (NFC/NFD, 134 ký tự precomposed).

## 2. Deliverables
1. **Module Glyph Canvas:** Giao diện canvas cảm ứng cho phép người dùng vẽ chữ cái gốc, nguyên âm đặc thù và các dấu thanh/dấu mũ độc lập.
2. **Module Vectorization / Glyph Trace:** Chuyển đổi nét vẽ bitmap/canvas thành đường cong vector (contours/beziers) đạt chuẩn font TrueType.
3. **Module Glyph Composition:** Tự động định vị, căn chỉnh tỷ lệ và ghép dấu (acute, grave, circumflex, breve, horn,...) vào chữ gốc; hỗ trợ chỉnh sửa/ghi đè thủ công (manual override).
4. **Module TTF Builder:** Thư viện đóng gói các bảng TTF (`head`, `hhea`, `maxp`, `cmap`, `glyf`, `loca`, `name`, `post`, `OS/2`) hoạt động offline trên Android.
5. **UI/UX App Flow:** Flow hướng dẫn từng bước vẽ mẫu chữ -> xem trước chữ ghép -> gõ thử văn bản mẫu tiếng Việt -> xuất & cài đặt/chia sẻ file `.ttf`.
6. **Automated Test Suite & Verification:** Kiểm thử độc lập đảm bảo phông tạo ra tương thích hệ thống, không rơi vào `.notdef`, render chính xác câu mẫu corpus.

## 3. Task Hierarchy & Status

| Task ID | Task Name | Dependencies | Status |
|---|---|---|---|
| **1.0** | **Vietnamese Specification Core** | - | **Completed** |
| 1.1 | Định nghĩa bảng ký tự tiếng Việt (134 precomposed + Latin + số + dấu câu) | - | Completed |
| 1.2 | Xử lý chuẩn hoá Unicode NFC/NFD | 1.1 | Completed |
| 1.3 | Bộ Test Corpus mẫu và Unit test độ phủ | 1.1, 1.2 | Completed |
| 1.4 | Bảng công thức ánh xạ ghép dấu (Composition Table) | 1.1 | Completed |
| **2.0** | **Handwriting Capture & Canvas UI** | **1.0** | **Completed** |
| 2.1 | Canvas vẽ nét mượt mà trên Jetpack Compose (Path smoothing, Bezier interpolation) | - | Completed |
| 2.2 | Khung template hướng dẫn nét mẫu (baseline, ascender, descender, cap-height guides) | 2.1 | Completed |
| 2.3 | Quy trình vẽ tối giản: Bộ chữ gốc Latin + nguyên âm riêng (`ă, â, ê, ô, ơ, ư`) + bộ dấu rời | 2.2 | Completed |
| 2.4 | Chức năng Undo / Redo / Clear / Tẩy nét | 2.1 | Completed |
| **3.0** | **Vectorization & Contour Processing** | **2.0** | **In Progress** |
| 3.1 | Thuật toán vector hoá nét vẽ (Potrace-like hoặc contour tracing từ nét vector canvas) | 2.1 | Not Started |
| 3.2 | Chuẩn hoá toạ độ glyph về Em-Square (e.g., 1024x1024 hoặc 2048x2048) | 3.1 | Not Started |
| 3.3 | Tính toán metrics: Advance width, Left/Right side bearings, Bounding box | 3.2 | Not Started |
| **4.0** | **Glyph Composition Engine** | **1.4, 3.0** | **Not Started** |
| 4.1 | Thuật toán tự động ghép diacritics vào base glyph theo tỷ lệ bounding box | 1.4, 3.3 | Not Started |
| 4.2 | Xử lý dấu xếp chồng (stacked diacritics: `ấ, ầ, ẩ, ẫ, ậ, ắ, ằ, ẳ, ẵ, ặ`) | 4.1 | Not Started |
| 4.3 | Xử lý chống va chạm nét và chống cắt mép (clipping prevention) | 4.1 | Not Started |
| 4.4 | Chế độ ghi đè thủ công (manual override): cho phép vẽ trực tiếp ký tự ghép | 4.1 | Not Started |
| **5.0** | **TrueType Font (TTF) Generator Engine** | **3.0, 4.0** | **Not Started** |
| 5.1 | Xây dựng cấu trúc Binary TTF tables writer (`head`, `hhea`, `maxp`, `OS/2`) | - | Not Started |
| 5.2 | Xây dựng bảng `cmap` định dạng Format 4 (ánh xạ Unicode BMP cho 134 ký tự tiếng Việt + Latin) | 1.1, 5.1 | Not Started |
| 5.3 | Xây dựng bảng `glyf` và `loca` đóng gói toạ độ và contours | 3.2, 5.1 | Not Started |
| 5.4 | Xây dựng bảng `name` và `post` (tên font, bản quyền, postscript name) | 5.1 | Not Started |
| 5.5 | Trình đóng gói file TTF hoàn chỉnh và tính toán checksum | 5.2, 5.3, 5.4 | Not Started |
| **6.0** | **App UI & User Flow** | **2.0, 4.0, 5.0** | **In Progress** |
| 6.1 | Màn hình danh sách ký tự cần hoàn thành (Progress tracking) | 2.3 | Completed |
| 6.2 | Màn hình xem trước font chữ động (Live Preview gõ thử văn bản) | 5.5 | Completed |
| 6.3 | Chức năng xuất file `.ttf` ra bộ nhớ máy / Chia sẻ file font | 5.5 | Not Started |
| **7.0** | **Testing & Validation** | **5.0, 6.0** | **Not Started** |
| 7.1 | Tích hợp kiểm thử tự động render font TTF tạo ra với bộ test corpus | 1.3, 5.5 | Not Started |
| 7.2 | Kiểm thử rendering độc lập trên Android Canvas / Typeface | 5.5 | Not Started |

## 4. Success Criteria
1. Người dùng chỉ cần vẽ ~40-45 nét cơ bản (thay vì 134 ký tự lẻ) để sinh đủ bộ chữ tiếng Việt.
2. File `.ttf` sinh ra hoàn toàn offline trên thiết bị Android trong thời gian < 3 giây.
3. File `.ttf` cài đặt và gõ được trên máy tính hoặc ứng dụng thứ ba (Word, Photoshop, CapCut, v.v.).
4. Đạt chuẩn 100% các tiêu chí chấp nhận trong đặc tả kỹ thuật tiếng Việt:
   - Đủ 134 ký tự precomposed hoa và thường.
   - Nhận diện đúng cả văn bản NFC và NFD.
   - Dấu thanh và dấu mũ không bị chồng lấn méo mó hoặc tràn ngoài bounding box.
   - Không bị rơi vào glyph `.notdef`.
