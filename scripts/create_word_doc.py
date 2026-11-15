import os
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, hex_color):
    """Thiết lập màu nền cho ô trong bảng"""
    shading_xml = f'<w:shd {nsdecls("w")} w:fill="{hex_color}"/>'
    cell._tc.get_or_add_tcPr().append(parse_xml(shading_xml))

def set_cell_margins(cell, top=120, bottom=120, left=150, right=150):
    """Thiết lập khoảng cách đệm (padding) trong ô"""
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = OxmlElement('w:tcMar')
    for margin_name, val in [('top', top), ('bottom', bottom), ('left', left), ('right', right)]:
        node = OxmlElement(f'w:{margin_name}')
        node.set(qn('w:w'), str(val))
        node.set(qn('w:type'), 'dxa')
        tcMar.append(node)
    tcPr.append(tcMar)

def create_document():
    doc = Document()

    # Thiết lập lề trang chuẩn A4
    for section in doc.sections:
        section.top_margin = Inches(0.8)
        section.bottom_margin = Inches(0.8)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)

    # 1. HEADER CƠ QUAN / TRƯỜNG HỌC
    p_header = doc.add_paragraph()
    p_header.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run_sch = p_header.add_run("TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH\n")
    run_sch.font.name = "Arial"
    run_sch.font.size = Pt(11)
    run_sch.font.bold = True
    run_sch.font.color.rgb = RGBColor(0, 51, 102)

    run_fac = p_header.add_run("KHOA CÔNG NGHỆ THÔNG TIN - BỘ MÔN CÔNG NGHỆ PHẦN MỀM\n")
    run_fac.font.name = "Arial"
    run_fac.font.size = Pt(10)
    run_fac.font.bold = True
    run_fac.font.color.rgb = RGBColor(100, 100, 100)

    run_subj = p_header.add_run("LẬP TRÌNH WEB (MÃ HỌC PHẦN: WEBPR330479)\n")
    run_subj.font.name = "Arial"
    run_subj.font.size = Pt(11)
    run_subj.font.bold = True
    run_subj.font.color.rgb = RGBColor(0, 102, 204)

    run_line = p_header.add_run("--------------------------------------------------------------------------------")
    run_line.font.color.rgb = RGBColor(200, 200, 200)

    # 2. TIÊU ĐỀ TÀI LIỆU
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_title.paragraph_format.space_before = Pt(18)
    p_title.paragraph_format.space_after = Pt(6)

    run_title = p_title.add_run("TÀI LIỆU HƯỚNG DẪN ĐĂNG NHẬP\nVÀ PHÂN QUYỀN HỆ THỐNG")
    run_title.font.name = "Arial"
    run_title.font.size = Pt(18)
    run_title.font.bold = True
    run_title.font.color.rgb = RGBColor(0, 51, 153)

    p_sub = doc.add_paragraph()
    p_sub.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_sub.paragraph_format.space_after = Pt(20)
    run_sub = p_sub.add_run("Hệ thống: Shopping Service MVC (Servlet - JDBC - 3 Tier Architecture)\nGiảng viên hướng dẫn: ThS. Nguyễn Hữu Trung")
    run_sub.font.name = "Arial"
    run_sub.font.size = Pt(11)
    run_sub.font.italic = True
    run_sub.font.color.rgb = RGBColor(80, 80, 80)

    # 3. MỤC 1: THÔNG TIN ĐĂNG NHẬP MẪU TRÊN HỆ THỐNG
    h1 = doc.add_heading("1. DANH SÁCH TÀI KHOẢN ĐĂNG NHẬP TRÊN HỆ THỐNG", level=1)
    h1.paragraph_format.space_before = Pt(12)
    h1.paragraph_format.space_after = Pt(8)
    for r in h1.runs:
        r.font.name = "Arial"
        r.font.size = Pt(13)
        r.font.bold = True
        r.font.color.rgb = RGBColor(0, 51, 102)

    p_intro = doc.add_paragraph()
    run_intro = p_intro.add_run(
        "Hệ thống đã được khởi tạo sẵn các tài khoản thử nghiệm tương ứng với từng vai trò (Role) "
        "quy định trong tài liệu học phần. Dưới đây là bảng thông tin tài khoản dùng để đăng nhập và kiểm thử:"
    )
    run_intro.font.name = "Arial"
    run_intro.font.size = Pt(10.5)

    # BẢNG TÀI KHOẢN (Đúng chuẩn như ảnh người dùng gửi)
    table_data = [
        ["Tên đăng nhập", "Mật khẩu", "Quyền (Role)", "Chức năng truy cập"],
        ["admin", "123456", "Role 1 (Admin)", "Toàn quyền Quản trị & CRUD Danh mục ( /admin/* )"],
        ["trungnh", "123456", "Role 1 (GVHD)", "Toàn quyền Quản trị & CRUD Danh mục"],
        ["manager", "123456", "Role 2 (Manager)", "Quản lý danh mục sản phẩm"],
        ["khachhang", "123456", "Role 5 (User)", "Giao diện mua sắm trực tuyến ( /home )"]
    ]

    table = doc.add_table(rows=len(table_data), cols=4)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False

    col_widths = [Inches(1.4), Inches(1.2), Inches(1.5), Inches(2.5)]

    for row_idx, row in enumerate(table.rows):
        is_header = (row_idx == 0)
        trPr = row._tr.get_or_add_trPr()
        trPr.append(parse_xml(f'<w:cantSplit {nsdecls("w")}/>'))
        if is_header:
            trPr.append(parse_xml(f'<w:tblHeader {nsdecls("w")}/>'))

        for col_idx, cell in enumerate(row.cells):
            cell.width = col_widths[col_idx]
            cell.vertical_alignment = WD_ALIGN_VERTICAL.CENTER
            set_cell_margins(cell, top=140, bottom=140, left=150, right=150)
            
            p = cell.paragraphs[0]
            p.paragraph_format.space_before = Pt(2)
            p.paragraph_format.space_after = Pt(2)
            
            text = table_data[row_idx][col_idx]
            run = p.add_run(text)
            run.font.name = "Arial"
            
            if is_header:
                set_cell_background(cell, "F2F2F2")
                run.font.bold = True
                run.font.size = Pt(10.5)
                run.font.color.rgb = RGBColor(30, 30, 30)
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            else:
                set_cell_background(cell, "FFFFFF" if row_idx % 2 == 1 else "F9FBFC")
                run.font.size = Pt(10)
                if col_idx in [0, 1]:
                    run.font.name = "Consolas"
                    run.font.bold = True
                    run.font.color.rgb = RGBColor(0, 70, 150)
                elif col_idx == 2:
                    run.font.bold = True
                    run.font.color.rgb = RGBColor(180, 40, 40) if "Admin" in text else RGBColor(40, 120, 40)

    # Thêm viền bảng thanh mảnh
    tblBorders = parse_xml(
        f'<w:tblBorders {nsdecls("w")}>'
        '<w:top w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>'
        '<w:left w:val="none"/>'
        '<w:bottom w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>'
        '<w:right w:val="none"/>'
        '<w:insideH w:val="single" w:sz="4" w:space="0" w:color="E5E7EB"/>'
        '<w:insideV w:val="none"/>'
        '</w:tblBorders>'
    )
    table._tbl.tblPr.append(tblBorders)

    # 4. MỤC 2: CÁC BƯỚC ĐĂNG NHẬP CHI TIẾT
    h2 = doc.add_heading("2. CÁC BƯỚC ĐĂNG NHẬP VÀO HỆ THỐNG", level=1)
    h2.paragraph_format.space_before = Pt(20)
    h2.paragraph_format.space_after = Pt(8)
    for r in h2.runs:
        r.font.name = "Arial"
        r.font.size = Pt(13)
        r.font.bold = True
        r.font.color.rgb = RGBColor(0, 51, 102)

    steps = [
        ("Bước 1: Khởi động Server Tomcat", 
         "Mở thư mục đồ án D:\\LT_Web\\BTVN_9-11\\ và nhấp đúp vào file run_server.bat. Cửa sổ Tomcat 11 sẽ hiển thị trạng thái Server startup hoàn tất."),
        ("Bước 2: Truy cập trang đăng nhập", 
         "Mở trình duyệt (Chrome, Cốc Cốc, Edge) và nhập đường dẫn:\n• URL Đăng nhập: http://localhost:8080/ShoppingServiceMVC/login\n(Hoặc truy cập trang chủ http://localhost:8080/ShoppingServiceMVC/ rồi bấm nút 'Đăng nhập' trên thanh điều hướng topbar)."),
        ("Bước 3: Nhập thông tin tài khoản", 
         "• Điền Tên đăng nhập vào ô 'Tài khoản' (Ví dụ: admin hoặc khachhang).\n• Điền Mật khẩu vào ô 'Mật khẩu' (Mặc định: 123456).\n• (Tùy chọn) Tích chọn ô 'Nhớ tôi' để hệ thống lưu Cookie ghi nhớ trong 30 phút."),
        ("Bước 4: Nhấn nút 'Đăng nhập'", 
         "Hệ thống sẽ gửi yêu cầu POST đến LoginController, xác thực mật khẩu trong bảng [User] của CSDL SQL Server và chuyển giao cho WaitingController để phân quyền tự động."),
        ("Bước 5: Tiếp nhận phân quyền theo vai trò", 
         "• Nếu là Admin (Role 1) hoặc Manager (Role 2): Tự động chuyển đến Bảng điều khiển Quản trị (Dashboard) tại /admin/home và cấp quyền quản lý Danh mục (CRUD).\n• Nếu là Khách hàng (Role 5): Tự động chuyển về Trang chủ Mua sắm tại /home.")
    ]

    for title, desc in steps:
        p_step = doc.add_paragraph()
        p_step.paragraph_format.space_before = Pt(4)
        p_step.paragraph_format.space_after = Pt(4)
        r_step_t = p_step.add_run(f"• {title}: ")
        r_step_t.font.name = "Arial"
        r_step_t.font.bold = True
        r_step_t.font.size = Pt(10.5)
        r_step_t.font.color.rgb = RGBColor(0, 102, 204)
        
        r_step_d = p_step.add_run(desc)
        r_step_d.font.name = "Arial"
        r_step_d.font.size = Pt(10)

    # 5. MỤC 3: HƯỚNG DẪN KIỂM THỬ CÁC TÌNH HUỐNG
    h3 = doc.add_heading("3. KỊCH BẢN KIỂM THỬ XÁC THỰC (TEST SCENARIOS)", level=1)
    h3.paragraph_format.space_before = Pt(16)
    h3.paragraph_format.space_after = Pt(8)
    for r in h3.runs:
        r.font.name = "Arial"
        r.font.size = Pt(13)
        r.font.bold = True
        r.font.color.rgb = RGBColor(0, 51, 102)

    cases = [
        ("Kiểm thử 1: Đăng nhập thành công với quyền Admin",
         "Nhập 'admin' / '123456'. Kết quả: Vào trang Dashboard /admin/home, thanh sidebar có mục 'Quản lý Danh mục', topbar hiển thị 'Xin chào, Quản Trị Viên Hệ Thống (Admin)'."),
        ("Kiểm thử 2: Đăng nhập thành công với quyền Khách hàng",
         "Nhập 'khachhang' / '123456'. Kết quả: Vào trang /home, topbar hiển thị 'Xin chào, Nguyễn Văn A', xem được lưới danh mục sản phẩm."),
        ("Kiểm thử 3: Bỏ trống tên đăng nhập hoặc mật khẩu",
         "Để trống 1 trong 2 ô rồi nhấn Đăng nhập. Kết quả: Trang ở lại giao diện đăng nhập và xuất hiện khung cảnh báo đỏ: 'Tài khoản hoặc mật khẩu không được để trống!'."),
        ("Kiểm thử 4: Nhập sai mật khẩu",
         "Nhập 'admin' / 'mat_khau_sai'. Kết quả: Hệ thống từ chối đăng nhập và xuất hiện cảnh báo đỏ: 'Tài khoản hoặc mật khẩu không đúng!'."),
        ("Kiểm thử 5: Chức năng 'Nhớ tôi' (Remember Me)",
         "Tích chọn 'Nhớ tôi' khi đăng nhập. Mở công cụ Developer Tools (F12) -> Application -> Cookies -> thấy cookie 'username' được lưu với thời hạn Max-Age 1800 giây (30 phút)."),
        ("Kiểm thử 6: Chức năng Đăng xuất (Logout)",
         "Nhấn nút 'Đăng xuất' ở góc phải topbar. Kết quả: Session bị hủy (invalidate), Cookie bị xóa (Max-Age = 0) và trình duyệt chuyển hướng ngay về trang Đăng nhập.")
    ]

    for title, desc in cases:
        p_c = doc.add_paragraph()
        p_c.paragraph_format.space_before = Pt(3)
        p_c.paragraph_format.space_after = Pt(3)
        r_c_t = p_c.add_run(f"3.{cases.index((title, desc)) + 1}. {title}\n")
        r_c_t.font.name = "Arial"
        r_c_t.font.bold = True
        r_c_t.font.size = Pt(10.5)
        
        r_c_d = p_c.add_run(f"   Thực hiện & Kết quả: {desc}")
        r_c_d.font.name = "Arial"
        r_c_d.font.size = Pt(10)
        r_c_d.font.color.rgb = RGBColor(60, 60, 60)

    # 6. MỤC 4: HƯỚNG DẪN ĐĂNG KÝ TÀI KHOẢN MỚI
    h4 = doc.add_heading("4. HƯỚNG DẪN TẠO TÀI KHOẢN MỚI (REGISTER)", level=1)
    h4.paragraph_format.space_before = Pt(16)
    h4.paragraph_format.space_after = Pt(8)
    for r in h4.runs:
        r.font.name = "Arial"
        r.font.size = Pt(13)
        r.font.bold = True
        r.font.color.rgb = RGBColor(0, 51, 102)

    p_reg = doc.add_paragraph()
    p_reg.add_run(
        "Nếu người dùng chưa có tài khoản trên hệ thống:\n"
        "1. Bấm vào liên kết 'Đăng ký' tại trang đăng nhập hoặc topbar (URL: http://localhost:8080/ShoppingServiceMVC/register).\n"
        "2. Điền đầy đủ các thông tin: Tên đăng nhập, Họ và tên, Email, Số điện thoại, Mật khẩu và Xác nhận mật khẩu.\n"
        "3. Hệ thống sẽ tự động kiểm tra:\n"
        "   • Tên đăng nhập không được trùng với tài khoản đã có trong hệ thống.\n"
        "   • Địa chỉ Email không được trùng lặp.\n"
        "   • Mật khẩu xác nhận phải trùng khớp tuyệt đối với mật khẩu đã nhập.\n"
        "4. Sau khi đăng ký thành công, hệ thống gán quyền mặc định Role 5 (Khách hàng) và tự động chuyển về trang Đăng nhập kèm thông báo màu xanh chúc mừng."
    ).font.name = "Arial"

    # 7. MỤC 5: GHI CHÚ KỸ THUẬT VỀ PHÂN QUYỀN VÀ BẢO MẬT
    h5 = doc.add_heading("5. CƠ CHẾ KỸ THUẬT PHÂN QUYỀN (ARCHITECTURE NOTE)", level=1)
    h5.paragraph_format.space_before = Pt(16)
    h5.paragraph_format.space_after = Pt(8)
    for r in h5.runs:
        r.font.name = "Arial"
        r.font.size = Pt(13)
        r.font.bold = True
        r.font.color.rgb = RGBColor(0, 51, 102)

    p_tech = doc.add_paragraph()
    p_tech.add_run(
        "• Tầng Controller: LoginController xác thực và lưu đối tượng User vào session với khóa Constant.SESSION_ACCOUNT ('account').\n"
        "• Tầng Điều Hướng: WaitingController kiểm tra session và căn cứ trên user.getRoleid():\n"
        "     - roleid == 1 (Admin) -> redirect /admin/home\n"
        "     - roleid == 2 (Manager) -> redirect /admin/home\n"
        "     - roleid == 5 (User) -> redirect /home\n"
        "• Tầng View: Các trang Admin được bảo vệ (nếu chưa đăng nhập hoặc không đủ quyền Admin/Manager sẽ bị chuyển hướng ngay về /login).\n"
        "• Thẻ Taglib Session trên topbar.jsp: Sử dụng thẻ <c:choose> kiểm tra ${sessionScope.account == null} để chuyển đổi giao diện người dùng theo đúng Slide 20 của tài liệu học phần."
    ).font.name = "Arial"

    # 8. MỤC 6: HƯỚNG DẪN XEM SẢN PHẨM & TÌM KIẾM
    h6 = doc.add_heading("6. HỆ THỐNG DANH MỤC & SẢN PHẨM (CATALOG & PRODUCT DETAIL)", level=1)
    h6.paragraph_format.space_before = Pt(16)
    h6.paragraph_format.space_after = Pt(8)
    for r in h6.runs:
        r.font.name = "Arial"
        r.font.size = Pt(13)
        r.font.bold = True
        r.font.color.rgb = RGBColor(0, 51, 102)

    p_prod = doc.add_paragraph()
    p_prod.add_run(
        "Hệ thống đã được bổ sung cơ sở dữ liệu sản phẩm hoàn chỉnh với 60 sản phẩm tiếng Việt chuẩn UTF-8 (10 sản phẩm/danh mục):\n"
        "• Bấm 'Xem sản phẩm' tại bất kỳ danh mục nào: Chuyển hướng tới URL /products?cateid=X để xem danh sách sản phẩm thuộc danh mục đó.\n"
        "• Bấm vào bất kỳ thẻ sản phẩm nào: Mở trang Chi tiết sản phẩm (/product/detail?id=X) hiển thị hình ảnh chuẩn, giá niêm yết VND, tình trạng kho, mô tả chi tiết và các sản phẩm tương tự cùng danh mục.\n"
        "• Chức năng tìm kiếm sản phẩm: Nhập từ khóa tại thanh tìm kiếm trên trang chủ hoặc trang sản phẩm để tìm kiếm sản phẩm theo tên và mô tả.\n"
        "• Chức năng sắp xếp: Lọc giá tăng dần, giá giảm dần trực quan tại trang danh sách sản phẩm.\n"
        "• Cơ chế phục vụ hình ảnh: DownloadImageController (/image?fname=...) hỗ trợ phục vụ ảnh chuẩn xác từ cả thư mục upload/category và upload/product."
    ).font.name = "Arial"

    # FOOTER
    p_end = doc.add_paragraph()
    p_end.paragraph_format.space_before = Pt(25)
    p_end.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    r_end = p_end.add_run("--- HẾT TÀI LIỆU HƯỚNG DẪN ---")
    r_end.font.name = "Arial"
    r_end.font.size = Pt(9.5)
    r_end.font.italic = True
    r_end.font.color.rgb = RGBColor(120, 120, 120)

    # Lưu 2 phiên bản: tên không dấu và có dấu
    path1 = "d:/LT_Web/BTVN_9-11/Huong_Dan_Dang_Nhap.docx"
    path2 = "d:/LT_Web/BTVN_9-11/Hướng dẫn đăng nhập.docx"
    doc.save(path1)
    print(f"Document saved to {path1}")
    try:
        doc.save(path2)
        print(f"Document saved to {path2}")
    except PermissionError:
        print(f"Lưu ý: File '{path2}' đang được mở bởi ứng dụng Word, vui lòng đóng Word để ghi đè.")

if __name__ == "__main__":
    create_document()
