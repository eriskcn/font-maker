# Font Maker (Android)

Ứng dụng Android mã nguồn mở cho phép người dùng tự vẽ chữ viết tay và xuất ra tệp phông chữ TrueType (`.ttf`) hoàn chỉnh, hoạt động hoàn toàn offline.

Đặc biệt tối ưu hóa và hỗ trợ toàn diện tiếng Việt: hỗ trợ đầy đủ 134 ký tự có dấu (precomposed), xử lý chuẩn hóa Unicode NFC/NFD, tự động ghép dấu thanh và dấu phụ từ các nét vẽ thành phần độc lập.

---

## Tính năng chính

- **Hỗ trợ tiếng Việt đầy đủ (Full Vietnamese Support):**
  - Đầy đủ 134 ký tự tiếng Việt có dấu (hoa và thường).
  - Chuẩn hoá Unicode NFC và xử lý canonical equivalence cho chuỗi NFD.
  - Phụ âm `đ`, `Đ` và toàn bộ 12 nguyên âm biến thể (`ă`, `â`, `e`, `ê`, `i`, `o`, `ô`, `ơ`, `u`, `ư`, `y`).
- **Quy trình vẽ tối giản (Glyph Composition Engine):**
  - Không bắt buộc người dùng vẽ thủ công từng ký tự có dấu.
  - Vẽ chữ cái cơ bản + các dấu rời (`sắc`, `huyền`, `hỏi`, `ngã`, `nặng`, `mũ`, `trăng`, `móc`).
  - Hệ thống tự động căn chỉnh và ghép dấu, hỗ trợ ghi đè nét vẽ thủ công khi cần.
- **Tạo phông chữ TrueType (`.ttf`) thuần Offline:**
  - Tự đóng gói cấu trúc nhị phân TTF (`head`, `hhea`, `maxp`, `cmap`, `glyf`, `loca`, `name`, `post`, `OS/2`) ngay trên thiết bị.
  - Phông chữ tạo ra tương thích chuẩn với mọi hệ điều hành (Android, iOS, Windows, macOS, Linux).
- **Bộ kiểm thử độc lập (Test Corpus):**
  - Tích hợp bộ câu và cụm từ tiếng Việt mẫu để kiểm tra chất lượng hiển thị và độ phủ glyph trước khi xuất file.

---

## Cấu trúc dự án

```text
font-maker/
├── app/                  # Ứng dụng Android (Jetpack Compose UI)
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/com/fontmaker/app/
├── core/                 # Thư viện thuần Kotlin JVM
│   └── src/
│       ├── main/kotlin/com/fontmaker/core/vietnamese/
│       │   ├── VietnameseRepertoire.kt       # Bảng ký tự và chuẩn hoá Unicode
│       │   ├── VietnameseTestCorpus.kt       # Bộ câu mẫu kiểm thử
│       │   └── GlyphComposition.kt          # Bảng và logic ghép dấu
│       └── test/kotlin/com/fontmaker/core/vietnamese/
│           └── VietnameseRepertoireTest.kt   # Unit test kiểm tra độ phủ
├── wbs/                  # Quản lý tiến độ theo Work Breakdown Structure
│   ├── WBS.md
│   └── reports/
├── build_and_run.sh      # Script tự động build & deploy qua ADB
└── VIETNAMESE_SUPPORT.md # Đặc tả kỹ thuật chi tiết về hỗ trợ tiếng Việt
```

---

## Yêu cầu môi trường

- **Hệ điều hành:** Linux, macOS hoặc Windows (WSL)
- **Java:** OpenJDK 21
- **Android SDK:** Compile SDK 34, Min SDK 26
- **Gradle:** Bản phân phối Gradle 8.7 (tự động tải qua script/wrapper)

---

## Cài đặt và Chạy thử

### 1. Chuẩn bị môi trường trên Ubuntu/Debian

Cài đặt Java 21, unzip và platform-tools (ADB):
```bash
sudo apt update
sudo apt install -y openjdk-21-jdk unzip adb
```

### 2. Thiết lập Android SDK
Tạo file `local.properties` tại thư mục gốc và trỏ đến thư mục SDK của bạn:
```bash
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

### 3. Build và chạy trên thiết bị

Kết nối thiết bị Android qua USB (bật USB Debugging), sau đó thực thi script:
```bash
chmod +x gradlew build_and_run.sh
./build_and_run.sh
```

Script sẽ tự động:
1. Kiểm tra môi trường và cấu hình SDK.
2. Tải và cấu hình Gradle.
3. Biên dịch bản debug APK (`:app:assembleDebug`).
4. Cài đặt trực tiếp lên thiết bị Android qua `adb` và khởi chạy màn hình chính.

---

## Chạy Unit Test

Kiểm tra độ phủ bộ ký tự tiếng Việt và logic chuẩn hoá Unicode:
```bash
./gradlew :core:test
```

---

## Lộ trình phát triển (Roadmap)

Xem chi tiết phân rã công việc và tiến độ tại [WBS.md](wbs/WBS.md).
- [x] Khởi tạo dự án & Core dữ liệu tiếng Việt (134 ký tự, NFC/NFD, Test corpus)
- [x] Stub app Android chạy trên Jetpack Compose
- [ ] Canvas vẽ nét chữ cảm ứng có lưới căn dòng (Baseline, Cap-height)
- [ ] Thuật toán trích xuất vector contours từ nét vẽ
- [ ] Engine tự động ghép dấu tiếng Việt theo tỷ lệ bounding box
- [ ] Bộ tạo nhị phân TrueType Font (`.ttf`) offline
- [ ] Giao diện xem trước và xuất file phông chữ

---

## Giấy phép

Dự án phát triển cho mục đích giáo dục và nghiên cứu mã nguồn mở.
