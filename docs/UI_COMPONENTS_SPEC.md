# UI Component Specification — Authentication & Cookie Session

## 1. Scope and source

- **Nguồn chính:** `README_UI.md` (route/page, workflow, UI mapping, API endpoint). Đối chiếu `README.md` để xác nhận stack và hiện trạng.
- **Stack dự kiến:** Next.js, TypeScript; Tailwind CSS và quy ước App Router chưa được xác nhận trong tài liệu. Đặc tả dùng gợi ý Tailwind thông dụng, không giả định theme tùy chỉnh.
- **Hiện trạng:** checkout này chứa backend; frontend được duy trì ở checkout riêng và đã có source kết nối gallery, CSRF, login và current-session với API. Đặc tả vẫn là baseline cho các component và workflow chưa hoàn thiện.
- **Route:** `/gallery`, `/admin/login`, `/admin/gallery` là các route frontend hiện hành; endpoint API giữ nguyên theo contract.
- **Quy ước:** “Theo tài liệu” là hành vi được nguồn xác nhận; “Đề xuất UI” là cách trình bày/feedback cần quyết định khi triển khai. Giá trị thiết kế ở §3 đã được người dùng chọn; cần tích hợp font khi frontend được tạo.

## 2. Route và workflow map

| Route | Mục đích | Components chính | Workflow tóm tắt |
| --- | --- | --- | --- |
| `/gallery` | Visitor duyệt gallery công khai | `GalleryPage`, `GalleryGrid`, `GalleryImage`, `GalleryLoadMoreSentinel`, `GalleryFeedback` | Tải và duyệt ảnh công khai theo trang/cuộn. Chi tiết cần thiết nằm ở component tương ứng. |
| `/admin/login` | Admin xác thực | `AdminLoginPage`, `AdminLoginForm`, `FormField`, `FormFeedback` | Gửi thông tin đăng nhập; frontend hiện đã wire CSRF/login và chuyển tới `/admin/gallery` sau thành công. |
| `/admin/gallery` | Admin quản lý ảnh | `AdminGalleryPage`, `AdminSessionGate`, `ImageUploadForm`, `ImagePreview`, `AdminImageGrid`, `DeleteImageControl`, `LogoutControl` | Kiểm tra session đã wire; upload/xóa ảnh và logout là các workflow frontend tiếp theo. |
| Workflow dùng chung | CSRF | `CsrfCookieClient` *(logic dùng chung, không phải page)* | Cung cấp header CSRF cho request thay đổi trạng thái. |

## 3. Design tokens — lựa chọn người dùng

- **Mood:** dark.
- **Fonts:** Cinzel cho title; Inter cho body, description và controls; fallback `serif` cho Cinzel, `sans-serif` cho Inter. Cần thêm font vào frontend khi repo có frontend; hiện checkout chưa có cấu hình tải font.

| Token | Giá trị | Vai trò |
| --- | --- | --- |
| `--color-background` | `#010736` | Nền trang sâu |
| `--color-surface` | `#0D1C42` | Container/card trên nền trang |
| `--color-primary` | `#A0E0EF` | Màu chữ chính, link, focus accent và nút sáng |
| `--color-accent` | `#BC0202` | Điểm nhấn nhỏ, chi tiết trang trí; tránh dùng làm chữ nhỏ hoặc trạng thái duy nhất |
| `--color-text` | `#A0E0EF` | Nội dung chính trên nền tối |
| `--color-muted` | `#A0E0EF` | Mô tả/phụ đề; có thể hạ opacity nhẹ nếu vẫn đủ tương phản |
| `--color-border` | `#A0E0EF` | Viền cần nhận biết trên nền navy; dùng mức opacity vừa phải |
| `--color-danger` | `#BC0202` | Điểm nhấn nguy hiểm; luôn kèm label/icon, không biểu đạt chỉ bằng màu |

