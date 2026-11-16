package vn.iotstar.test;

import java.sql.Timestamp;
import java.util.List;
import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.VideoDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.dao.impl.VideoDaoImpl;
import vn.iotstar.model.LoginResult;
import vn.iotstar.model.User;
import vn.iotstar.model.Video;
import vn.iotstar.service.UserService;
import vn.iotstar.service.VideoService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.BCrypt;
import vn.iotstar.util.OtpManager;

/**
 * Kiểm thử toàn diện các chức năng Bài tập 02:
 * 1. Đăng ký & Kích hoạt tài khoản bằng OTP
 * 2. Đăng nhập với mật khẩu băm BCrypt & kiểm tra kích hoạt
 * 3. Quên mật khẩu qua mã OTP
 * 4. CRUD Video, phân quyền người dùng, phân trang 6 video/trang, top 10 video mới nhất
 */
public class TestVideoAndAuthFlow {

    private static int passed = 0;
    private static int failed = 0;

    private static void assertTrue(String testName, boolean condition, String details) {
        if (condition) {
            System.out.println("  [PASS] " + testName + " - " + details);
            passed++;
        } else {
            System.err.println("  [FAIL] " + testName + " - " + details);
            failed++;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("     BỘ KIỂM THỬ TOÀN DIỆN CÁC CHỨC NĂNG BÀI TẬP 02 (JAVA WEB SHOPPING)");
        System.out.println("================================================================================\n");

        UserService userService = new UserServiceImpl();
        UserDao userDao = new UserDaoImpl();
        VideoService videoService = new VideoServiceImpl();
        VideoDao videoDao = new VideoDaoImpl();

        // ---------------------------------------------------------------------
        // TEST NHÓM 1: MẬT KHẨU BĂM BCRYPT & ĐĂNG NHẬP
        // ---------------------------------------------------------------------
        System.out.println("--- 1. KIỂM THỬ ĐĂNG NHẬP & BĂM MẬT KHẨU BCRYPT ---");
        
        // TC 1.1: Mật khẩu mặc định 123456 của admin được băm BCrypt
        User admin = userDao.get("admin");
        assertTrue("TC 1.1", admin != null, "Tìm thấy tài khoản admin trong CSDL");
        if (admin != null) {
            boolean hashValid = admin.getPassWord().startsWith("$2a$") || admin.getPassWord().startsWith("$2b$");
            assertTrue("TC 1.2", hashValid, "Mật khẩu admin được lưu dạng chuẩn băm BCrypt ($2a$): " + admin.getPassWord().substring(0, 15) + "...");
            boolean passMatch = BCrypt.checkpw("123456", admin.getPassWord());
            assertTrue("TC 1.3", passMatch, "Mật khẩu '123456' khớp chính xác với chuỗi băm BCrypt");
        }

        // TC 1.4: Xác thực đăng nhập qua UserService
        LoginResult loginAdmin = userService.authenticate("admin", "123456");
        assertTrue("TC 1.4", loginAdmin.getStatus() == LoginResult.Status.SUCCESS, "Đăng nhập admin / 123456 thành công");

        LoginResult loginFail = userService.authenticate("admin", "wrong_password");
        assertTrue("TC 1.5", loginFail.getStatus() == LoginResult.Status.INVALID_CREDENTIALS, "Nhập sai mật khẩu bị từ chối chính xác");

        // ---------------------------------------------------------------------
        // TEST NHÓM 2: QUẢN LÝ MÃ OTP (KÍCH HOẠT & QUÊN MẬT KHẨU)
        // ---------------------------------------------------------------------
        System.out.println("\n--- 2. KIỂM THỬ QUẢN LÝ MÃ OTP ---");
        String testEmail = "test_user_" + System.currentTimeMillis() + "@gmail.com";
        String otp = OtpManager.generateOtp();
        assertTrue("TC 2.1", otp.length() == 6 && otp.matches("\\d{6}"), "Sinh mã OTP ngẫu nhiên 6 chữ số: " + otp);

        OtpManager.createOtpSession(testEmail, otp, "REGISTER", "DUMMY_PAYLOAD");
        assertTrue("TC 2.2", OtpManager.getSession(testEmail) != null, "Khởi tạo phiên OTP trong bộ nhớ thành công (Hạn 5 phút)");

        long cooldown = OtpManager.getRemainingCooldownSeconds(testEmail);
        assertTrue("TC 2.3", cooldown > 0 && cooldown <= 60, "Thời gian đếm ngược gửi lại OTP (cooldown) là: " + cooldown + "s");

        // Nhập sai OTP
        OtpManager.VerifyResult wrongRes = OtpManager.verifyOtp(testEmail, "999999", "REGISTER");
        assertTrue("TC 2.4", wrongRes == OtpManager.VerifyResult.INVALID, "Nhập sai OTP trả về kết quả INVALID");

        // Nhập đúng OTP
        OtpManager.VerifyResult okRes = OtpManager.verifyOtp(testEmail, otp, "REGISTER");
        assertTrue("TC 2.5", okRes == OtpManager.VerifyResult.SUCCESS, "Nhập đúng OTP (" + otp + ") trả về SUCCESS");

        OtpManager.removeSession(testEmail);
        assertTrue("TC 2.6", OtpManager.getSession(testEmail) == null, "Xóa phiên OTP sau khi sử dụng thành công (chống dùng lại)");

        // ---------------------------------------------------------------------
        // TEST NHÓM 3: CRUD VIDEO, QUAN HỆ VỚI USER, PHÂN QUYỀN & PHÂN TRANG
        // ---------------------------------------------------------------------
        System.out.println("\n--- 3. KIỂM THỬ CHỨC NĂNG VIDEO ---");

        // TC 3.1: Lấy 10 Video mới nhất trang chủ /home
        List<Video> top10 = videoService.getTop10Latest();
        assertTrue("TC 3.1", top10 != null && !top10.isEmpty() && top10.size() <= 10, 
                   "Lấy 10 Video mới nhất từ CSDL thành công (Hiện có: " + top10.size() + " video)");
        if (!top10.isEmpty()) {
            Video first = top10.get(0);
            System.out.println("       Video mới nhất: [" + first.getVideoId() + "] " + first.getTitle() + " (" + first.getUploadDate() + ")");
        }

        // TC 3.2: Thêm Video mới (Create)
        String newVidId = "TEST_VID_" + System.currentTimeMillis();
        Video testVid = new Video();
        testVid.setVideoId(newVidId);
        testVid.setTitle("Video Kiểm Thử Tự Động " + System.currentTimeMillis());
        testVid.setDescription("Mô tả chi tiết nội dung video kiểm thử hệ thống.");
        testVid.setVideoUrl("test_video.mp4");
        testVid.setPoster("cat_dientu.png");
        testVid.setViews(100);
        testVid.setActive(1);
        testVid.setUploadDate(new Timestamp(System.currentTimeMillis()));
        testVid.setUserId(admin.getId()); // Thuộc về admin

        videoService.insert(testVid);

        Video fetchedVid = videoService.findById(newVidId);
        assertTrue("TC 3.2", fetchedVid != null && fetchedVid.getTitle().equals(testVid.getTitle()), 
                   "Thêm Video mới (INSERT) và đọc lại (findById) thành công");

        // TC 3.3: Tăng lượt xem (increaseViews)
        int initialViews = fetchedVid.getViews();
        videoService.increaseViews(newVidId);
        Video afterView = videoService.findById(newVidId);
        assertTrue("TC 3.3", afterView.getViews() == initialViews + 1, 
                   "Tự động tăng số lượt xem video thành công (Trước: " + initialViews + " -> Sau: " + afterView.getViews() + ")");

        // TC 3.4: Sửa thông tin Video (Update)
        afterView.setTitle("Video Đã Cập Nhật Tiêu Đề Mới");
        afterView.setDescription("Mô tả đã được sửa đổi qua test.");
        videoService.update(afterView);
        Video updatedVid = videoService.findById(newVidId);
        assertTrue("TC 3.4", updatedVid != null && "Video Đã Cập Nhật Tiêu Đề Mới".equals(updatedVid.getTitle()), 
                   "Cập nhật (UPDATE) thông tin video thành công");

        // TC 3.5: Phân trang 6 Video/trang (Pagination)
        int totalUserVideos = videoService.countByUserId(admin.getId());
        List<Video> page1 = videoService.getByUserId(admin.getId(), 1, 6);
        assertTrue("TC 3.5", page1 != null && page1.size() <= 6, 
                   "Phân trang 6 Video/trang của User thành công (Tổng: " + totalUserVideos + ", Trang 1 có: " + page1.size() + " video)");

        // TC 3.6: Phân quyền xóa Video (Security check)
        // Một user khác (ví dụ userId 999999) không được phép xóa video của admin
        boolean unauthorizedDelete = videoService.delete(newVidId, 999999, false);
        assertTrue("TC 3.6", !unauthorizedDelete, "Chặn người dùng lạ không được xóa video của người khác thành công");

        // Chính chủ admin xóa video của mình
        boolean authorizedDelete = videoService.delete(newVidId, admin.getId(), false);
        assertTrue("TC 3.7", authorizedDelete, "Chính chủ xóa video của mình thành công");
        assertTrue("TC 3.8", videoService.findById(newVidId) == null, "Video đã bị xóa hoàn toàn khỏi cơ sở dữ liệu");

        // ---------------------------------------------------------------------
        // TỔNG KẾT KẾT QUẢ KIỂM THỬ
        // ---------------------------------------------------------------------
        System.out.println("\n================================================================================");
        System.out.println("                          KẾT QUẢ KIỂM THỬ HỆ THỐNG");
        System.out.println("================================================================================");
        System.out.println("  TỔNG SỐ TEST CASES : " + (passed + failed));
        System.out.println("  SỐ TEST ĐẠT (PASS) : " + passed);
        System.out.println("  SỐ TEST LỖI (FAIL) : " + failed);
        System.out.println("  TỶ LỆ THÀNH CÔNG   : " + String.format("%.1f%%", (passed * 100.0 / (passed + failed))));
        System.out.println("================================================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
