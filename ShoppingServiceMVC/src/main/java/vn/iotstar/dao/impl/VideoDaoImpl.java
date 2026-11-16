package vn.iotstar.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.VideoDao;
import vn.iotstar.model.Video;

/**
 * Lớp triển khai VideoDao sử dụng JDBC kết nối SQL Server
 */
public class VideoDaoImpl implements VideoDao {

    private final DBConnection db = new DBConnection();

    private Video mapResultSetToVideo(ResultSet rs) throws Exception {
        Video video = new Video();
        video.setVideoId(rs.getString("VideoId"));
        video.setTitle(rs.getString("Title"));
        video.setDescription(rs.getString("Description"));
        video.setVideoUrl(rs.getString("VideoUrl"));
        video.setPoster(rs.getString("Poster"));
        video.setViews(rs.getInt("Views"));
        video.setActive(rs.getInt("Active"));
        video.setUploadDate(rs.getTimestamp("UploadDate"));
        video.setUserId(rs.getInt("UserId"));

        // Lấy thông tin user nếu có cột JOIN
        try {
            video.setUserFullName(rs.getString("fullname"));
        } catch (Exception ignored) {
        }
        try {
            video.setUserUsername(rs.getString("username"));
        } catch (Exception ignored) {
        }

        return video;
    }

    @Override
    public void insert(Video video) {
        String sql = "INSERT INTO dbo.Videos (VideoId, Title, Description, VideoUrl, Poster, Views, Active, UploadDate, UserId) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, video.getVideoId());
            ps.setString(2, video.getTitle());
            ps.setString(3, video.getDescription());
            ps.setString(4, video.getVideoUrl());
            ps.setString(5, video.getPoster());
            ps.setInt(6, video.getViews());
            ps.setInt(7, video.getActive());
            ps.setTimestamp(8, video.getUploadDate() != null ? video.getUploadDate() : new Timestamp(System.currentTimeMillis()));
            ps.setInt(9, video.getUserId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Video video) {
        String sql = "UPDATE dbo.Videos SET Title = ?, Description = ?, VideoUrl = ?, Poster = ?, Active = ? " +
                     "WHERE VideoId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, video.getTitle());
            ps.setString(2, video.getDescription());
            ps.setString(3, video.getVideoUrl());
            ps.setString(4, video.getPoster());
            ps.setInt(5, video.getActive());
            ps.setString(6, video.getVideoId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String videoId) {
        String sql = "DELETE FROM dbo.Videos WHERE VideoId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, videoId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Video findById(String videoId) {
        String sql = "SELECT v.*, u.fullname, u.username " +
                     "FROM dbo.Videos v " +
                     "LEFT JOIN dbo.[User] u ON v.UserId = u.id " +
                     "WHERE v.VideoId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, videoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVideo(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Video> findAll() {
        List<Video> list = new ArrayList<>();
        String sql = "SELECT v.*, u.fullname, u.username " +
                     "FROM dbo.Videos v " +
                     "LEFT JOIN dbo.[User] u ON v.UserId = u.id " +
                     "ORDER BY v.UploadDate DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToVideo(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Video> findTop10Latest() {
        List<Video> list = new ArrayList<>();
        String sql = "SELECT TOP 10 v.*, u.fullname, u.username " +
                     "FROM dbo.Videos v " +
                     "LEFT JOIN dbo.[User] u ON v.UserId = u.id " +
                     "WHERE v.Active = 1 " +
                     "ORDER BY v.UploadDate DESC";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToVideo(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Video> findByUserId(int userId, int page, int pageSize) {
        List<Video> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT v.*, u.fullname, u.username " +
                     "FROM dbo.Videos v " +
                     "LEFT JOIN dbo.[User] u ON v.UserId = u.id " +
                     "WHERE v.UserId = ? " +
                     "ORDER BY v.UploadDate DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToVideo(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM dbo.Videos WHERE UserId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Video> findAllPaged(int page, int pageSize) {
        List<Video> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        String sql = "SELECT v.*, u.fullname, u.username " +
                     "FROM dbo.Videos v " +
                     "LEFT JOIN dbo.[User] u ON v.UserId = u.id " +
                     "ORDER BY v.UploadDate DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToVideo(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM dbo.Videos";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void increaseViews(String videoId) {
        String sql = "UPDATE dbo.Videos SET Views = Views + 1 WHERE VideoId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, videoId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