- **Title gradient:** gradient nhẹ `#A0E0EF` → `#D9F5FA`, chỉ áp dụng cho title lớn; fallback chữ đặc `#A0E0EF`, đảm bảo selection/high-contrast mode vẫn đọc được. Màu sáng thứ hai là biến thể tinh chỉnh để tạo gradient, không thay đổi palette cơ sở đã chọn.
- **Contrast check:** `#A0E0EF` trên `#010736` đạt khoảng 13.28:1 và trên `#0D1C42` khoảng 11.41:1, đạt WCAG AA cho văn bản thông thường.
- **Buttons:** border-radius `15px`; nền/foreground phải tương phản với container. Trên nền tối dùng button nền `#A0E0EF` với chữ `#010736`; nếu container sáng hơn trong tương lai, dùng nền tối với chữ aqua. Hover/focus không chỉ đổi màu; giữ outline rõ.
- **Type scale:** body/control `clamp(1rem, 0.96rem + 0.15vw, 1.125rem)`; page title `clamp(1.75rem, 1.4rem + 1.2vw, 2.5rem)`; description `clamp(0.95rem, 0.92rem + 0.1vw, 1rem)`.
- **Layout:** container fluid `width: min(100% - 2rem, 72rem)`; spacing theo bội số `0.25rem`; card radius `0.75rem`; control radius mặc định `0.5rem`, button riêng `15px`; focus ring `2px` aqua với offset `2px`.
- **CSS variables:** khai báo tokens trên ở `:root`; utility dùng CSS variables như `bg-[var(--color-surface)]`, `text-[var(--color-text)]`; chỉ thêm Tailwind theme aliases nếu cấu hình thực sự được tạo.
- **Tailwind defaults tạm:** breakpoints `sm` 640px, `md` 768px, `lg` 1024px. Đây không phải convention hiện hữu của repo.

## 4. Shared layout và navigation

- **Public shell:** vùng nội dung chung cho `/gallery`; header/navigation không được mô tả trong tài liệu. Ưu tiên đặt tên gallery rõ ràng và một điều hướng tối thiểu chỉ khi có đích dùng thật; không thêm mục “About” hoặc link chưa có đích trong tài liệu.
- **Admin shell:** page quản trị đề xuất có vùng gallery và controls upload/logout; không thêm sidebar/header trang trí. Giữ logout dễ tìm và cùng vị trí nếu shell dùng chung.
- **Điều hướng:** frontend hiện hướng login thành công tới `/admin/gallery` và dùng `/admin/login` làm đích sau logout; logout chưa được wire trong source hiện tại.
- **Session gate:** gọi session endpoint để xác định quyền hiển thị controls. Tránh giả định session đã đăng nhập chỉ dựa trên trạng thái client; cookie được trình duyệt gửi tự động.

## 5. Component specifications

### `GalleryPage` / `GalleryGrid`
- **Purpose and placement:** `/gallery`; mục tiêu Visitor xem ảnh không cần đăng nhập.
- **Workflow/API trace:** mount → `GET /api/v1/images` không có `nextCursor` → render `data.items[]`; khi cuộn, gửi cursor nhận trước đó cho request kế tiếp. Mỗi trang tối đa 10.
- **Content/data:** `imageId`, `imageURL`; sắp xếp mới nhất trước theo backend. Không thêm caption/title vì tài liệu loại metadata này khỏi phạm vi.
- **Base styles:** grid responsive `grid-cols-1 sm:grid-cols-2 lg:grid-cols-3`; ảnh `width:100%`, tỷ lệ khung đề xuất `4/3`, `object-fit: cover`; đặt max-width bằng container token. Tỷ lệ/crop là đề xuất, URL ảnh là dữ liệu nguồn. Tile không clickable toàn phần vì không có trang chi tiết; không nhồi caption/metadata không tồn tại.
- **Responsive:** mobile 1 cột; tablet 2; desktop 3 hoặc nhiều hơn khi khoảng rộng cho phép. Không ép chiều rộng cố định.
- **States:** loading skeleton (đề xuất); empty gallery và loading/error chung được UC01 xác nhận; end-of-list theo `hasNextPage`/`nextCursor`. Empty copy cần báo gallery hiện chưa có ảnh; không thêm CTA không có căn cứ. Giữ nút tải thêm có thể focus làm fallback cho sentinel cuộn.
- **Motion:** ảnh/skeleton thay đổi opacity 120ms ease-out; không animate vị trí grid; `prefers-reduced-motion` tắt transition.
- **Accessibility:** mỗi ảnh có alt text; API không cung cấp trường mô tả nên cần quy tắc alt phù hợp khi triển khai. Sentinel không phải cách tải tiếp duy nhất; giữ focus khi dữ liệu thêm. Tile ảnh không có affordance giả nếu không có hành động.
- **Evidence/status:** gallery, infinite scroll, empty/error, page size và trường API theo tài liệu; grid/crop/loading skeleton đề xuất.

