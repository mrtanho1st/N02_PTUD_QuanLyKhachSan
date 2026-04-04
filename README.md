# Hotel Management (VS Code + Java Swing + SQL Server)

## Folder Structure
- `src`: source code
- `lib`: dependencies (`.jar`)
- `bin`: compiled output
- `db/schema.sql`: schema + seed data dùng chung cho cả nhóm
- `scripts/run-main.sh`: chạy nhanh trên Ubuntu/Linux
- `scripts/run-main.bat`: chạy nhanh trên Windows

## VS Code Java Setup
Project đang theo đúng cấu trúc mặc định của VS Code Java:
- Source path: `src`
- Output path: `bin`
- Libraries: `lib/**/*.jar`

## Thiết lập `.env` (bắt buộc cho máy mới)
Tạo file `.env` từ `.env.example` trước khi chạy:

### Ubuntu/Linux
```bash
cp .env.example .env
```

### Windows (CMD)
```bat
copy .env.example .env
```

Sau đó chỉnh giá trị trong `.env` theo máy của bạn:
- `DB_HOST` (mặc định: `127.0.0.1`)
- `DB_PORT` (mặc định: `1433`)
- `DB_NAME` (mặc định: `HotelManagement`)
- `DB_USER` (mặc định: `sa`)
- `DB_PASSWORD` (phải đúng với SQL Server bạn đang dùng)
- `MSSQL_SA_PASSWORD` (dùng cho Docker SQL Server)

Lưu ý:
- `.env.example` là file mẫu để commit lên git.
- `.env` chứa thông tin môi trường thực tế, không nên commit.

## Run One Command
### Ubuntu/Linux
```bash
./scripts/run-main.sh
```
Script sẽ:
1. Nếu có Docker thì start SQL Server container, apply `db/schema.sql`.
2. Build Java.
3. Mở `gui.GiaoDienChinh`.

Ghi chú Ubuntu: script có sẵn fallback `sudo` (mặc định dùng `SUDO_PASSWORD=quan0511`).

### Windows
```bat
scripts\run-main.bat
```
Script sẽ:
1. Apply `db\schema.sql` qua `sqlcmd` (nếu có).
2. Build Java.
3. Mở `gui.GiaoDienChinh`.

## Data Consistency
- Dữ liệu nhập trong lúc chạy được lưu trong SQL Server runtime (local SQL hoặc Docker volume), không nằm trong source code.
- `db/schema.sql` là bộ seed chuẩn để máy mới chạy lên có dữ liệu ban đầu giống nhau.
