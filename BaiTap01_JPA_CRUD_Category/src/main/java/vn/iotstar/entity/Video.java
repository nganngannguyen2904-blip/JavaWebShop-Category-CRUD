package vn.iotstar.entity;

import java.io.Serializable;

import jakarta.persistence.*;

/**
 * Entity đại diện cho bảng Videos trong cơ sở dữ liệu
 * Ánh xạ quan hệ Many-to-One hai chiều với entity Category
 */
@Entity
@Table(name = "Videos")
@NamedQuery(name = "Video.findAll", query = "SELECT v FROM Video v")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "VideoId", length = 50)
    private String videoId;

    @Column(name = "Active")
    private int active;

    @Column(name = "Description", columnDefinition = "nvarchar(500) null")
    private String description;

    @Column(name = "Poster", columnDefinition = "nvarchar(500) null")
    private String poster;

    @Column(name = "Title", columnDefinition = "nvarchar(500) null")
    private String title;

    @Column(name = "Views")
    private int views;

    @Column(name = "VideoUrl", columnDefinition = "nvarchar(255) null")
    private String videoUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UploadDate")
    private java.util.Date uploadDate;

    // Quan hệ Many-to-One với User (Khóa ngoại UserId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId")
    private User user;

    // Quan hệ Many-to-One với Category (Khóa ngoại CategoryId)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryId")
    private Category category;

    // ================= Constructors =================
    public Video() {
        super();
    }

    public Video(String videoId, String title, String poster, String description, int active, int views, Category category) {
        this.videoId = videoId;
        this.title = title;
        this.poster = poster;
        this.description = description;
        this.active = active;
        this.views = views;
        this.category = category;
    }

    // ================= Getters & Setters =================
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public java.util.Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(java.util.Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Video [videoId=" + videoId + ", title=" + title + ", active=" + active + ", views=" + views + "]";
    }
}