### `AdminLoginForm`
- **Purpose and placement:** `/admin/login`; email/password form theo UC02.
- **Workflow/API trace:** submit → lấy cookie CSRF từ `GET /api/v1/csrf-token` nếu chưa có → gửi JSON `email`, `password`, cookie `XSRF-TOKEN` và header `X-XSRF-TOKEN` qua `POST /api/v1/auth/admin` → `201` xác nhận `authenticated:true` và session cookie.
- **Content/data:** label email/password; không log hoặc hiển thị password trong lỗi. Câu chữ cụ thể chưa được tài liệu quy định.
- **Base styles:** form max-width đề xuất `28rem`, surface card, field full width; password không bị tự động ghi nhớ bởi UI ngoài lựa chọn browser chuẩn.
- **Responsive:** padding fluid; trên mobile form full width trong container có lề; trên desktop căn giữa trong vùng nội dung.
- **States:** default; hover (pointer); focus-visible; pressed; disabled/submitting để chống gửi lặp; validation feedback cho `400`; generic invalid credentials cho `401`; CSRF failure `403`; generic server error `500`. Giữ input sau lỗi có thể phục hồi; không xóa email/password đã nhập nếu không cần. Nội dung feedback cụ thể là đề xuất, không phải copy đã được xác nhận.
- **Motion:** focus/hover 120ms; submitting indicator không làm dịch layout; reduced motion tắt spinner animation nếu có.
- **Accessibility:** `<form>`, label bền vững gắn input, `type=email`, `autocomplete=username`, password `autocomplete=current-password`; lỗi actionable đặt cạnh field và thông báo qua vùng `aria-live`, focus lỗi đầu tiên khi validation thất bại. Nút/link có tên mô tả; target tối thiểu 24×24 CSS px, khuyến nghị lớn hơn cho touch; không dựa riêng vào màu.
- **Evidence/status:** fields, CSRF sequence và API result đã xác nhận; wording, layout, post-login navigation đề xuất/chưa chốt.

### `AdminSessionGate`
- **Purpose and placement:** bảo vệ controls trong page quản trị đề xuất.
- **Workflow/API trace:** vào/khôi phục page → `GET /api/v1/auth/admin/sessions` với session cookie → `200 authenticated:true` cho phép controls; `401` UC03 đưa về trang login.
- **States:** checking/loading; authenticated; unauthenticated; lỗi kiểm tra `500`. Thiết kế phản hồi cho `500` chưa có nguồn, đề xuất thông báo lỗi chung và retry thủ công.
- **Accessibility:** announce trạng thái kiểm tra; không đưa focus vào controls trước khi xác thực hoàn tất.
- **Evidence/status:** endpoint và nhánh `200`/`401` theo tài liệu; thời điểm gọi, hiển thị pending/500 và nơi đặt gate là đề xuất.

### `ImageUploadForm` / `ImagePreview`
- **Purpose and placement:** controls upload trên `/admin/gallery` đề xuất.
- **Workflow/API trace:** chọn file → local preview tạm thời → submit multipart `file` tới `POST /api/v1/images`, kèm session cookie và `X-XSRF-TOKEN` → `201` trả `imageId`, `imageURL`; thêm/làm mới ảnh lên đầu gallery.
- **Content/data:** allowed `.jpg`, `.jpeg`, `.png`, `.HEIC`, `.HEIF`, `.WebP`, `.AVIF`, tối đa 15 MB; backend kiểm tra actual content/type. Không tin extension hoặc MIME từ browser thay validation server.
- **Base styles:** file input có label và vùng chọn file; preview co giãn với `max-width:100%`, `object-fit:contain`; nút submit dùng primary token.
- **Responsive:** xếp dọc mobile; trên desktop preview và controls có thể đặt cạnh nhau nếu không làm mất luồng đọc.
- **States:** no file; preview; hover/focus/pressed cho controls; submitting/disabled; success; missing/empty `400`, unauthorized `401`, forbidden `403`, too large `413`, unsupported `415`, server `500`. Error copy/recovery chưa được tài liệu chốt; giữ file/preview khi lỗi có thể phục hồi.
- **Motion:** preview opacity 120ms; không zoom tự động; reduced-motion tắt transition.
- **Accessibility:** file input và button dùng được bằng bàn phím; label thường trực; thông tin loại/dung lượng bằng text; announce preview, progress và submit result; errors gắn với input. Controls có focus-visible, target tối thiểu 24×24 CSS px và ưu tiên 44×44 cho touch.
- **Evidence/status:** preview, formats/size, API field/result và cập nhật gallery theo nguồn; bố cục, validation client, wording/state details là đề xuất.

