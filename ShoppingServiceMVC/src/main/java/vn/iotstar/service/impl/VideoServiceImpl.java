package vn.iotstar.service.impl;

import java.io.File;
import java.util.List;
import vn.iotstar.dao.VideoDao;
import vn.iotstar.dao.impl.VideoDaoImpl;
import vn.iotstar.model.Video;
import vn.iotstar.service.VideoService;
import vn.iotstar.util.Constant;

/**
 * Lớp triển khai VideoService
 * Quản lý logic nghiệp vụ, phân quyền sở hữu video và dọn dẹp file vật lý
 */
public class VideoServiceImpl implements VideoService {

    private final VideoDao videoDao = new VideoDaoImpl();

    @Override
    public void insert(Video video) {
        if (video == null) return;
        videoDao.insert(video);
    }

    @Override
    public void update(Video video) {
        if (video == null) return;
        videoDao.update(video);
    }

    @Override
    public boolean delete(String videoId, int currentUserId, boolean isAdmin) {
        Video video = videoDao.findById(videoId);
        if (video == null) {
            return false;
        }

        // Kiểm tra quyền: Chỉ chính chủ hoặc Admin/Manager mới được xóa
        if (!isAdmin && video.getUserId() != currentUserId) {
            return false;
        }

        // Xóa file video vật lý trên đĩa để tránh để lại file rác
        deletePhysicalFile(video.getVideoUrl(), "video");

        // Xóa file poster nếu có và không phải ảnh hệ thống mặc định
        if (video.getPoster() != null && !video.getPoster().startsWith("cat_") && !video.getPoster().startsWith("sp_") && !video.getPoster().equals("default.png")) {
            deletePhysicalFile(video.getPoster(), "video");
            deletePhysicalFile(video.getPoster(), "category");
        }

        videoDao.delete(videoId);
        return true;
    }

    private void deletePhysicalFile(String fileName, String subFolder) {
        if (fileName == null || fileName.trim().isEmpty()) return;
        try {
            File targetFile = new File(Constant.UPLOAD_DIR + File.separator + subFolder + File.separator + fileName);
            if (targetFile.exists() && targetFile.isFile()) {
                boolean deleted = targetFile.delete();
                if (deleted) {
                    System.out.println("[VideoService] Đã dọn dẹp file rác: " + targetFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("[VideoService] Không thể xóa file: " + fileName + " - " + e.getMessage());
        }
    }

    @Override
    public Video findById(String videoId) {
        if (videoId == null || videoId.trim().isEmpty()) return null;
        return videoDao.findById(videoId.trim());
    }

    @Override
    public List<Video> findAll() {
        return videoDao.findAll();
    }

    @Override
    public List<Video> getTop10Latest() {
        return videoDao.findTop10Latest();
    }

    @Override
    public List<Video> getByUserId(int userId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 6;
        return videoDao.findByUserId(userId, page, pageSize);
    }

    @Override
    public int countByUserId(int userId) {
        return videoDao.countByUserId(userId);
    }

    @Override
    public List<Video> getAllPaged(int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 6;
        return videoDao.findAllPaged(page, pageSize);
    }

    @Override
    public int countAll() {
        return videoDao.countAll();
    }

    @Override
    public void increaseViews(String videoId) {
        if (videoId == null || videoId.trim().isEmpty()) return;
        videoDao.increaseViews(videoId.trim());
    }

    @Override
    public List<Video> searchAndFilter(int userId, boolean isAdmin, String keyword, String sort, Integer productId, int page, int pageSize) {
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 6;
        return videoDao.searchAndFilter(userId, isAdmin, keyword, sort, productId, page, pageSize);
    }

    @Override
    public int countSearchAndFilter(int userId, boolean isAdmin, String keyword, Integer productId) {
        return videoDao.countSearchAndFilter(userId, isAdmin, keyword, productId);
    }
}
