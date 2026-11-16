package vn.iotstar.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model đại diện cho bảng Videos trong cơ sở dữ liệu
 * Thiết lập quan hệ N-1 với Model User (Mỗi Video thuộc về một User)
 */
@SuppressWarnings("serial")
public class Video implements Serializable {

    private String videoId;
    private String title;
    private String description;
    private String videoUrl;
    private String poster;
    private int views;
    private int active;
    private Timestamp uploadDate;
    private int userId;

    // Các trường hỗ trợ hiển thị thông tin User đăng tải
    private String userFullName;
    private String userUsername;

    public Video() {
        this.active = 1;
        this.views = 0;
        this.uploadDate = new Timestamp(System.currentTimeMillis());
    }

    public Video(String videoId, String title, String description, String videoUrl, 
                 String poster, int views, int active, Timestamp uploadDate, int userId) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.poster = poster;
        this.views = views;
        this.active = active;
        this.uploadDate = uploadDate;
        this.userId = userId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Timestamp getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Timestamp uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    @Override
    public String toString() {
        return "Video [videoId=" + videoId + ", title=" + title + ", views=" + views + ", userId=" + userId + "]";
    }
}
