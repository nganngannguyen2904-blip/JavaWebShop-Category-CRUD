package vn.iotstar.service;

import java.util.List;
import vn.iotstar.model.Video;

/**
 * Interface Service tầng nghiệp vụ cho chức năng Quản lý Video
 */
public interface VideoService {

    void insert(Video video);

    void update(Video video);

    boolean delete(String videoId, int currentUserId, boolean isAdmin);

    Video findById(String videoId);

    List<Video> findAll();

    List<Video> getTop10Latest();

    List<Video> getByUserId(int userId, int page, int pageSize);

    int countByUserId(int userId);

    List<Video> getAllPaged(int page, int pageSize);

    int countAll();

    void increaseViews(String videoId);

    List<Video> searchAndFilter(int userId, boolean isAdmin, String keyword, String sort, Integer productId, int page, int pageSize);

    int countSearchAndFilter(int userId, boolean isAdmin, String keyword, Integer productId);
}
