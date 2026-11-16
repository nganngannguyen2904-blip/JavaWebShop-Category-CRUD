package vn.iotstar.dao;

import java.util.List;
import vn.iotstar.model.Video;

/**
 * Interface DAO cho các thao tác dữ liệu bảng Videos
 */
public interface VideoDao {

    void insert(Video video);

    void update(Video video);

    void delete(String videoId);

    Video findById(String videoId);

    List<Video> findAll();

    List<Video> findTop10Latest();

    List<Video> findByUserId(int userId, int page, int pageSize);

    int countByUserId(int userId);

    List<Video> findAllPaged(int page, int pageSize);

    int countAll();

    void increaseViews(String videoId);
}
