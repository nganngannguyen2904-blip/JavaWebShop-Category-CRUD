package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;

/**
 * Class Test khởi tạo và kiểm thử cơ bản Entity Category và Video theo đúng Section 5 tài liệu mẫu
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("=== KIỂM THỬ KHỞI TẠO VÀ LƯU DỮ LIỆU JPA CƠ BẢN ===");
        EntityManager enma = JPAConfig.getEntityManager();
        EntityTransaction trans = enma.getTransaction();
        
        Category cate = new Category();
        cate.setCategoryname("Iphone");
        cate.setImages("abc.jpg");
        cate.setStatus(1);
        
        Video video = new Video();
        video.setVideoId("v01");
        video.setTitle("test video jpa");
        video.setActive(1);
        video.setViews(100);
        video.setCategory(cate);
        cate.addVideo(video);

        try {
            trans.begin();
            // Nếu videoId "v01" đã tồn tại từ lần test trước, dọn dẹp để tránh lỗi duplicate key
            Video oldVideo = enma.find(Video.class, "v01");
            if (oldVideo != null) {
                enma.remove(oldVideo);
                enma.flush();
            }
            enma.persist(cate);
            enma.persist(video);
            trans.commit();
            System.out.println("[+] Đã lưu Category (Iphone) và Video (v01) thành công vào cơ sở dữ liệu!");
        } catch (Exception e) {
            System.err.println("[-] Lỗi khi lưu dữ liệu: " + e.getMessage());
            if (trans.isActive()) {
                trans.rollback();
            }
            e.printStackTrace();
        } finally {
            enma.close();
            JPAConfig.close();
        }
    }
}
