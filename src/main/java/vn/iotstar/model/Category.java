package vn.iotstar.model;

import java.io.Serializable;

/**
 * Model đại diện cho bảng Category trong database
 * Bảng: Category (cate_id, cate_name, icons)
 */
@SuppressWarnings("serial")
public class Category implements Serializable {

    private int id;
    private String name;
    private String icon;

    // ===================== Constructors =====================

    public Category() {
        super();
    }

    public Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public Category(int id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    // ===================== Getters & Setters =====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Alias cho tương thích với code trong PDF
    public int getCateid() { return id; }
    public void setCateid(int id) { this.id = id; }
    public int getCateId() { return id; }
    public void setCateId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Alias
    public String getCatename() { return name; }
    public void setCatename(String name) { this.name = name; }
    public String getCateName() { return name; }
    public void setCateName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "', icon='" + icon + "'}";
    }
}
