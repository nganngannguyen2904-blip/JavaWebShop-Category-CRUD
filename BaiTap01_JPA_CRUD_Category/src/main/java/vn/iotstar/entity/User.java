package vn.iotstar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity đại diện cho bảng [User] trong cơ sở dữ liệu
 * Tương thích chuẩn JPA 3.0 / Hibernate ORM
 */
@Entity
@Table(name = "[User]")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotBlank(message = "Họ và tên không được để trống")
    @Column(name = "fullname", columnDefinition = "NVARCHAR(200)")
    private String fullname;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "roleid")
    private int roleid = 5; // 1: Admin, 2: Manager, 5: Normal User

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "status")
    private int status = 1; // 1: Hoạt động, 0: Đang khóa / Chưa kích hoạt

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createddate")
    private Date createdDate;

    // Quan hệ 1-N: Một User có nhiều Video
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Video> videos;

    public User() {
        this.status = 1;
        this.roleid = 5;
        this.createdDate = new Date();
    }

    public User(String fullname, String email, String password) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.status = 1;
        this.roleid = 5;
        this.createdDate = new Date();
    }

    public User(int id, String fullname, String email, String password, String avatar, int roleid, String phone, int status, Date createdDate) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.roleid = roleid;
        this.phone = phone;
        this.status = status;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public java.util.List<Video> getVideos() {
        return videos;
    }

    public void setVideos(java.util.List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullname='" + fullname + '\'' +
                ", email='" + email + '\'' +
                ", roleid=" + roleid +
                ", status=" + status +
                ", createdDate=" + createdDate +
                '}';
    }
}