### `AdminImageGrid` / `DeleteImageControl`
- **Purpose and placement:** hiển thị và xóa ảnh trong page quản trị đề xuất.
- **Workflow/API trace:** chọn ảnh và xác nhận xóa → `DELETE /api/v1/images/{imageId}` (UUID) với session cookie và CSRF → `200 deleted:true` → bỏ ảnh khỏi gallery đang hiển thị.
- **Content/data:** `imageId`, `imageURL`; không dựa vào `imageURL` để xóa.
- **Base styles:** tái dùng image display từ gallery; nút xóa tách biệt, nhãn có tên/ID ảnh hoặc accessible name cụ thể.
- **Responsive:** thao tác xóa luôn khả dụng bằng bàn phím/touch, không chỉ hiện khi hover; tile co theo grid.
- **States:** default; hover/focus/pressed; confirm trước khi xóa theo UC05; deleting/disabled và success removal là cách trình bày đề xuất quanh kết quả đã xác nhận; `401`, `403`, `404`, `500` theo contract, UI feedback/recovery chưa định nghĩa. Confirmation chỉ nêu ảnh sắp xóa và hệ quả; có Cancel rõ ràng.
- **Motion:** chỉ fade-out sau khi server xác nhận thành công, 120ms; khi reduced motion bật thì bỏ transition.
- **Accessibility:** confirmation dialog quản lý focus, Escape/cancel và accessible name; thông báo xóa qua `aria-live`; destructive button có verb cụ thể “Xóa ảnh” (nhãn đề xuất), không chỉ biểu đạt nguy hiểm bằng màu đỏ. Target tối thiểu 24×24 CSS px.
- **Evidence/status:** chọn/xác nhận xóa, endpoint, status và bỏ ảnh sau thành công theo tài liệu; placement, dialog design và lỗi feedback đề xuất.

### `LogoutControl`
- **Purpose and placement:** kết thúc admin session trên admin shell đề xuất.
- **Workflow/API trace:** action logout → CSRF header → `DELETE /api/v1/auth/admin/sessions` → `200 authenticated:false`, session bị vô hiệu hóa, cookie hết hạn → UI unauthenticated.
- **States:** default; hover/focus-visible/pressed; submitting/disabled; unauthenticated sau success; `401`/`500` theo API. Error presentation chưa được tài liệu quy định.
- **Responsive/accessibility:** button có label hành động “Đăng xuất” (nhãn đề xuất); keyboard/touch accessible, focus-visible rõ, target tối thiểu 24×24 CSS px và ưu tiên 44×44 cho touch.
- **Motion:** không chuyển trang bằng animation dài; reduced motion không có hiệu ứng phụ.
- **Evidence/status:** hành vi API/session theo tài liệu; session gate đã wire trong frontend, còn logout và vị trí control vẫn là phần triển khai tiếp theo.

### `CsrfCookieClient` *(logic dùng chung, không hiển thị)*
- **Purpose and placement:** adapter API dùng chung trước state-changing requests.
- **Workflow/API trace:** `GET /api/v1/csrf-token` → đọc `XSRF-TOKEN` cookie (không HttpOnly theo cấu hình tài liệu) → gắn `X-XSRF-TOKEN`; token không nằm trong body.
- **Accessibility/styles:** không có UI trực tiếp; lỗi request được component gọi xử lý theo API response, không lộ cookie/token.
- **Evidence/status:** theo README_UI/API contract.

## 6. API feedback và page states

Mỗi component ở §5 chỉ giữ phần workflow/API tối thiểu để giải thích hành vi UI. Chi tiết endpoint và contract nằm trong `api_contract.md`; luồng sản phẩm đầy đủ nằm trong `README_UI.md`.

Chỉ trạng thái được tài liệu xác nhận được xem là yêu cầu. Skeleton, submitting feedback, retry và wording lỗi là đề xuất khi cần; HTTP status không quyết định nguyên văn nội dung UI. Không tự retry mutation khi kết quả không rõ vì tài liệu chưa định nghĩa idempotency/retry.

## 7. Responsive behavior summary

| Component/section | Mobile | Tablet | Laptop/desktop |
| --- | --- | --- | --- |
| Public gallery | 1 cột; lề fluid; cuộn dọc | 2 cột | 3+ cột theo chiều rộng; giữ ảnh linh hoạt |
| Login form | Full width trong lề; field/button cao dễ chạm | Card max-width | Card căn giữa, không kéo rộng |
| Upload form/preview | Xếp dọc; preview full container | Xếp dọc hoặc 2 vùng cân đối | Có thể song song form/preview |
| Admin image grid | 1 cột hoặc 2 khi đủ chỗ; control không phụ thuộc hover | 2–3 cột | 3+ cột theo container |
| Feedback/dialog | Không tràn viewport; action xếp dọc nếu cần | Panel vừa nội dung | Dialog max-width, giữ focus |

Các cột là responsive recommendation; breakpoint Tailwind mặc định tạm thời vì repo chưa xác nhận frontend config.

## 8. Open questions and documentation gaps

- Người dùng chưa chọn density và animation intensity; hiện giữ ở mức trung tính, không làm thay đổi luồng nghiệp vụ.
- Tài liệu chưa chốt: route đích sau login/logout; page split/placement của admin controls; header/navigation; alt text nguồn cho ảnh; UI copy và retry cho các lỗi; feedback cho lỗi partial deletion/reconciliation.
- Frontend hiện dùng Next.js App Router/Tailwind; các conventions trong đặc tả cần tiếp tục đối chiếu khi hoàn thiện upload/delete/logout.
