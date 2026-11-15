package vn.iotstar.test;

import java.io.File;
import java.util.List;

import jakarta.persistence.EntityManager;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.dao.impl.CategoryDao;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

/**
 * Class kiểm thử toàn diện 12 chức năng CRUD Category và cấu hình JPA theo yêu cầu
 */
public class JpaTest {

    private static final ICategoryService cateService = new CategoryServiceImpl();
    private static final ICategoryDao cateDao = new CategoryDao();

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("      KIỂM THỬ TOÀN DIỆN 12 CHỨC NĂNG CRUD CATEGORY BẰNG JPA 3.0 & HIBERNATE");
        System.out.println("================================================================================\n");

        int passed = 0;
        int failed = 0;

        // TC 1: Kiểm tra kết nối JPA & SQL Server
        try {
            System.out.print("[TEST 01] Kết nối JPA & Khởi tạo EntityManager: ");
            EntityManager em = JPAConfig.getEntityManager();
            if (em != null && em.isOpen()) {
                System.out.println("THÀNH CÔNG! (EntityManager is open)");
                em.close();
                passed++;
            } else {
                System.out.println("THẤT BẠI!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 2: INSERT Category & Video
        int insertedId = -1;
        String testCategoryName = "Iphone Test " + System.currentTimeMillis();
        try {
            System.out.print("[TEST 02] INSERT (Thêm Category & Video liên kết): ");
            Category cat = new Category();
            cat.setCategoryname(testCategoryName);
            cat.setImages("iphone_test.jpg");
            cat.setStatus(1);

            Video v = new Video();
            v.setVideoId("V_TEST_" + System.currentTimeMillis());
            v.setTitle("Video gioi thieu Iphone");
            v.setPoster("poster_test.jpg");
            v.setActive(1);
            v.setViews(150);
            cat.addVideo(v);

            cateDao.insert(cat);
            insertedId = cat.getCategoryid();
            if (insertedId > 0) {
                System.out.println("THÀNH CÔNG! (Sinh mã ID = " + insertedId + ")");
                passed++;
            } else {
                System.out.println("THẤT BẠI! (Không sinh được ID)");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 3: SELECT ALL
        try {
            System.out.print("[TEST 03] SELECT ALL (NamedQuery Category.findAll): ");
            List<Category> all = cateService.findAll();
            if (all != null && !all.isEmpty()) {
                System.out.println("THÀNH CÔNG! (Lấy được " + all.size() + " danh mục)");
                passed++;
            } else {
                System.out.println("THẤT BẠI!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 4: FIND BY ID
        try {
            System.out.print("[TEST 04] FIND BY ID (ID = " + insertedId + "): ");
            Category found = cateService.findById(insertedId);
            if (found != null && found.getCategoryname().equals(testCategoryName)) {
                System.out.println("THÀNH CÔNG! (Tìm thấy: " + found.getCategoryname() + ")");
                passed++;
            } else {
                System.out.println("THẤT BẠI!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 5: FIND BY NAME
        try {
            System.out.print("[TEST 05] FIND BY NAME (Tên = " + testCategoryName + "): ");
            Category foundByName = cateService.findByCategoryname(testCategoryName);
            if (foundByName != null && foundByName.getCategoryid() == insertedId) {
                System.out.println("THÀNH CÔNG! (Tìm thấy ID: " + foundByName.getCategoryid() + ")");
                passed++;
            } else {
                System.out.println("THẤT BẠI!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 6: SEARCH
        try {
            System.out.print("[TEST 06] SEARCH (Tìm kiếm LIKE '%Iphone%'): ");
            List<Category> searchResults = cateService.searchByName("Iphone");
            if (searchResults != null && !searchResults.isEmpty()) {
                System.out.println("THÀNH CÔNG! (Tìm được " + searchResults.size() + " kết quả)");
                passed++;
            } else {
                System.out.println("THẤT BẠI!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 7: UPDATE
        try {
            System.out.print("[TEST 07] UPDATE (Cập nhật tên & trạng thái): ");
            Category toUpdate = cateService.findById(insertedId);
            if (toUpdate != null) {
                String updatedName = "Iphone 16 Pro Max Updated";
                toUpdate.setCategoryname(updatedName);
                toUpdate.setStatus(0);
                cateService.update(toUpdate);

                Category afterUpdate = cateService.findById(insertedId);
                if (afterUpdate != null && afterUpdate.getCategoryname().equals(updatedName) && afterUpdate.getStatus() == 0) {
                    System.out.println("THÀNH CÔNG! (Tên mới: " + afterUpdate.getCategoryname() + ", Status: " + afterUpdate.getStatus() + ")");
                    passed++;
                } else {
                    System.out.println("THẤT BẠI!");
                    failed++;
                }
            } else {
                System.out.println("THẤT BẠI (Không tìm thấy bản ghi để sửa)!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 8: COUNT
        try {
            System.out.print("[TEST 08] COUNT (Đếm tổng số Category): ");
            int count = cateService.count();
            if (count > 0) {
                System.out.println("THÀNH CÔNG! (Tổng số: " + count + ")");
                passed++;
            } else {
                System.out.println("THẤT BẠI!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 9: PAGINATION
        try {
            System.out.print("[TEST 09] PAGINATION (Phân trang page 0, size 2): ");
            List<Category> pageList = cateService.findAll(0, 2);
            if (pageList != null && pageList.size() <= 2) {
                System.out.println("THÀNH CÔNG! (Lấy " + pageList.size() + " phần tử trang đầu)");
                passed++;
            } else {
                System.out.println("THẤT BẠI!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 10: VALIDATION (@NotEmpty)
        try {
            System.out.print("[TEST 10] VALIDATION (Chặn tên rỗng): ");
            try {
                Category invalid = new Category("", "test.jpg", 1);
                cateService.insert(invalid);
                System.out.println("THẤT BẠI (Không chặn được tên rỗng)!");
                failed++;
            } catch (IllegalArgumentException expected) {
                System.out.println("THÀNH CÔNG! (Đã bắt lỗi hợp lệ: \"" + expected.getMessage() + "\")");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI NGOÀI DỰ KIẾN: " + e.getMessage());
            failed++;
        }

        // TC 11: UPLOAD DIRECTORY
        try {
            System.out.print("[TEST 11] UPLOAD DIR CONFIG (Kiểm tra đường dẫn upload): ");
            File uploadDir = new File(Constant.DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            if (uploadDir.exists() && uploadDir.isDirectory()) {
                System.out.println("THÀNH CÔNG! (" + uploadDir.getAbsolutePath() + ")");
                passed++;
            } else {
                System.out.println("THẤT BẠI!");
                failed++;
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        }

        // TC 12: DELETE
        try {
            System.out.print("[TEST 12] DELETE (Xóa Category đã tạo thử nghiệm): ");
            if (insertedId > 0) {
                cateService.delete(insertedId);
                Category afterDelete = cateService.findById(insertedId);
                if (afterDelete == null) {
                    System.out.println("THÀNH CÔNG! (Đã xóa sạch khỏi database)");
                    passed++;
                } else {
                    System.out.println("THẤT BẠI! (Bản ghi vẫn tồn tại sau khi xóa)");
                    failed++;
                }
            } else {
                System.out.println("BỎ QUA (Không có ID hợp lệ để xóa)");
            }
        } catch (Exception e) {
            System.out.println("LỖI: " + e.getMessage());
            failed++;
        } finally {
            JPAConfig.close();
        }

        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.printf("TỔNG KẾT KẾT QUẢ KIỂM THỬ: %d/12 TIÊU CHÍ ĐẠT (%s)%n", 
                passed, (failed == 0 ? "HOÀN HẢO 100%" : failed + " LỖI CẦN XỬ LÝ"));
        System.out.println("================================================================================\n");
    }
}
