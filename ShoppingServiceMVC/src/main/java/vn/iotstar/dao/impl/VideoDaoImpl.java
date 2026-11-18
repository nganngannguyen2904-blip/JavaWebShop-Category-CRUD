package vn.iotstar.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import vn.iotstar.connection.DBConnection;
import vn.iotstar.dao.VideoDao;
import vn.iotstar.model.Video;

/**
 * Lớp triển khai VideoDao sử dụng JDBC kết nối SQL Server
 * Hỗ trợ liên kết Product, Category thời trang, tìm kiếm & bộ lọc sắp xếp
 */
public class VideoDaoImpl implements VideoDao {

    private final DBConnection db = new DBConnection();

    private static final String BASE_SELECT = 
        "SELECT v.*, u.fullname, u.username, " +
        "p.product_name, p.price AS product_price, p.image AS product_image, c.cate_name AS product_category " +
        "FROM dbo.Videos v " +
        "LEFT JOIN dbo.[User] u ON v.UserId = u.id " +
        "LEFT JOIN dbo.Product p ON v.ProductId = p.product_id " +
        "LEFT JOIN dbo.Category c ON p.cate_id = c.cate_id ";

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

        // Lấy ProductId nếu có
        try {
            int pId = rs.getInt("ProductId");
            if (!rs.wasNull() && pId > 0) {
                video.setProductId(pId);
            }
        } catch (Exception ignored) {
        }

        // Lấy thông tin user đăng tải
        try {
            video.setUserFullName(rs.getString("fullname"));
        } catch (Exception ignored) {
        }
        try {
            video.setUserUsername(rs.getString("username"));
        } catch (Exception ignored) {
        }

        // Lấy thông tin sản phẩm thời trang liên kết
        try {
            video.setProductName(rs.getString("product_name"));
        } catch (Exception ignored) {
        }
        try {
            video.setProductPrice(rs.getBigDecimal("product_price"));
        } catch (Exception ignored) {
        }
        try {
            video.setProductImage(rs.getString("product_image"));
        } catch (Exception ignored) {
        }
        try {
            video.setProductCategory(rs.getString("product_category"));
        } catch (Exception ignored) {
        }

        return video;
    }

    @Override
    public void insert(Video video) {
        String sql = "INSERT INTO dbo.Videos (VideoId, Title, Description, VideoUrl, Poster, Views, Active, UploadDate, UserId, ProductId) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            if (video.getProductId() != null && video.getProductId() > 0) {
                ps.setInt(10, video.getProductId());
            } else {
                ps.setNull(10, Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Video video) {
        String sql = "UPDATE dbo.Videos SET Title = ?, Description = ?, VideoUrl = ?, Poster = ?, Active = ?, ProductId = ? " +
                     "WHERE VideoId = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, video.getTitle());
            ps.setString(2, video.getDescription());
            ps.setString(3, video.getVideoUrl());
            ps.setString(4, video.getPoster());
            ps.setInt(5, video.getActive());
            if (video.getProductId() != null && video.getProductId() > 0) {
                ps.setInt(6, video.getProductId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, video.getVideoId());
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
        String sql = BASE_SELECT + "WHERE v.VideoId = ?";
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
        String sql = BASE_SELECT + "ORDER BY v.UploadDate DESC";
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
        String sql = "SELECT TOP 10 v.*, u.fullname, u.username, " +
                     "p.product_name, p.price AS product_price, p.image AS product_image, c.cate_name AS product_category " +
                     "FROM dbo.Videos v " +
                     "LEFT JOIN dbo.[User] u ON v.UserId = u.id " +
                     "LEFT JOIN dbo.Product p ON v.ProductId = p.product_id " +
                     "LEFT JOIN dbo.Category c ON p.cate_id = c.cate_id " +
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
        String sql = BASE_SELECT +
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
        String sql = BASE_SELECT +
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

    @Override
    public List<Video> searchAndFilter(int userId, boolean isAdmin, String keyword, String sort, Integer productId, int page, int pageSize) {
        List<Video> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (!isAdmin) {
            sql.append("AND v.UserId = ? ");
            params.add(userId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (v.Title LIKE ? OR v.Description LIKE ? OR p.product_name LIKE ?) ");
            String kwParam = "%" + keyword.trim() + "%";
            params.add(kwParam);
            params.add(kwParam);
            params.add(kwParam);
        }

        if (productId != null && productId > 0) {
            sql.append("AND v.ProductId = ? ");
            params.add(productId);
        }

        // Sắp xếp
        if ("oldest".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY v.UploadDate ASC ");
        } else if ("views".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY v.Views DESC, v.UploadDate DESC ");
        } else if ("title".equalsIgnoreCase(sort)) {
            sql.append("ORDER BY v.Title ASC ");
        } else {
            // Mặc định newest
            sql.append("ORDER BY v.UploadDate DESC ");
        }

        int offset = (page - 1) * pageSize;
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(pageSize);

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
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
    public int countSearchAndFilter(int userId, boolean isAdmin, String keyword, Integer productId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM dbo.Videos v LEFT JOIN dbo.Product p ON v.ProductId = p.product_id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (!isAdmin) {
            sql.append("AND v.UserId = ? ");
            params.add(userId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (v.Title LIKE ? OR v.Description LIKE ? OR p.product_name LIKE ?) ");
            String kwParam = "%" + keyword.trim() + "%";
            params.add(kwParam);
            params.add(kwParam);
            params.add(kwParam);
        }

        if (productId != null && productId > 0) {
            sql.append("AND v.ProductId = ? ");
            params.add(productId);
        }

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
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
}
